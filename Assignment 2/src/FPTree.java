import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class FPTree {


    private LinkedHashMap<String, ArrayList<FPNode>> uniquePaths;
    private LinkedHashMap<String, LinkedHashMap<ArrayList<FPNode>, Integer>> conditonalPatternBase;
    private FPGrowth fpGrowth;
    private FPNode root;

    public FPTree(int supportThreshold) throws IOException {
        fpGrowth = new FPGrowth(supportThreshold);
        uniquePaths = new LinkedHashMap<>();
        conditonalPatternBase = new LinkedHashMap<>();
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


    public void generateConditionalPatternBase(int minsup) {
        for (String key : fpGrowth.getFreqItemSetReversed().keySet()) {
            int x = 1;
            //     System.out.println("Item: " + key + " Support: " + fpGrowth.getFreqItemSetReversed().get(key));
            //   System.out.println("=============================================================");
            for (FPNode leaf : uniquePaths.get(key)) {
                ArrayList<FPNode> nodelink = leaf.getHeadLinks();
                //   System.out.println("Num:" + x + " (" + key + "|" + leaf.freq + ")" + "-> " + nodelink);
                ArrayList<FPNode> temp = (ArrayList<FPNode>) nodelink.clone();
                FPNode lastLeaf = temp.get(temp.size()-1);


                if(true) {
                   // temp.removeIf(n -> n.item.equals(key));
                    temp.remove(temp.size()-1);
                    conditonalPatternBase.computeIfAbsent(key, k -> new LinkedHashMap<ArrayList<FPNode>, Integer>());
                    conditonalPatternBase.get(key).put(temp, lastLeaf.freq);

                    // Sorting Items By Value rather than key (Descending order)
                    LinkedHashMap<ArrayList<FPNode>, Integer> sortedTemp = conditonalPatternBase.get(key).entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                    conditonalPatternBase.put(key, (sortedTemp));
                }
                leaf.getHeadLinks().clear();
                //        x++;
            }
            //  System.out.println();
        }
    }


    public void generateConditionalFP() {
        for (String key : fpGrowth.getFreqItemSetReversed().keySet()) {
            System.out.println();
            System.out.println("Item: " + key + " Support: " + fpGrowth.getFreqItemSetReversed().get(key) + "\n");
            LinkedHashMap<ArrayList<FPNode>, Integer> paths = conditonalPatternBase.get(key);

            for (ArrayList<FPNode> lists : paths.keySet()) {
                StringBuilder sb = new StringBuilder(lists.toString() + " <-- " + key +"/" + paths.get(lists));
                sb.replace(0, 1, "[(Root),");
                System.out.println(sb);
            }
            System.out.println();

            ArrayList<String> itemList = new ArrayList<>();
            for (ArrayList<FPNode> nodeList : paths.keySet()) {
                for (FPNode n : nodeList) {
                    String sub = n.toString().substring(1, n.toString().indexOf("|"));
                    itemList.add(sub);
                }
            }
            System.out.println("ItemList: " + itemList);

            HashSet<String> set = new HashSet<>();
            set.addAll(itemList);
            System.out.println("Item Set: " + set);
            System.out.println();

            for (String item : set) {
                if (Collections.frequency(itemList, item) == paths.size()) {
                    System.out.println("Item: " + item + " -> Freq: " + Collections.frequency(itemList, item));
                    int sum = 0;
                    for (Integer i : paths.values()) {
                        sum += i;
                    }
                    System.out.println("{" + item + ":" + sum + "}");
                }
            }
            System.out.println("===========================================");

        }
    }
}


