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
        for (String key : fpGrowth.getFreqItemSetReversed().keySet()) {
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
            System.out.println();

            LinkedHashMap<ArrayList<FPNode>, Integer> paths = conditonalPatternBase.get(key);

            // Code to Print the paths
            System.out.println("Item: " + key + " Support: " + fpGrowth.getFreqItemSetReversed().get(key) + "\n");
            for (ArrayList<FPNode> path : paths.keySet()) {
                StringBuilder sb = new StringBuilder(path.toString() + " <-- " + key + "/" + paths.get(path));
                sb.replace(0, 1, "[(Root),");
                System.out.println(sb);
            }
            System.out.println();

            //Synced ArrayList Following
            ArrayList<String> itemList = new ArrayList<>();
            ArrayList<Integer> valueList = new ArrayList<>();
            for (ArrayList<FPNode> path : paths.keySet()) {
                for (FPNode n : path) {
                    String sub = n.toString().substring(1, n.toString().indexOf("|"));
                    itemList.add(sub);
                    valueList.add(paths.get(path));
                }
            }
            System.out.println("ItemList: " + itemList);

            HashSet<String> set = new HashSet<>();
            set.addAll(itemList);

            System.out.println("Item Set: " + set);
            System.out.println();


            for (String item : set) {
                System.out.println("Item: " + item + " -> Freq: " + Collections.frequency(itemList, item));
            }

            System.out.println();
            LinkedHashMap<String, Integer> conditionalFP = new LinkedHashMap<>();

            for (String setItem : set) {
                int x = 0;
                for (int i = 0; i < itemList.size(); i++) {
                    if (setItem.equals(itemList.get(i))) {
                        if (!conditionalFP.containsKey(itemList.get(i))) {
                            x++;
                            System.out.println("Index: " + i + " Value: " + itemList.get(i) + " Int Value: " + valueList.get(i));
                            conditionalFP.put(itemList.get(i), valueList.get(i));
                            System.out.println(itemList.get(i) + " count: " + x);
                        } else {
                            x++;
                            System.out.println("Index: " + i + " Value: " + itemList.get(i) + " Int Value: " + valueList.get(i));
                            conditionalFP.put(itemList.get(i), conditionalFP.get(itemList.get(i)) + valueList.get(i));
                            System.out.println(itemList.get(i) + " count: " + x);
                        }

                    }
                }
                conditionalFPTreeMap.put(key, conditionalFP);
                System.out.println("===========================================");
            }
        }
    }

}


