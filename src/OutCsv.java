import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class OutCsv {

    public static int nowday = -1;                     
    public static String fn = "";                      //output filename
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
        if (fn==""){fn=time_str;}                            
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
            int countTmp = 0;                 // Calculate the daily true latency and the mean and standard deviation of the latency
            float SHADOW_PERIOD=0;
            float max=-1;
            float min=999;



            int sum_confirmed_mark=0;            
            int sum_freeze_mark=0;               
            int sum_diagnosis_mark=0;            
            float sum_sons=0;            
            float count_father=0;        
            float r0=0;
            int needbedCount=0;            
            for (Person person : people)
            {
                sum_diagnosis_mark+=person.is_diagnosis_mark;       
                sum_freeze_mark+=person.is_freeze_mark;              
                sum_confirmed_mark+=person.is_confirmed_mark;          
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
                //add by Liang
                if (person.getState() == Person.State.FREEZE)
                {
                    sum_sons+=person.son;
                    count_father++;
                }
                if (count_father!=0)
                {
                    r0=sum_sons/count_father;
                }
                //add by liang, needbedCount  20200214
//                    if (person.getState() == Person.State.DIAGNOSIS &&    
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
            if(countTmp > max_countTmp)                  
            {
                max_countTmp = countTmp;
                count_shadow_average = shadow_average;             
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
                    PersonPool.getInstance().getPeopleSize(Person.State.SUPER) + ',' +  
                    PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED) + ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.DIAGNOSIS) + ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.FREEZE)+ ',' +
                    PersonPool.getInstance().getPeopleSize(Person.State.CURED)+ ',' +     
                    shadow_average +',' +                                   
                    shadow_std +',' +                                    
                    shadow_max+',' +                                      
                    shadow_min+',' +                                     
                    real_countTmp  +',' +             
                    max_countTmp  +',' +                    
                    count_shadow_average +',' +
                    count_shadow_std  +',' +
                    count_shadow_max +',' +
                    count_shadow_min +',' +
                    sum_confirmed_mark  +',' +                     
                    sum_diagnosis_mark  +',' +                
                    sum_freeze_mark  +',' +                          
                    r0  +',' +                            
                    PersonPool.getInstance().getPeopleGSize(1)  +',' +           
                    PersonPool.getInstance().getPeopleGSize(2)  +',' +           
                    PersonPool.getInstance().getPeopleGSize(3)  +',' +             
                    PersonPool.getInstance().getPeopleGSize(4)  +',' +        
                    PersonPool.getInstance().getPeopleGSize(5)  +',' +          

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
