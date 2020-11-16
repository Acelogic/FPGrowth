import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FPGrowth {

    private TransactionCSVParser csvParser;
    private LinkedHashMap<String, Integer> fList;
    private LinkedHashMap<String, Integer> fListReversed;
    private ArrayList<Transaction> transactionsList;
    private TreeSet<String> itemSet;
    public FPGrowth(int supportThreshold) throws IOException {
        csvParser = new TransactionCSVParser();
        transactionsList = csvParser.getTransactionList();
        itemSet = csvParser.getItemSet();
        fListReversed = new LinkedHashMap<>();

        buildFList(supportThreshold);
        reOrderedFList();
    }
    // A count of how much of each item is in each transaction (X being items, Y being support count)
    private void buildFList(int supportThreshold) throws IOException {
        TreeMap<String, Integer> tempSupportCountList = new TreeMap<>();

        // Build Unpruned freq item sets with support counts
        for (Transaction tr : transactionsList) {
            for (String item : itemSet) {
                if (tr.contains(item) && !tempSupportCountList.containsKey(item)) {
                    tempSupportCountList.put(item, 1);
                } else if (tr.contains(item) && tempSupportCountList.containsKey(item)) {
                    tempSupportCountList.put(item, tempSupportCountList.get(item) + 1);
                }
            }
        }

        // Prune Items below min support count
        for (String key : List.copyOf(tempSupportCountList.keySet())) {
            if (tempSupportCountList.get(key) < supportThreshold) {
                tempSupportCountList.remove(key);
            }
        }
        // Sorting keys based on value and reinserting them into a linkedhashmap to maintain insertion order
        // the .keySet() and .values() would be in order already when called
        fList = tempSupportCountList.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private void reOrderedFList() {
        for (Transaction transaction : transactionsList) {

            // Pruning All Transactions of items that didn't meet support min
            transaction.removeIf(item -> !fList.containsKey(item));

            // Convert Transaction Item list into LinkedHashMap, with values corresponding to freq item set
            LinkedHashMap<String, Integer> temp = new LinkedHashMap<>();
            for (String item : transaction) {
                temp.put(item, fList.get(item));
            }

            // Sorting Items By Value rather than key (Descending order)
            LinkedHashMap<String, Integer> sortedTemp = temp.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            // Wipes Unsorted item list
            transaction.clear();

            // Populates transaction item list based on sorted keyset from value sort
            transaction.addAll(sortedTemp.keySet());

        }

        // Clean up any empty transactions due to pruning
        for (Transaction transaction : List.copyOf(transactionsList)) {
            if (transaction.size() == 0) {
                transactionsList.remove(transaction);
            }
        }
    }

    public LinkedHashMap<String, Integer> getfList() {
        return fList;
    }

    public LinkedHashMap<String, Integer> getFListReversed() {
        List<String> list = new ArrayList<>(fList.keySet());
        Collections.reverse(list);
        for (String key : list) {
            fListReversed.put(key, fList.get(key));
        }

        return fListReversed;

    }

    public ArrayList<Transaction> getOptimizedTransactionsList() {
        return transactionsList;
    }

}
