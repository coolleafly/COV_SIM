import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
public class Hospital extends Point
{
    public static final int HOSPITAL_X = Constants.CITY_WIDTH+150;
    public static final int HOSPITAL_Y = 80;
    private int width;
    private int height = 600;

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    private static Hospital hospital = new Hospital();

    public static Hospital getInstance()
    {
        return hospital;
    }

    private Point point = new Point(HOSPITAL_X, HOSPITAL_Y);//The first window position is drawn and used as the base position for other beds. If the screen overlaps or the bed cannot be displayed, it is related to this procedure.
    private List<Bed> beds = new ArrayList<>();

    public List<Bed> getBeds()                           
    {
        return beds;
    }

    private Hospital()
    {
        super(HOSPITAL_X, HOSPITAL_Y + 10);           //Draw a rectangle to represent the hospital and dynamically change its size.
        if (Constants.BED_COUNT == 0)                        //Calculate the coordinate
        {
            width = 0;                            //Calculate the width of the rectangle
            height = 0;                           
        }

        int column = Constants.BED_COUNT / 100;
        if (Constants.BED_COUNT % 100>0){column++;}

        width = column * 6;                                     //Initializes coordinates for other beds
        for (int i = 0; i < column; i++)
        {

            for (int j = 10; j <= 606; j += 6)
            {
                Bed bed = new Bed(point.getX() + i * 6, point.getY() + j);
                beds.add(bed);
                if (beds.size() >= Constants.BED_COUNT)
                {
                    break;
                }
            }
        }
    }

    public Bed pickBed()                    //Take up the bed
    {
        for (Bed bed : beds)
        {
            if (bed.isEmpty())
            {
                return bed;
            }
        }
        return null;
    }

    public Bed returnBed(Bed bed)                //Release the occupancy of beds upon death or recovery from discharge
    {
        if (bed != null)
        {
            bed.setEmpty(true);
        }
        return bed;
    }
}
