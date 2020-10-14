import java.util.ArrayList;

public class Transaction extends ArrayList<String> {

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


}
