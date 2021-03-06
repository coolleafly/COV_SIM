
public class Constants
{
    //Citizen initial status parameters
    public static int ORIGINAL_COUNT = 10;       //Number of initial infected persons
    public static int sig = 1;                   //人Influence coefficient of group flow willingness, 1 means everyone wants to move
    public static float FLOW = 0.99f;            //Crowd flow rate,0.99 is the fastest rate of crowd flow

    //Infectious disease parameters
    public static float BROAD_RATE = 0.8f;           //The transmission rate, the probability that a person will get sick after being exposed to a sick person.
    public static float FATALITY_RATE = 0.05f;       //Case fatality rate
    public static float DIAGNOSIS_RATE = 1f;       //Standby parameter, The probability of being diagnosed with the disease after the appearance of suspected symptoms, 
    public static float d_HOSPITAL_RECEIVE_TIME = 1;    //Hospital admission response time
    public static double d_SHADOW_TIME = 7;            //The average incubation period
    public static double d_SHADOW_VARIANCE = 5;      //The standard deviation of the incubation period

    //Cure and discharge outcome parameters
    public static double d_CURED_TIME = 15;            //The average time to cure the disease
    public static double d_CURED_VARIANCE = 2;      //The standard deviation of the time to cure the disease
    public static float CURED_BROAD_RATE = 0f;    //Standby parameter, Probability of reinfection after cure 
    public static float OUT_HOSPITAL_SHADOW = 0f;  //Standby parameter, Probability of remaining infectious after discharge from hospital

    //Parameter of super transfected carriers

    public static float SUPER_RATE = 0.00f;          //0 means tuen off this function
    public static double d_SUPER_TIME = 7;           //The average incubation period for super transfected carriers
    public static double d_SUPER_VARIANCE = 3;         //The standard deviation of the incubation period for super transfected carriers

    //Parameter of probability of death
    public static float d_DIED_TIME = 10;                   //Mean time of death
    public static double d_MILD_DIED_VARIANCE = 1;         //Standard deviation of time to death in mild cases
    public static double d_SEVERE_DIED_VARIANCE = 2;      //Standby parameter, Standard deviation of time to death in severe cases

    //The other parameters
    public static final int CITY_WIDTH = 1000;     //The size of the city is the window boundary
    public static final int CITY_HEIGHT = 800;


    public static int CITY_PERSON_SIZE = 5000;     //Total urban population
    public static int BED_COUNT = 1000;            //Hospital beds
    public static int STOP_DAY = 500;             //Stop time

    public static float everyday_count = 3.0f;    //Repeat times per day

    ///////////////////////////////////////////////
    //Don't change
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
