import javafx.scene.effect.Shadow;
import java.util.List;
import java.util.Random;

public class Person extends Point
{
    private City city;
    private MoveTarget moveTarget;

    double targetXU;                 //x方向的均值mu           //正态分布N(mu,sigma)随机位移目标位置
    double targetYU;                 //y方向的均值mu
    double targetSig = 50;//方差sigma

    public interface State
    {
        int NORMAL = 0;               //正常人，未感染的健康人
        int SUSPECTED = NORMAL + 1;   //有暴露感染风险
        int CURED = SUSPECTED + 1;    //治愈者——————2020.02.13注释
        int SHADOW = CURED + 1;       //潜伏期
        int SUPER = SHADOW + 1;       //超级传播者——————2020.02.17注释
        int CONFIRMED = SUPER + 1;   //疑似感染病人
        int DIAGNOSIS = CONFIRMED + 1;  //确诊病人——————2020.02.24注释
        int FREEZE = DIAGNOSIS + 1;   //住院病人
        int DEATH = FREEZE + 1;       //病死者
    }

    private void action()                 //不同状态下的单个人实例运动行为
    {
        if (state == State.FREEZE || state == State.DEATH)
        {
            return;                       //如果处于隔离或者死亡状态，则无法行动
        }
        if (!wantMove())
        {
            return;
        }

        if (moveTarget == null || moveTarget.isArrived())   //存在流动意愿的，将进行流动，流动位移仍然遵循标准正态分布
        {
            //在想要移动并且没有目标时，将自身移动目标设置为随机生成的符合正态分布的目标点
            //产生N(a,b)的数：Math.sqrt(b)*random.nextGaussian()+a
            double targetX = MathUtil.stdGaussian(targetSig, targetXU);
            double targetY = MathUtil.stdGaussian(targetSig, targetYU);
            moveTarget = new MoveTarget((int) targetX, (int) targetY);
        }
        int dX = moveTarget.getX() - getX();                 //计算运动位移
        int dY = moveTarget.getY() - getY();
        double length = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));   //与目标点的距离
        if (length < 1)
        {
            moveTarget.setArrived(true);   //判断是否到达目标点
            return;
        }
        int udX = (int) (dX / length);      //x轴dX为位移量，符号为沿x轴前进方向, 即udX为X方向表示量
        if (udX == 0 && dX != 0)
        {
            if (dX > 0)
            {
                udX = 1;
            }
            else
            {
                udX = -1;
            }
        }
        int udY = (int) (dY / length);     //y轴dY为位移量，符号为沿x轴前进方向，即udY为Y方向表示量
        if (udY == 0 && dY != 0)
        {
            if (dY > 0)
            {
                udY = 1;
            }
            else
            {
                udY = -1;
            }
        }
        if (getX() > Constants.CITY_WIDTH || getX() < 0)       //横向运动边界
        {
            moveTarget = null;
            if (udX > 0)
            {
                udX = -udX;
            }
        }
        if (getY() > Constants.CITY_HEIGHT || getY() < 0)     //纵向运动边界
        {
            moveTarget = null;
            if (udY > 0)
            {
                udY = -udY;
            }
        }
        moveTo(udX, udY);
    }
    public Bed useBed;
    public Person(City city, int x, int y)
    {
        super(x, y);
        this.city = city;                                     //对市民的初始位置进行N(x,100)的正态分布随机
        targetXU = MathUtil.stdGaussian(100, x);
        targetYU = MathUtil.stdGaussian(100, y);
    }
    public boolean wantMove()
    {
        return MathUtil.stdGaussian(Constants.sig, Constants.FLOW) > 0;
    }
    public double distance(Person person)
    {
        return Math.sqrt(Math.pow(getX() - person.getX(), 2) + Math.pow(getY() - person.getY(), 2));
    }                                                         //计算两点之间的直线距离

    private float SAFE_DIST = 2f;          //安全距离
    private int state = State.NORMAL;

    public int getState()
    {
        return state;
    }

    int infectedTime = 0;          //感染时刻
    float confirmedTime = 0;         //疑似时刻
    int diagnosisTime = 0;        //确诊时刻——————2020.02.24注释
    int diedMoment = 0;            //死亡时刻，为0代表未确定，-1代表不会病死
    int curedMoment = 0;           //治愈时刻——————2020.02.13注释

    int is_confirmed_mark=0;        //出现疑似症状后原始标记值——————2020.02.18注释
    int is_diagnosis_mark = 0;         //确诊原始标记值——————2020.02.24注释
    int is_freeze_mark = 0;             //隔离原始标记值

