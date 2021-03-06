public class Bed extends Point
{
    public Bed(int x, int y) {
        super(x, y);
    }


    private boolean isEmpty = true;             //Is the bed occupied

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
