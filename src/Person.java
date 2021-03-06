import javafx.scene.effect.Shadow;
import java.util.List;
import java.util.Random;

public class Person extends Point
{
    private City city;
    private MoveTarget moveTarget;

    double targetXU;               
    double targetYU;               
    double targetSig = 50;

    public interface State
    {
        int NORMAL = 0;               
        int SUSPECTED = NORMAL + 1;   
        int CURED = SUSPECTED + 1;    
        int SHADOW = CURED + 1;     
        int SUPER = SHADOW + 1;     
        int CONFIRMED = SUPER + 1;   
        int DIAGNOSIS = CONFIRMED + 1; 
        int FREEZE = DIAGNOSIS + 1;  
        int DEATH = FREEZE + 1;      
    }

    private void action()             
    {
        if (state == State.FREEZE || state == State.DEATH)
        {
            return;                       //If you are in isolation or dead, you cannot move
        }
        if (!wantMove())
        {
            return;
        }

        if (moveTarget == null || moveTarget.isArrived())   //If there is flow intention, the flow will be carried out, and the flow displacement still follows the standard normal distribution
        {
            //When it wants to move and there is no target, it sets its own moving target to the randomly generated target point conforming to the normal distribution
            //The number that produces N(a,b)
            double targetX = MathUtil.stdGaussian(targetSig, targetXU);
            double targetY = MathUtil.stdGaussian(targetSig, targetYU);
            moveTarget = new MoveTarget((int) targetX, (int) targetY);
        }
        int dX = moveTarget.getX() - getX();                 //Calculate motion displacement
        int dY = moveTarget.getY() - getY();
        double length = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));   //Distance from the target point
        if (length < 1)
        {
            moveTarget.setArrived(true);   
            return;
        }
        int udX = (int) (dX / length);      //The x axis dX is the displacement quantity, and the sign is the forward direction along the x axis, i.e., udX is the quantity in the x direction
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
        int udY = (int) (dY / length);     //Y axis dY is the displacement quantity, and the sign is the direction of advance along the x axis, i.e., udY is the quantity in the y direction
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
        if (getX() > Constants.CITY_WIDTH || getX() < 0)      
        {
            moveTarget = null;
            if (udX > 0)
            {
                udX = -udX;
            }
        }
        if (getY() > Constants.CITY_HEIGHT || getY() < 0)    
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
        this.city = city;                               
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
    }                                                

    private float SAFE_DIST = 2f;         
    private int state = State.NORMAL;

    public int getState()
    {
        return state;
    }

    int infectedTime = 0;     
    float confirmedTime = 0;       
    int diagnosisTime = 0;       
    int diedMoment = 0;          
    int curedMoment = 0;        

    int is_confirmed_mark=0;      
    int is_diagnosis_mark = 0;       
    int is_freeze_mark = 0;          

 
    int generation=0;            
    int son=0;             

    public boolean isInfected()
    {
        return state >= State.SHADOW;
    }
    public void beInfected()
    {
        int LONG_OR_SHORT = new Random().nextInt(10000) + 1;
        if (1 <= LONG_OR_SHORT && LONG_OR_SHORT <= (int) (Constants.SUPER_RATE * 10000))   
        {
            state = State.SUPER;                              
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


    public void update()                     //Different treatment is carried out for people of various states, and updates and releases the health status of citizens
    {
        if (state == State.DEATH)
        {
            return;                   
        }

        if (state == State.FREEZE && diedMoment== 0)             
        {
            int destiny = new Random().nextInt(10000) + 1;
            if (1 <= destiny && destiny <= (int) (Constants.FATALITY_RATE * 10000))   
            {
                int dieTime = (int) MathUtil.stdGaussian(Constants.SEVERE_DIED_VARIANCE, Constants.DIED_TIME);
                diedMoment = diagnosisTime + dieTime;        //The moment of death is determined after severe onset, and the probability of death is small
            }
            else
                {
                    int lucky = new Random().nextInt(10000) + 1;
                    if (1 <= lucky && lucky <= (int) (Constants.FATALITY_RATE * 10000)) //[1,10000] If the random number is within the range of mild symptoms
                    {
                        int dieTime = (int) MathUtil.stdGaussian(Constants.MILD_DIED_VARIANCE, Constants.DIED_TIME);
                        diedMoment = diagnosisTime + dieTime;
                    }                                 //The moment of death can be determined after mild onset, and death can be avoided with high probability
                    else
                    {
                        diedMoment = -1;            //In cases that have now been diagnosed, death can still be avoided later
                    }
                }
        }

        if (state == State.CONFIRMED && MyPanel.worldTime - confirmedTime >= Constants.HOSPITAL_RECEIVE_TIME)
        {                                                          //If it is now suspected, and (world moment - suspected moment)
            int DOOM = new Random().nextInt(10000) + 1;
            if ( 1 <= DOOM && DOOM <= (int) (Constants.DIAGNOSIS_RATE * 10000))
            {
                state = State.DIAGNOSIS;
                diagnosisTime = MyPanel.worldTime;                    //Record the moment of diagnosis
                is_diagnosis_mark = 1;                                //They are marked as soon as they are diagnosed
            }
//            else
//            {
//                state = State.NORMAL;
//            }
        }

        if(state == State.DIAGNOSIS)    //Admission of patients who have already been diagnosed
        {
            Bed bed = Hospital.getInstance().pickBed();
            if (bed != null)
            {                                                     //Search for vacant beds and place patients if available
                useBed = bed;
                state = State.FREEZE;
                is_freeze_mark = 1 ;
                setX(bed.getX());
                setY(bed.getY());
                bed.setEmpty(false);
            }
        }

        if (state == State.DIAGNOSIS && diedMoment== 0)              //Treatment of patients who have been hospitalized, life and death are not yet known
        {
            int dieTime = (int) MathUtil.stdGaussian(Constants.SEVERE_DIED_VARIANCE, Constants.DIED_TIME);
            diedMoment = diagnosisTime + dieTime;
        }

        if (state == State.FREEZE && curedMoment == 0)             //Now in the hospital
        {
            int curedTime = (int) MathUtil.stdGaussian(Constants.CURED_VARIANCE, Constants.CURED_TIME);
            if (curedTime > diedMoment - MyPanel.worldTime && diedMoment > 0)
            {
                curedMoment = -1;                 
            }
            else
            {
                curedMoment = MyPanel.worldTime + curedTime;
            }
        }

        if (state == State.FREEZE && curedMoment > 0 && MyPanel.worldTime >= curedMoment)   
        {
            int random1 = new Random().nextInt(10000) + 1;        
            if (random1 < Constants.OUT_HOSPITAL_SHADOW*10000)
            {
                infectedTime = MyPanel.worldTime;
//                confirmedTime = 0;
                diagnosisTime = 0;
//                infectedTime = 0;
                diedMoment = 0;
                curedMoment = 0;                    
                state = State.SHADOW;                 //The state goes dormant and then returns to a random location within the city
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
                state = State.CURED;                      //The state is changed to Heal and then returned to a random location in the city
            }
            super.setX((int) MathUtil.stdGaussian(100, city.getCenterX()));
            super.setY((int) MathUtil.stdGaussian(100, city.getCenterY()));
            Hospital.getInstance().returnBed(useBed);                  //Return the beds
        }
        if ((state == State.DIAGNOSIS || state == State.FREEZE) && MyPanel.worldTime >= diedMoment && diedMoment > 0)
        {
            state = State.DEATH;                                 
            super.setX((int) MathUtil.stdGaussian(100, city.getCenterX()));
            super.setY((int) MathUtil.stdGaussian(100, city.getCenterY()));
            Hospital.getInstance().returnBed(useBed);                    //Return the beds
        }
        if (MyPanel.worldTime  > confirmedTime && state == State.SHADOW)
        {                                                //A normal distribution is used for the time during the incubation period when suspected symptoms appear randomly
            state = State.CONFIRMED;
            is_confirmed_mark = 1;                              
        }
        if(MyPanel.worldTime  > confirmedTime && state == State.SUPER)
        {
            state = State.CONFIRMED;             
            is_confirmed_mark = 1;               
        }

        action();                                                       //Handling the movement of unquarantined persons

        List<Person> people = PersonPool.getInstance().personList;
        if (state >= State.SHADOW)
        {
            return;
        }
        for (Person person : people)
        {

            if (person.getState() == State.NORMAL||person.getState() == State.DEATH||person.getState() == State.CURED)
            {
                continue;                            //It traverses the population and infects others by random values and determining whether it is a safe distance
            }
            int random = new Random().nextInt(10000) + 1;
            if(state== State.NORMAL && random < Constants.BROAD_RATE*10000 && distance(person) < SAFE_DIST)
            {
                this.beInfected();                     
                person.son++;                           
                this.generation=person.generation + 1;   
                break;
            }
            if (state == State.CURED && random < Constants.CURED_BROAD_RATE*10000 && distance(person) < SAFE_DIST)
            {
                this.beInfected();                    //The probability of a person being reinfected from an unsafe distance after being cured ,Liang
                person.son++;                        
                this.generation=person.generation + 1;   //The infective algebra of this person is the next generation of the parent
                break;
            }
        }
    }
}
