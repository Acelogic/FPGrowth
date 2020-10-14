import java.util.ArrayList;
import java.util.TreeMap;

public class TransactionLog {

    private String date;
    private TreeMap<Integer, Transaction> transactionMap;



    public TransactionLog(String date){
        this.date = date;
        transactionMap = new TreeMap<>();
    }


    public void addItem(int memberID, String item){
        if (!transactionMap.containsKey(memberID)) {
            // if transaction for member is non existent then create one
            transactionMap.put(memberID, new Transaction(memberID, date));
        }
        transactionMap.get(memberID).add(item);
    }


    public String getDate() {
        return date;
    }

    public TreeMap<Integer, Transaction> getTransactionMap() {
        return transactionMap;
    }

    public ArrayList<Transaction> getTransactionList(){
        ArrayList<Transaction> transactionList = new ArrayList<>();

        for (int memberID: transactionMap.keySet()) {
            transactionList.add(transactionMap.get(memberID));
        }
        return transactionList;
    }
}
