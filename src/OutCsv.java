import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class OutCsv {

    public static int nowday = -1;                     //判断已经输出了哪一天
    public static String fn = "";                      //输出文件名
    public static float max_countTmp = -1;
    public static float count_shadow_average = 0;
    public static float count_shadow_std = 0;
    public static float count_shadow_max = 0;
    public static float count_shadow_min = 0;

    public static void writeCsv(boolean title)
    {
        StringBuffer sb=new StringBuffer();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String time_str = simpleDateFormat.format(date);
        if (fn==""){fn=time_str;}                            //——————2020.02.13注释
        //sb.append("城市总人数,健康者人数,潜伏期人数,超级传播者人数,疑似者人数,确诊者人数，已隔离人数,治愈人数,
        // 真实潜伏期均值，真实潜伏期标准差，真实潜伏期最大值，真实潜伏期最小值，累计确诊人数，
        // r0，第1代病人数，第2代病人数，第3代病人数，第4代病人数，第5代病人数
        // 空余病床,急需病床,病死人数,世界时间（天）,系统记录时间\n");
        if (title)
        {
            sb.append("worldTime(Day),CITY_PERSON_SIZE,NORMAL,SHADOW,SUPER,CONFIRMED,DIAGNOSIS,FREEZE,CURED," +
                    "shadow_average,shadow_std,shadow_max,shadow_min," +

                    "real_countTmp,max_count," +
                    "count_shadow_average,count_shadow_std,count_shadow_max,count_shadow_min," +

                    "sum_confirmed_mark,sum_diagnosis_mark,sum_freeze_mark," +
                    "r0,G1num,G2num,G3Num,G4Num,G5Num," +
                    "BED_CanUse,BED_Need,DEATH,recordTime\n");
        }
        else
        {
            List<Person> people = PersonPool.getInstance().personList;
            float SUM_PERIOD = 0;
            int countTmp = 0;                 //计算每天真实潜伏期及潜伏期的均值和标准差——————2020.02.16注释
            float SHADOW_PERIOD=0;
            float max=-1;
            float min=999;



            int sum_confirmed_mark=0;            //计算累计疑似人数——————2020.02.18注释
            int sum_freeze_mark=0;               //计算累计隔离人数——————2020.02.22注释
            int sum_diagnosis_mark=0;            //计算累计确诊人数——————2020.02.24注释
            float sum_sons=0;            //累加有多少病例的后代
            float count_father=0;        //有多少个可以产生病例的爹
            float r0=0;
            int needbedCount=0;            //计算有多少疑似后达到入院时间的人
            for (Person person : people)
            {
                sum_diagnosis_mark+=person.is_diagnosis_mark;        //确诊人数累计——————2020.02.24注释
                sum_freeze_mark+=person.is_freeze_mark;               //隔离人数累计——————2020.02.22注释
                sum_confirmed_mark+=person.is_confirmed_mark;             //疑似人数累计——————2020.02.18注释
                if ((person.getState() == Person.State.DIAGNOSIS)||(person.getState() == Person.State.FREEZE))
                {
                    countTmp++;
                    SHADOW_PERIOD = (person.confirmedTime/Constants.everyday_count)-(person.infectedTime/Constants.everyday_count);
                    SUM_PERIOD+=SHADOW_PERIOD;
                    if(SHADOW_PERIOD>max)
                    {
                        max=SHADOW_PERIOD;
                    }
                    if(SHADOW_PERIOD<min)
                    {
                        min=SHADOW_PERIOD;
                    }
                }
                //梁烨新增 r0计算
                if (person.getState() == Person.State.FREEZE)
                {
                    sum_sons+=person.son;
                    count_father++;
                }
                if (count_father!=0)
                {
                    r0=sum_sons/count_father;
                }
                //梁烨新增 needbedCount  20200214
//                    if (person.getState() == Person.State.DIAGNOSIS &&       //计算目前所需床位数
//                            MyPanel.worldTime - person.diagnosisTime >= Constants.HOSPITAL_RECEIVE_TIME)
//                    {
//                        needbedCount++;
//                    }
                if(person.getState() == Person.State.DIAGNOSIS)
                {
                    needbedCount++;
                }
            }
            float shadow_average = SUM_PERIOD/countTmp;

            float sss = 0;
            float sum_std =0;
            for (Person person : people)
            {
                if ((person.getState() == Person.State.DIAGNOSIS)||(person.getState() == Person.State.FREEZE))
                {
                    SHADOW_PERIOD = (person.confirmedTime/Constants.everyday_count)-(person.infectedTime/Constants.everyday_count);
                    sss =(SHADOW_PERIOD - shadow_average)*(SHADOW_PERIOD - shadow_average) ;
                    sum_std += sss;
                }
            }
            double shadow_std = Math.sqrt(sum_std/(countTmp-1));
            float shadow_max=max;
            float shadow_min=min;
            if(shadow_max<0)
            {
                shadow_max= 9999;
            }
            if(shadow_min>998)
            {
                shadow_min= 9999;
            }
            if(countTmp > max_countTmp)                    //max_countTmp初始值为-1
            {
                max_countTmp = countTmp;
                count_shadow_average = shadow_average;                 //以下直接附带
                count_shadow_std = (float) shadow_std;
                count_shadow_max= shadow_max;
                count_shadow_min = shadow_min;
            }

            float real_countTmp = countTmp;


                int needBeds=0;
                int canuse=Constants.BED_COUNT- PersonPool.getInstance().getPeopleSize(Person.State.FREEZE);
                if (needbedCount<=canuse)
                {
                    canuse = canuse - needbedCount;
                    needBeds = 0;
                }
                else
                {
                    needBeds=needbedCount-canuse;
                    canuse=0;
                }
            sb.append("" +(int) (MyPanel.worldTime / Constants.everyday_count)+ ',' +
                    Constants.CITY_PERSON_SIZE + ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.NORMAL) + ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.SHADOW) + ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.SUPER) + ',' +   //——————2020.02.16注释
                    PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED) + ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.DIAGNOSIS) + ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.FREEZE)+ ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.CURED)+ ',' +     //——————2020.02.13注释
                    shadow_average +',' +                                    //输出真实潜伏期均值——————2020.02.16注释
                    shadow_std +',' +                                       //输出真实潜伏期标准差——————2020.02.16注释
                    shadow_max+',' +                                        //真实潜伏时间最大值——————2020.02.16注释
                    shadow_min+',' +                                        //真实潜伏时间最小值——————2020.02.16注释
                    real_countTmp  +',' +                     //每天实际确诊加隔离人数，即用于计算潜伏期均值的分母——————2020.02.27注释
                    max_countTmp  +',' +                      //多天以来（实际确诊加隔离人数）的最大值——————2020.02.27注释
                    count_shadow_average +',' +
                    count_shadow_std  +',' +
                    count_shadow_max +',' +
                    count_shadow_min +',' +
                    sum_confirmed_mark  +',' +                             //累计疑似人数——————2020.02.18注释
                    sum_diagnosis_mark  +',' +                            //累计确诊人数——————2020.02.24注释
                    sum_freeze_mark  +',' +                               //累计隔离人数——————2020.02.22注释
                    r0  +',' +                             //r0——————2020.02.18注释
                    PersonPool.getInstance().getPeopleGSize(1)  +',' +              //第1代确诊人数——————2020.02.22注释
                    PersonPool.getInstance().getPeopleGSize(2)  +',' +              //第2代确诊人数——————2020.02.22注释
                    PersonPool.getInstance().getPeopleGSize(3)  +',' +              //第3代确诊人数——————2020.02.22注释
                    PersonPool.getInstance().getPeopleGSize(4)  +',' +              //第4代确诊人数——————2020.02.22注释
                    PersonPool.getInstance().getPeopleGSize(5)  +',' +              //第5代确诊人数——————2020.02.22注释

                    Math.max(canuse, 0)+ ',' +
                    (Math.max(needBeds, 0))+ ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.DEATH)

            );
                sb.append(","+time_str + "\n");
        }
//        if ((title)||((int) (MyPanel.worldTime / Constants.everyday_count) != nowday)) {
            if (!title)
            {
                nowday=(int) (MyPanel.worldTime / Constants.everyday_count);
            }
            try
            {
                FileWriter fw = new FileWriter("output_"+fn+".csv", true);
                fw.write(sb.toString());
                fw.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
//        }
    }
}