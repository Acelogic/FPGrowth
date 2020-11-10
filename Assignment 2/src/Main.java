import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

    FPTree fp = new FPTree(800);
    fp.populateTree();


    fp.getFreqItemSet();
    fp.getFreqItemSetReversed();
    fp.getConditionalPatternBase();
    System.out.println();



    }
}