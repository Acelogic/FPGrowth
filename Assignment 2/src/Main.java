import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

    FPTree fpTree = new FPTree(120);
    fpTree.insertAll();
    fpTree.generateConditionalPatternBase();
    System.out.println();
    fpTree.generateConditionalFP();
    System.out.println();



    }
}