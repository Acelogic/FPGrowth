import java.io.IOException;
import java.util.Scanner;

public class Main {


    public static void printCSVTest() throws IOException {
        TransactionCSVParser r = new TransactionCSVParser();
        System.out.println("CustomerID, Date of purchase, no_of_items(k)");
/*        for (TransactionLog log : r.getTransactionLogList()) {
            System.out.println("=================================================");
            System.out.println("TRANSACTION LOG FOR" + " (" + log.getDate() + ")");
            System.out.println("=================================================");
            for (Transaction transaction : log.getTransactionMap().values()) {
                System.out.println(transaction.toString());

            }
        }*/
        // Transaction List Test
        for (Transaction tr : r.getTransactionList()) {
            System.out.println(tr.toString());
        }
    }

    public static void rawFreqTest() throws IOException {
        TransactionCSVParser r = new TransactionCSVParser();
        System.out.println("Item Set Size: " + r.getItemSet().size());
        System.out.println("Item List Size: " + r.getItemList().size());

        for (String item : r.getItemFrequencyList().keySet()) {
            System.out.println("Item: " + item + " (" + r.getItemFrequencyList().get(item) + ")");
        }
    }

    public static void Debug() throws IOException {
        TransactionCSVParser r = new TransactionCSVParser();
        FPGrowth fp = new FPGrowth(r);



    }


    public static void main(String[] args) throws IOException {
        //printCSVTest();
        // rawFreqTest();
         Debug();


    }
}
