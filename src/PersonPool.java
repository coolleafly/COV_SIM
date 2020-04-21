import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class PersonPool                   //区域人群对象池，该地区假设为一个近似封闭的环境，拥有几乎不变的民众数量
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

    public int getPeopleSize(int state)                 //获取指定人群数量
    {
        if (state == -1)
        {
            return personList.size();                //state 市民类型 Person.State的值，若为-1则返回当前总数目
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

    public int getPeopleGSize(int G)                 //获取指定人群数量
    {
        if (G == -1)
        {
            return personList.size();                //state 市民类型 Person.State的值，若为-1则返回当前总数目
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
        City city = new City((Constants.CITY_WIDTH/2), (Constants.CITY_HEIGHT/2));        //设置城市中心为坐标(400,400)
        for (int i = 0; i < Constants.CITY_PERSON_SIZE; i++)                   //添加城市居民
        {
            Random random = new Random();                        //产生N(a,b)的数：Math.sqrt(b)*random.nextGaussian()+a
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
