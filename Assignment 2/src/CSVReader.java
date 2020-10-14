import java.io.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
@author  Miguel Cruz (mcruz@mcruz.me)
 */

public class CSVReader {
    private ArrayList<Integer> memberidList;
    private ArrayList<String> dateList;
    private TreeSet<String> dateSet;
    private ArrayList<String> itemList;
    private TreeSet<String> itemSet;
    private ArrayList<TransactionLog> transactionLogList;

    public class dateComparator<String>{

    }
    public CSVReader() throws IOException {
        dateList = new ArrayList<>();
        dateSet = new TreeSet<String>(new Comparator<String>() {
            @Override
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
        transactionLogList = new ArrayList<>();

        dumpCSV();
        buildTransactions();
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

    public void buildTransactions() {
        int nrOfItems = itemList.size();
        // Iterate Over Unique Dates
        for (String date : dateSet) {
            TransactionLog transLog = new TransactionLog(date);

            // Iterate Over the date and find indices that match the date
            for (int i = 0; i < nrOfItems; i++) {
                if (date.equalsIgnoreCase(dateList.get(i))) {
                    transLog.addItem(memberidList.get(i), itemList.get(i));
                }
            }
            transactionLogList.add(transLog);
        }
    }

    public ArrayList<Integer> getMemberidList() {
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

    public ArrayList<TransactionLog> getTransactionLogList() {
        return transactionLogList;
    }
}
