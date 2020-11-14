import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

    FPTree fpTree = new FPTree(1400);
    fpTree.insertAll();
    fpTree.generateConditionalPatternBase(0);
    fpTree.generateConditionalFP();
    System.out.println();



    }
}