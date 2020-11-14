import java.util.ArrayList;
import java.util.HashMap;

class TrieTests {
    HashMap<String, ArrayList<Node>> unique = new HashMap();
    Node tree = null;

    public TrieTests() {
        tree = new Node(null);
    }

    class Node {
        int freq;
        String s = null;
        Node head = null;
        ArrayList<Node> children = new ArrayList();

        Node(String letter) {
            s = letter;
            if (letter == null) {
                freq = 0;
            } else {
                freq = 1;
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "freq=" + freq +
                    ", s='" + s + '\'' +
                    ", head=" + head +
                    ", children=" + children +
                    '}';
        }
    }

    public Node getHead() {
        return tree;
    }

    public boolean addEntity(Node loci, ArrayList<String> words) {
        if (words.isEmpty()) {
            return true;
        }

        String insertWord = words.get(0);
        words.remove(0);

        Node child = null;
        for (Node x : loci.children) {
            if (x.s.equals(insertWord)) {
                child = x;
                break;
            }
        }
        if (child == null) {
            child = new Node(insertWord);
            child.head = loci;
            loci.children.add(child);
            ArrayList<Node> set = unique.get(insertWord);
            if (set == null) {
                set = new ArrayList();
            }
            set.add(child);
            unique.put(insertWord, set);
        } else {
            child.freq++;
        }

        return addEntity(child, words);
    }

    public static void main(String[] args) {
        TrieTests ds = new TrieTests();
        ArrayList<String> al = new ArrayList<>();
        al.add("dead");
        al.add("child");
        ds.addEntity(ds.getHead(), al);
        al = new ArrayList();
        al.add("dead");
        al.add("ghost");
        ds.addEntity(ds.getHead(), al);
        al = new ArrayList();
        al.add("dead");
        al.add("child");
        al.add("ghost");
        ds.addEntity(ds.getHead(), al);
        System.out.println();
    }
}
