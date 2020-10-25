import java.util.*;
import java.util.stream.Collectors;


public class FPGrowth {

    private TransactionCSVParser tp;
    private LinkedHashMap <String, Integer> supportCount;
    public FPGrowth(TransactionCSVParser tp){
        this.tp = tp;
        buildSupportCount();
        reorderTransactionItems();
    }

    // A count of how much of each item is in each transaction
    private void buildSupportCount() {
        TreeMap<String, Integer> supportCountTemp = new TreeMap<>();
        for (Transaction tr : tp.getTransactionList()) {
            for (String item : tp.getItemSet()) {
                if (tr.contains(item) && !supportCountTemp.containsKey(item)) {
                    supportCountTemp.put(item, 1);
                }
                else if(tr.contains(item) && supportCountTemp.containsKey(item)) {
                   supportCountTemp.put(item, supportCountTemp.get(item) + 1 );
                }
            }
        }
        // Sorting keys based on value and reinserting them into a linkedhashmap to maintain insertion order
        // the .keySet() and .values() would be in order already when called
        supportCount = supportCountTemp.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }


    private void reorderTransactionItems(){
        for (Transaction transaction : tp.getTransactionList()) {
            // Convert Transaction Item list into LinkedHashMap, with values corresponding to support count
            LinkedHashMap<String, Integer> temp = new LinkedHashMap<>();
            for (String item: transaction) {
                temp.put(item, supportCount.get(item));
            }

            // Sorting Items By Value rather than key (Descending order)
            LinkedHashMap<String, Integer> sortedTemp = temp.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            // Wipes Unsorted item list
            transaction.clear();

            // Populates transaction item list based on sorted keyset
            for (String item:sortedTemp.keySet()) {
                transaction.add(item);
            }

        }
    }

    public LinkedHashMap<String, Integer> getSupportCount() {
        return supportCount;
    }
}
