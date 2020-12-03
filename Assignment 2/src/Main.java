import org.w3c.dom.ls.LSOutput;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {

/*        // Task 1
        TransactionCSVParser r = new TransactionCSVParser();
        System.out.println("CustomerID, Date of purchase, no_of_items(k)");
        for (TransactionLog log : r.getTransactionLogList()) {
            System.out.println("=================================================");
            System.out.println("TRANSACTION LOG FOR" + " (" + log.getDate() + ")");
            System.out.println("=================================================");
            for (Transaction transaction : log.getTransactionMap().values()) {
                System.out.println(transaction.toString());
            }
        }*/


        // Task 2
        int supportThreshold = 90;
        System.out.println("Support Threshold: " + supportThreshold);
        FPTree fpTree = new FPTree(supportThreshold);
        Long begin = System.currentTimeMillis();
        fpTree.insertAll();
        fpTree.generateConditionalPatternBase();
        fpTree.generateConditionalFP();
        fpTree.generateFrequentItemSetsAndRules();
        Long end = System.currentTimeMillis() - begin;
        System.out.println("Time: " + end + "ms");


    }
}