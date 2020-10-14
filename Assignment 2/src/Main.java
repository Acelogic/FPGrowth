import java.io.IOException;

public class Main {


    public static void printCSV() throws IOException {
        CSVReader r = new CSVReader();
        System.out.println("CustomerID, Date of purchase, no_of_items(k)");
        for (TransactionLog log : r.getTransactionLogList()) {
            System.out.println("=================================================");
            System.out.println("TRANSACTION LOG FOR" + " ("+log.getDate()+")");
            System.out.println("=================================================");
            for (Transaction transaction : log.getTransactionMap().values()) {
                System.out.println(transaction.toString());

            }
        }
    }

    public static void main(String[] args) throws IOException {
        printCSV();
        //CSVReader r = new CSVReader();
        //System.out.println(r.getItemSet().size());

    }
}
