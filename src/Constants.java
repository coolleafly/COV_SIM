
public class Constants
{
    //市民初始状态参数
    public static int ORIGINAL_COUNT = 10;       //初始感染者的数量
    public static int sig = 1;                   //人群流动意愿影响系数
    public static float FLOW = 0.99f;            //人群流动速率,0.99为人群流动最快速率, 可导致全城感染

    //传染病参数
    public static float BROAD_RATE = 0.8f;           //传播率
    public static float FATALITY_RATE = 0.05f;       //病死率，根据2月6日数据估算（病死数/确诊数）为0.02
    public static float DIAGNOSIS_RATE = 1f;       //出现疑似症状后最终确诊为患病的概率——————2020.02.24注释
    public static float d_HOSPITAL_RECEIVE_TIME = 1;    //医院收治响应时间
    public static double d_SHADOW_TIME = 7;            //潜伏时间均值——————2020.02.15更改
    public static double d_SHADOW_VARIANCE = 5;      //潜伏时间标准差——————2020.02.150更改

    //治愈及出院转归问题参数
    //CURED_BROAD_RATE默认为0.3f，若CURED_BROAD_RATE与BROAD_RATE一致，则治愈患者与正常人被感染的概率一致
    public static double d_CURED_TIME = 15;            //治愈时间均值——————2020.02.13更改
    public static double d_CURED_VARIANCE = 2;      //治愈时间标准差——————2020.02.13更改
    public static float CURED_BROAD_RATE = 0f;    //治愈后再次感染的概率——————2020.02.14更改
    public static float OUT_HOSPITAL_SHADOW = 0f;  //出院后仍然具有传染性的概率——————2020.02.15更改

    //超级传播者相关参数
    //SUPER_RATE默认为0.01f，当改成0f时，该功能项关闭
    public static float SUPER_RATE = 0.00f;          //成为超级传播者的概率——————2020.02.16更改
    public static double d_SUPER_TIME = 7;           //超级潜伏时间均值——————2020.02.16更改
    public static double d_SUPER_VARIANCE = 3;         //超级潜伏时间标准差——————2020.02.16更改

    //死亡概率参数
    public static float d_DIED_TIME = 10;                   //死亡时间均值，从发病（确诊）时开始计时
    public static double d_MILD_DIED_VARIANCE = 1;         //轻症死亡时间标准差——————2020.02.15更改
    public static double d_SEVERE_DIED_VARIANCE = 2;      //重症死亡时间标准差——————2020.02.15更改

    //默认固定参数
    public static final int CITY_WIDTH = 1000;     //城市大小即窗口边界，限制不允许出城
    public static final int CITY_HEIGHT = 800;


    public static int CITY_PERSON_SIZE = 5000;     //城市总人口数量
    public static int BED_COUNT = 1000;            //医院床位
    public static int STOP_DAY = 500;             //停止时间

    public static float everyday_count = 3.0f;    //每天叠代次数

    ///////////////////////////////////////////////
    //程序内部运算，不要动
    public static double CURED_TIME = d_CURED_TIME * everyday_count;
    public static double CURED_VARIANCE = d_CURED_VARIANCE * everyday_count;

    public static double SHADOW_TIME = d_SHADOW_TIME * everyday_count;
    public static double SHADOW_VARIANCE = d_SHADOW_VARIANCE * everyday_count;

    public static double SUPER_TIME = d_SUPER_TIME * everyday_count;
    public static double SUPER_VARIANCE = d_SUPER_VARIANCE * everyday_count;

    public static double MILD_DIED_VARIANCE = d_MILD_DIED_VARIANCE * everyday_count;
    public static double SEVERE_DIED_VARIANCE = d_SEVERE_DIED_VARIANCE * everyday_count;

    public static float HOSPITAL_RECEIVE_TIME = d_HOSPITAL_RECEIVE_TIME * everyday_count;
    public static float DIED_TIME = d_DIED_TIME * everyday_count;

}
