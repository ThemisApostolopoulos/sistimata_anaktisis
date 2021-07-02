package phase4;

import org.jetbrains.annotations.NotNull;

public class Tuple implements Comparable{
    private int docId;
    private double cos;


    public Tuple(double cos, int pos) {
        this.cos = cos;
        this.docId = pos;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if(o==null){
            return 1;
        }
        double compareNum = ((Tuple)o).getCos();
        if(this.cos < compareNum){
            return 1;
        }else if(this.cos == 0){
            return 0;
        }else{
            return -1;
        }
    }

    public double getCos(){
        return cos;
    }

    public int getDocId() {
        return docId;
    }
}
