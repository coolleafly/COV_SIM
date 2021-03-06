import javax.swing.*;

import java.util.List;
import java.util.Random;
public class Main
{
    public static void main(String[] args)                    //Program main entry
    {
        initHospital();
        initPanel();
        initInfected();
    }

    private static void initPanel()                 //init the Panel
    {
        MyPanel p = new MyPanel();
        Thread panelThread = new Thread(p);
        JFrame frame = new JFrame();
        frame.add(p);
        frame.setSize(Constants.CITY_WIDTH + hospitalWidth + 300, Constants.CITY_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setTitle("Simulation models for the spread of infectious diseases");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelThread.start();             //Start the Canvas Thread
        OutCsv.writeCsv(true);
    }
    private static int hospitalWidth;

    private static void initHospital() {
        hospitalWidth = Hospital.getInstance().getWidth();
    }

    private static void initInfected()
    {
        List<Person> people = PersonPool.getInstance().getPersonList();              //Establish citizens
        for (int i = 0; i < Constants.ORIGINAL_COUNT; i++)
        {
            Person person;
            do
            {
                person = people.get(new Random().nextInt(people.size() - 1));//Pick a citizen at random
            }
            while (person.isInfected());         //If the citizen has been infected, reselect
            person.beInfected();                 
            person.generation=1;
        }
    }
}
