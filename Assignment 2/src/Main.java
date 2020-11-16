import org.w3c.dom.ls.LSOutput;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {
        int supportThreshold = 75;
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