import java.io.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
@author  Miguel Cruz (mcruz@mcruz.me)

 Purpose of this class is to just facilitate the distribution of attributes from the
 grocery list CSV file into sensible data structures
 */



public class TransactionCSVParser {
    private ArrayList<Integer> memberidList;
    private ArrayList<String> dateList, itemList;
    private TreeSet<String> dateSet, itemSet;

    private ArrayList<TransactionLog> transactionLogList;
    private Hashtable<String, Integer> itemFrequency;
    private ArrayList<Transaction> transactionList;

    public TransactionCSVParser() throws IOException {
        dateList = new ArrayList<>();
        dateSet = new TreeSet<String>(new Comparator<String>() {
            @Override
            // A comparator is made here purely for stylistic purposes when debugging and printing to std output
            public int compare(String o1, String o2) {
                 Date a = new SimpleDateFormat("dd-MM-yyyy").parse(o1, new ParsePosition(0));
                 Date b = new SimpleDateFormat("dd-MM-yyyy").parse(o2, new ParsePosition(0));
                // Define comparing logic here
                return a.compareTo(b);
            }
        });

        memberidList = new ArrayList<>();
        itemList = new ArrayList<>();
        itemSet = new TreeSet<>();
        itemFrequency = new Hashtable<>();
        transactionLogList = new ArrayList<>();
        transactionList = new ArrayList<>();

        dumpCSV();
        buildTransactions();
        buildTransactionList();
        buildItemFrequencyTable();

    }
    private void dumpCSV() throws IOException {
        String path = "Groceries_dataset.csv";
        File f = new File(path);
        String line = "";
        System.out.println(f.getAbsolutePath());
        BufferedReader br = new BufferedReader(new FileReader(path));
        while ((line = br.readLine()) != null) {
            if (!line.contains("Member_number") || !line.contains("itemDescription")) {
                String pattern = "^(\".*?\"|.*?),(\".*?\"|.*?),(\".*?\"|.*?)$";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(line);
                m.find();

                int memberNumber = Integer.parseInt(m.group(1));
                String date = m.group(2);
                String item = m.group(3);
                memberidList.add(memberNumber);

                dateList.add(date);
                dateSet.add(date);

                itemList.add(item);
                itemSet.add(item);
            }
        }
        br.close();
    }

    private void buildTransactions() {
        int nrOfDates = dateList.size();
        // Iterate Over Unique Dates
        for (String date : dateSet) {
            TransactionLog transLog = new TransactionLog(date);

            /* (Iterate Over the the length of the item list and find indices that match the date we're targeting)
             nrOfItems, memberIDList, and dateList has the same length
             We can see which member id and item correspond to a date by iterating over a "set" of dates
             and observing which memberID/item pairs, match the date we're looking for
             Since dateSet is in order the items corresponding to earlier dates is put into the transactionLogList first
             */
            for (int i = 0; i < nrOfDates ; i++) {
                if (date.equalsIgnoreCase(dateList.get(i))) {
                    transLog.addItem(memberidList.get(i), itemList.get(i));
                }
            }
            transactionLogList.add(transLog);
        }
    }

    private void buildItemFrequencyTable(){
        for (String item: itemSet) {
            itemFrequency.put(item, Collections.frequency(itemList, item));
        }
    }

    private void buildTransactionList(){
        for (TransactionLog tLog : transactionLogList) {
            for (Transaction tr : tLog.getTransactionMap().values()) {
                transactionList.add(tr);
            }
        }

        //Ensures transactions are sorted in the hashmap by date
        Collections.sort(transactionList);
    }

    // Getters
    public ArrayList<Integer> getMemberIDList() {
        return memberidList;
    }

    public ArrayList<String> getDateList() {
        return dateList;
    }

    public ArrayList<String> getItemList() {
        return itemList;
    }

    public TreeSet<String> getItemSet() {
        return itemSet;
    }

    public TreeSet<String> getDateSet() {
        return dateSet;
    }

    // Returns a transaction log for each date containing a list of transactions mapped to a member id
    public ArrayList<TransactionLog> getTransactionLogList() {
        return transactionLogList;
    }

    public ArrayList<Transaction> getTransactionList() {
        return transactionList;
    }

    public Hashtable<String, Integer> getItemFrequencyList() {
        return itemFrequency;
    }
}
