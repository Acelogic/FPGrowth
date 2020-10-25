import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Transaction extends ArrayList<String> implements Comparable{

    private int memberID;
    private String date;


    public Transaction(int memberID, String date){
        this.memberID = memberID;
        this.date = date;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(memberID +","+date+","+this.size());
        for (String item: this) {
            sb.append(","+item);
        }

        return sb.toString();

    }

    public int getMemberID() {
        return memberID;
    }

    public String getDate() {
        return date;
    }


    @Override
    public int compareTo(Object o) {
        Transaction other = (Transaction) o;
        Date a = new SimpleDateFormat("dd-MM-yyyy").parse(this.date, new ParsePosition(0));
        Date b = new SimpleDateFormat("dd-MM-yyyy").parse(other.date, new ParsePosition(0));

        // Define comparing logic here
        if(a.compareTo(b) > 0) {
            return a.compareTo(b);
        }
        else if(a.compareTo(b) < 0) {
            return a.compareTo(b);
        }
            return Integer.compare(this.memberID, other.memberID);


    }

}
