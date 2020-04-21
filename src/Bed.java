public class Bed extends Point
{
    public Bed(int x, int y) {
        super(x, y);
    }


    private boolean isEmpty = true;             //是否占用了该床位

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