//    int infectedTime_long = 0;       //超级传播者的成为长期感染状态的时刻——————2020.02.17注释
    int generation=0;              //第几代病例----梁烨 2020.2.22
    int son=0;                     //他产生了多少个病毒宝宝，上帝之眼看R0。。--梁烨 2020.2.22

    public boolean isInfected()
    {
        return state >= State.SHADOW;
    }
    public void beInfected()
    {
        int LONG_OR_SHORT = new Random().nextInt(10000) + 1;
        if (1 <= LONG_OR_SHORT && LONG_OR_SHORT <= (int) (Constants.SUPER_RATE * 10000))   //[1,10000]随机数若在**区间内
        {
            state = State.SUPER;                                  //有较小概率成为超级传播者——————2020.02.17注释
            infectedTime  = MyPanel.worldTime;
            double stdSuper_time = (MathUtil.stdGaussian(Constants.SUPER_VARIANCE,Constants.SUPER_TIME));
            double stdsuper_time =  Math.max(stdSuper_time,0);
            confirmedTime = (float) (MyPanel.worldTime + stdsuper_time);
        }
        else
        {
            state = State.SHADOW;
            infectedTime = MyPanel.worldTime;
            double stdShadow_time = (MathUtil.stdGaussian(Constants.SHADOW_VARIANCE,Constants.SHADOW_TIME));
            double stdRnshadow_time = Math.max(stdShadow_time,0);
            confirmedTime = (float) (MyPanel.worldTime + stdRnshadow_time);
        }
    }


    public void update()                     //对各种状态的人进行不同的处理，更新发布市民健康状态
    {
        if (state == State.DEATH)
        {
            return;                   //如果已经死亡，就不需要处理了
        }

        if (state == State.FREEZE && diedMoment== 0)              //处理已经住院的患者，目前生死未定
        {
            int destiny = new Random().nextInt(10000) + 1;
            if (1 <= destiny && destiny <= (int) (Constants.FATALITY_RATE * 10000))   //[1,10000]随机数若在重症区间内
            {
                int dieTime = (int) MathUtil.stdGaussian(Constants.SEVERE_DIED_VARIANCE, Constants.DIED_TIME);
                diedMoment = diagnosisTime + dieTime;        //重度发病后确定死亡时刻，小概率逃过死亡——————2020.02.15注释
            }
            else
                {
                    int lucky = new Random().nextInt(10000) + 1;
                    if (1 <= lucky && lucky <= (int) (Constants.FATALITY_RATE * 10000)) //[1,10000]随机数若在轻症区间内
                    {
                        int dieTime = (int) MathUtil.stdGaussian(Constants.MILD_DIED_VARIANCE, Constants.DIED_TIME);
                        diedMoment = diagnosisTime + dieTime;
                    }                                 //轻度发病后确定死亡时刻，可大概率逃过死亡——————2020.02.15注释
                    else
                    {
                        diedMoment = -1;            //在现已确诊的情况下，日后仍可逃过死亡——————2020.02.15注释
                    }
                }
        }

        if (state == State.CONFIRMED && MyPanel.worldTime - confirmedTime >= Constants.HOSPITAL_RECEIVE_TIME)
        {                                                          //如果现在是疑似状态，且（世界时刻-疑似时刻）
            int DOOM = new Random().nextInt(10000) + 1;
            if ( 1 <= DOOM && DOOM <= (int) (Constants.DIAGNOSIS_RATE * 10000))
            {
                state = State.DIAGNOSIS;
                diagnosisTime = MyPanel.worldTime;                    //把确诊时刻记录下来
                is_diagnosis_mark = 1;                                //确诊后即被标记
            }
//            else
//            {
//                state = State.NORMAL;
//            }
        }

        if(state == State.DIAGNOSIS)    //已确诊过的病人进行收治——————2020.02.24注释
        {
            Bed bed = Hospital.getInstance().pickBed();
            if (bed != null)
            {                                                     //查找空床位,若有空床就安置病人
                useBed = bed;
                state = State.FREEZE;
                is_freeze_mark = 1 ;
                setX(bed.getX());
                setY(bed.getY());
                bed.setEmpty(false);
            }
        }

        if (state == State.DIAGNOSIS && diedMoment== 0)              //处理已经住院的患者，目前生死未定
        {
            int dieTime = (int) MathUtil.stdGaussian(Constants.SEVERE_DIED_VARIANCE, Constants.DIED_TIME);
            diedMoment = diagnosisTime + dieTime;
        }

        if (state == State.FREEZE && curedMoment == 0)             //目前住院但能否康复未知
        {
            int curedTime = (int) MathUtil.stdGaussian(Constants.CURED_VARIANCE, Constants.CURED_TIME);
            if (curedTime > diedMoment - MyPanel.worldTime && diedMoment > 0)
            {
                curedMoment = -1;                   //来不及治好，死亡时间在治愈时间之前——————2020.02.13注释
            }
            else
            {
                curedMoment = MyPanel.worldTime + curedTime;
            }
        }

        if (state == State.FREEZE && curedMoment > 0 && MyPanel.worldTime >= curedMoment)   //目前住院但达到康复时间
        {
            int random1 = new Random().nextInt(10000) + 1;        //随机摇个骰子1-100——————2020.02.15注释
            if (random1 < Constants.OUT_HOSPITAL_SHADOW*10000)
            {
                infectedTime = MyPanel.worldTime;
//                confirmedTime = 0;
                diagnosisTime = 0;
//                infectedTime = 0;
                diedMoment = 0;
                curedMoment = 0;                      //重新定义状态变化之后的需要判定的时刻——————2020.02.15注释
                state = State.SHADOW;                 //状态变为潜伏，之后放回城市内随机位置——————2020.02.15注释
                double stdShadow_time = (MathUtil.stdGaussian(Constants.SHADOW_VARIANCE,Constants.SHADOW_TIME));
                double stdRnshadow_time = Math.abs(stdShadow_time);
                confirmedTime = (float) (MyPanel.worldTime + stdRnshadow_time);
            }
            else
            {
//                infectedTime =0;
                confirmedTime = 0;
                diagnosisTime = 0;
                infectedTime = 0;
                diedMoment = 0;
                curedMoment = 0;
                state = State.CURED;                      //状态变为治愈，之后放回城市内随机位置——————2020.02.15注释
            }
            super.setX((int) MathUtil.stdGaussian(100, city.getCenterX()));
            super.setY((int) MathUtil.stdGaussian(100, city.getCenterY()));
            Hospital.getInstance().returnBed(useBed);                  //归还床位——————2020.02.15注释
        }
        if ((state == State.DIAGNOSIS || state == State.FREEZE) && MyPanel.worldTime >= diedMoment && diedMoment > 0)
        {
            state = State.DEATH;                                 //患者死亡,尸体丢弃在城市正态分布随机所得位置
            super.setX((int) MathUtil.stdGaussian(100, city.getCenterX()));
            super.setY((int) MathUtil.stdGaussian(100, city.getCenterY()));
            Hospital.getInstance().returnBed(useBed);                    //归还床位
        }
        if (MyPanel.worldTime  > confirmedTime && state == State.SHADOW)
        {                                                //一个正态分布，用于潜伏期内随机出现疑似症状时间
            state = State.CONFIRMED;
            is_confirmed_mark = 1;                              //出现疑似症状后就被标记——————2020.02.18注释
        }
        if(MyPanel.worldTime  > confirmedTime && state == State.SUPER)
        {
            state = State.CONFIRMED;               //一个正态分布，用于超级潜伏者随机出现疑似症状时间——————2020.02.17注释
            is_confirmed_mark = 1;               //出现疑似症状后就被标记——————2020.02.18注释
        }

        action();                                                       //处理未隔离者的移动问题

        List<Person> people = PersonPool.getInstance().personList;
        if (state >= State.SHADOW)
        {
            return;
        }
        for (Person person : people)
        {

            if (person.getState() == State.NORMAL||person.getState() == State.DEATH||person.getState() == State.CURED)
            {
                continue;                            //遍历人群，通过随机值和判定是否为安全距离决定感染其他人
            }
            int random = new Random().nextInt(10000) + 1;
            if(state== State.NORMAL && random < Constants.BROAD_RATE*10000 && distance(person) < SAFE_DIST)
            {
                this.beInfected();                      //一个正常人在非安全距离里感染——————2020.02.14注释
                person.son++;                           //传染他的人多了一个病毒宝宝 ----2020.2.22 梁烨FATALITY_RATE
                this.generation=person.generation + 1;   //此人的感染代数是父辈的下一代 ----2020.2.22 梁烨
                break;
            }
            if (state == State.CURED && random < Constants.CURED_BROAD_RATE*10000 && distance(person) < SAFE_DIST)
            {
                this.beInfected();                    //一个人治愈后，在非安全距离再次被传播感染的概率——————2020.02.14注释
                person.son++;                        //传染他的人多了一个病毒宝宝 ----2020.2.22 梁烨
                this.generation=person.generation + 1;   //此人的感染代数是父辈的下一代 ----2020.2.22 梁烨 治愈后再次感染视为新一代病例
                break;
            }
        }
    }
}
