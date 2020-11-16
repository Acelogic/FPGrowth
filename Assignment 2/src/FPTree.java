import com.sun.source.tree.Tree;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class FPTree {


    private LinkedHashMap<String, ArrayList<FPNode>> uniquePaths;
    private LinkedHashMap<String, LinkedHashMap<ArrayList<FPNode>, Integer>> conditonalPatternBase;
    private LinkedHashMap<String, LinkedHashMap<String, Integer>> conditionalFPTreeMap;
    private FPGrowth fpGrowth;
    private FPNode root;
    private int supportThreshold;

    public FPTree(int supportThreshold) throws IOException {
        this.supportThreshold = supportThreshold;
        fpGrowth = new FPGrowth(supportThreshold);
        uniquePaths = new LinkedHashMap<>();
        conditonalPatternBase = new LinkedHashMap<>();
        conditionalFPTreeMap = new LinkedHashMap<>();
        root = new FPNode(null);
    }

    public void insertAll() {
        //Deep copy transaction list to new Array
        ArrayList<Transaction> transactionListCopy = new ArrayList<>();
        for (Transaction tr : fpGrowth.getOptimizedTransactionsList()) {
            transactionListCopy.add((Transaction) tr.clone());
        }
        for (Transaction transaction : transactionListCopy) {
            addEntity(getHead(), transaction);
        }
    }


    private boolean addEntity(FPNode loci, ArrayList<String> transaction) {
        if (transaction.isEmpty()) {
            return true;
        }

        String insertWord = transaction.get(0);
        transaction.remove(0);

        FPNode child = null;
        for (FPNode x : loci.children) {
            if (x.item.equals(insertWord)) {
                child = x;
                break;
            }
        }
        if (child == null) {
            child = new FPNode(insertWord);
            child.head = loci;
            loci.children.add(child);
            ArrayList<FPNode> path = uniquePaths.get(insertWord);
            if (path == null) {
                path = new ArrayList();
            }
            path.add(child);
            uniquePaths.put(insertWord, path);
        } else {
            child.freq++;
        }

        return addEntity(child, transaction);
    }


    public FPNode getHead() {
        return root;
    }


    public void generateConditionalPatternBase() {
        for (String key : fpGrowth.getFListReversed().keySet()) {
            int x = 1;
            // System.out.println();
            // System.out.println("Item: " + key + " Support: " + fpGrowth.getFreqItemSetReversed().get(key));
            // System.out.println("=============================================================");
            for (FPNode leaf : uniquePaths.get(key)) {
                if (!(leaf.head.item == null)) {
                    ArrayList<FPNode> nodelink = leaf.getHeadLinks();
                    ArrayList<FPNode> nodeLinkCpy = (ArrayList<FPNode>) nodelink.clone();
                    FPNode lastLeaf = nodeLinkCpy.get(nodeLinkCpy.size() - 1);

                    // removes item from it's own path
                    nodeLinkCpy.removeIf(n -> n.item.equals(key));

                    //System.out.println("Num:" + x + " " + nodeLinkCpy + " <-- (" + key + "|" + leaf.freq + ")");
                    conditonalPatternBase.computeIfAbsent(key, k -> new LinkedHashMap<ArrayList<FPNode>, Integer>());
                    conditonalPatternBase.get(key).put(nodeLinkCpy, lastLeaf.freq);

                    // Sorting Items By Value rather than key (Descending order)
                    LinkedHashMap<ArrayList<FPNode>, Integer> sortedTemp = conditonalPatternBase.get(key).entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    conditonalPatternBase.put(key, sortedTemp);
                    leaf.getHeadLinks().clear();
                    x++;
                } else {
                    // System.out.println("Single Node Path Freq: " + leaf.freq);
                }

            }
        }
    }

    public void generateConditionalFP() {
        // Iterating over the word <-> support list in reverse
        for (String key : conditonalPatternBase.keySet()) {
            LinkedHashMap<ArrayList<FPNode>, Integer> paths = conditonalPatternBase.get(key);

            // Code to Print the paths
   /*         System.out.println("Item: " + key + " Support: " + fpGrowth.getFListReversed().get(key) + "\n");
            System.out.println("\t\t\t\t\t(Paths)\n");
            for (ArrayList<FPNode> path : paths.keySet()) {
                StringBuilder sb = new StringBuilder(path.toString() + " <-- " + key + "/" + paths.get(path));
                sb.replace(0, 1, "[(Root),");
                System.out.println(sb);
            }
            System.out.println();*/

            //Synced ArrayLists needed for getting words and associated weights
            ArrayList<String> allPathItemList = new ArrayList<>();
            ArrayList<Integer> allPathValueList = new ArrayList<>();
            for (ArrayList<FPNode> path : paths.keySet()) {
                for (FPNode node : path) {
                    String sub = node.toString().substring(1, node.toString().indexOf("|"));
                    allPathItemList.add(sub);
                    allPathValueList.add(paths.get(path));
                }
            }
           // System.out.println("ItemList: " + allPathItemList);

            HashSet<String> pathItemSet = new HashSet<>();
            pathItemSet.addAll(allPathItemList);
           // System.out.println("Item Set: " + pathItemSet);
           // System.out.println();


            // Making Conditional FP tree with frequency of items from conditional pattern base
            LinkedHashMap<String, Integer> conditionalFP = new LinkedHashMap<>();
            for (String item : pathItemSet) {
                for (int i = 0; i < allPathItemList.size(); i++) {

                    // curr for current
                    String currPathItem = allPathItemList.get(i);
                    int currPathValue = allPathValueList.get(i);

                    if (item.equals(currPathItem)) {
                        if (!conditionalFP.containsKey(currPathItem)) {
                            conditionalFP.put(currPathItem, currPathValue);
                        } else {
                            conditionalFP.put(currPathItem, conditionalFP.get(currPathItem) + currPathValue);
                        }
                    }
                }
            }
           // System.out.println("==============================================");
            // Pruning Items in Conditional FP that doesn't meet support count
            for (String keySet :List.copyOf(conditionalFP.keySet())) {
                if(conditionalFP.get(keySet) < supportThreshold) {
                    conditionalFP.remove(keySet);
                }
            }
            if(conditionalFP.size() != 0){
            conditionalFPTreeMap.put(key, conditionalFP);
            }
        }

/*        //Print Final Results
        System.out.println("Conditional FP Tree");
        for (String  fp: conditionalFPTreeMap.keySet()){
            System.out.println("Key: " + fp + "Value: " + conditionalFPTreeMap.get(fp));;
        }*/
    }

    public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    public void generateFrequentItemSetsAndRules(){
        LinkedHashMap<String, Set<String>> freqItemSet = new LinkedHashMap<>();
        for (String  fp: conditionalFPTreeMap.keySet()){
            // Confidence = (Sup X U Sup Y)/ num of transactions
            double Confidence = (fpGrowth.getfList().get(fp) + fpGrowth.getfList().get(conditionalFPTreeMap.get(fp).keySet().toArray()[0]))/(double) fpGrowth.getOptimizedTransactionsList().size();
            System.out.println("Confidence: "  + Confidence + " {"+fp+"}" + " ---> " + conditionalFPTreeMap.get(fp));
            ArrayList<Set> setList = new ArrayList<>();
            setList.add(powerSet(conditionalFPTreeMap.get(fp).keySet()));
            for (Set set: setList) {
                if (!set.isEmpty()) {
                    set.remove(set.iterator().next());
                }
                for (Object s :List.copyOf(set)) {
                    StringBuilder sb = new StringBuilder(s.toString());
                    sb.replace(0,1 , fp+",").replace(sb.indexOf("]"), sb.length(), "");
                    HashSet<String> temp = new HashSet<>();
                    temp.add(sb.toString());
                    set.remove(s);
                    set.add(temp);

                }
            }
            freqItemSet.put(fp , setList.get(0));
        }

    }


}


