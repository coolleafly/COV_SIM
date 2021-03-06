import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class PersonPool                   
{
    private static PersonPool personPool = new PersonPool();

    public static PersonPool getInstance()
    {
        return personPool;
    }

    List<Person> personList = new ArrayList<Person>();

    public List<Person> getPersonList()
    {
        return personList;
    }

    public int getPeopleSize(int state)                 //Gets the number of the specified population
    {
        if (state == -1)
        {
            return personList.size();                
        }
        int p = 0;
        for (Person person : personList)
        {
            if (person.getState() == state)
            {
                p++;
            }
        }
        return p;
    }

    public int getPeopleGSize(int G)             
    {
        if (G == -1)
        {
            return personList.size();            
        }
        int p = 0;
        for (Person person : personList)
        {
            if (person.generation == G && (person.getState()>=Person.State.FREEZE||person.getState()==Person.State.CURED))
            {
                p++;
            }
        }
        return p;
    }

    private PersonPool()
    {
        City city = new City((Constants.CITY_WIDTH/2), (Constants.CITY_HEIGHT/2));       
        for (int i = 0; i < Constants.CITY_PERSON_SIZE; i++)                   
        {
            Random random = new Random();                        //N(a,b): Math.sqrt(b)*random.nextGaussian()+a
            int x = (int) ((Constants.CITY_WIDTH/8) * random.nextGaussian() + city.getCenterX());
            int y = (int) ((Constants.CITY_HEIGHT/10) * random.nextGaussian() + city.getCenterY());
            if (x > Constants.CITY_WIDTH)
            {
                x = Constants.CITY_WIDTH;
            }
            personList.add(new Person(city, x, y));
        }
    }
}
