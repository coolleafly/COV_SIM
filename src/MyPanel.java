import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyPanel extends JPanel implements Runnable {

    public MyPanel()
    {
        super();
        this.setBackground(new Color(0x444444));
    }

    @Override
    public void paint(Graphics g)
    {
        timeLine.timerun();
        super.paint(g);
        g.setColor(new Color(0xFF797D));                   //The Color of the Hospital
        g.drawRect(Hospital.getInstance().getX(), Hospital.getInstance().getY(),
                Hospital.getInstance().getWidth(), Hospital.getInstance().getHeight());    
        g.setFont(new Font("微软雅黑", Font.BOLD, 16));
        g.setColor(new Color(0x00ff00));
        g.drawString("医院", Hospital.getInstance().getX() + Hospital.getInstance().getWidth() / 4,
                Hospital.getInstance().getY() - 16);
        List<Person> people = PersonPool.getInstance().getPersonList();    //The circle of the people
        if (people == null)
        {
            return;
        }
        for (Person person : people)
        {
            switch (person.getState())
            {
                case Person.State.NORMAL:
                {
                    g.setColor(new Color(0x338221));   //NORMAL
                    break;
                }
                case Person.State.SHADOW:
                {
                    g.setColor(new Color(0xffee00));   //SHADOW
                    break;
                }
                case Person.State.SUPER:
                {
                    g.setColor(new Color(0xFFFFAE));     //SUPER
                    break;
                }
                case Person.State.CONFIRMED:
                {
                    g.setColor(new Color(0xFF97C4));   //CONFIRMED
                    break;
                }
                case Person.State.DIAGNOSIS:
                {
                    g.setColor(new Color(0xFF0E09));   //DIAGNOSIS
                    break;
                }
                case Person.State.FREEZE:
                {
                    g.setColor(new Color(0x48FFFC));   //FREEZE
                    break;
                }
                case Person.State.DEATH:
                {
                    g.setColor(new Color(0x000000));   //DEATH
                    break;
                }
                case Person.State.CURED:
                {
                    //治愈患者
                    g.setColor(new Color(0x00ff00));      //CURED
                }
            }
            person.update();         //Data update
            g.fillRoundRect(person.getX(), person.getY(), 5, 5,0,0);
        }

        int captionStartOffsetX = Constants.CITY_WIDTH + Hospital.getInstance().getWidth() - 70;
        int captionStartOffsetY = 40;
        int captionSize = 24;

        //显示数据信息
        g.setColor(Color.WHITE);
        g.drawString("Total persons：" + Constants.CITY_PERSON_SIZE, captionStartOffsetX, captionStartOffsetY);
        g.setColor(new Color(0x338221));
        g.drawString("Number of healthy persons: " + PersonPool.getInstance().getPeopleSize(Person.State.NORMAL),
                captionStartOffsetX, captionStartOffsetY + captionSize);
        g.setColor(new Color(0xffee00));
        g.drawString("Number of people in incubation period: " + PersonPool.getInstance().getPeopleSize(Person.State.SHADOW),
                captionStartOffsetX, captionStartOffsetY + 2 * captionSize);
        g.setColor(new Color(0xFFFFAE));
        g.drawString("Number of super infectors:" + PersonPool.getInstance().getPeopleSize(Person.State.SUPER),
                captionStartOffsetX, captionStartOffsetY + 3 * captionSize);
        g.setColor(new Color(0xFF97C4));
        g.drawString("Number of suspected symptoms persons:" + PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED),
                captionStartOffsetX, captionStartOffsetY + 4 * captionSize);
        g.setColor(new Color(0xFF0E09));
        g.drawString("Number of confirmed:" + PersonPool.getInstance().getPeopleSize(Person.State.DIAGNOSIS),
                captionStartOffsetX, captionStartOffsetY + 5 * captionSize);
        g.setColor(new Color(0x48FFFC));
        g.drawString("Number of isolated at the hospital:" + PersonPool.getInstance().getPeopleSize(Person.State.FREEZE),
                captionStartOffsetX, captionStartOffsetY + 6 * captionSize);
        g.setColor(new Color(0x00ff00));
        g.drawString("治愈者人数：" + PersonPool.getInstance().getPeopleSize(Person.State.CURED),
                captionStartOffsetX, captionStartOffsetY + 7 * captionSize);
        g.setColor(new Color(0x338221));
        g.drawString("空余病床数：" + Math.max(Constants.BED_COUNT - PersonPool.getInstance().getPeopleSize(Person.State.FREEZE), 0),
                captionStartOffsetX, captionStartOffsetY + 8 * captionSize);
        g.setColor(new Color(0xE39476));
        //    急需病床数量应为 NEED = 现在发病者数量 - (床位总数-隔离人数)

        int needBeds = PersonPool.getInstance().getPeopleSize(Person.State.DIAGNOSIS)
            -(Constants.BED_COUNT- PersonPool.getInstance().getPeopleSize(Person.State.FREEZE));

        g.drawString("急需病床：" + (Math.max(needBeds, 0)),
                captionStartOffsetX, captionStartOffsetY + 9 * captionSize);
        g.setColor(new Color(0x000000));
        g.drawString("病死人数：" + PersonPool.getInstance().getPeopleSize(Person.State.DEATH),
                captionStartOffsetX, captionStartOffsetY + 10 * captionSize);
        g.setColor(new Color(0xffffff));
        g.drawString("世界时间（天）：" + (int) (worldTime / Constants.everyday_count), captionStartOffsetX,
                captionStartOffsetY + 11 * captionSize);
        OutCsv.writeCsv(false);
        worldTime++;
    }

    public static int worldTime = 0;//世界时间

    public Timer timer = new Timer();

    class MyTimerTask extends TimerTask
    {
        public boolean isStop()
        {
            boolean result=false;
            //动画重绘终止条件， 以被隔离达到或超过100人时停止为例。
            //if (PersonPool.getInstance().getPeopleSize(Person.State.FREEZE)>=100) {result = true;};
            //运行到世界时间30天。
            if ((int) (worldTime / Constants.everyday_count)>=Constants.STOP_DAY)
            {
                result = true;
            }
            return  result;
        }

        @Override
        public void run() {
            //动画重绘终止条件， 以被隔离超过100人时停止为例。
            if (!this.isStop())
            {
                MyPanel.this.repaint();
            }
        }
    }

    @Override
    public void run() {
        timer.schedule(new MyTimerTask(), 0, 100);
        //启动世界计时器，时间开始流动（突然脑补DIO台词：時は停た）
    }
}
