import java.util.Random;

public class MathUtil
{
    private static final Random randomGen = new Random();    //Random number generator

    public static double stdGaussian(double sigma, double u)   //The normal standard deviation sigma value, the normal mean parameter u
    {
        double X = randomGen.nextGaussian();     //Decide whether to flow or not.
        return sigma * X + u;                   //StdX = (X-u)/sigma,  X = sigma * StdX + u
    }
}
