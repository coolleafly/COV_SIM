import java.util.Random;

public class MathUtil
{
    private static final Random randomGen = new Random();    //使用一个随机数生成器

    public static double stdGaussian(double sigma, double u)   //正态标准差sigma值,正态均值参数mu
    {
        double X = randomGen.nextGaussian();     //流动意愿标准化后判断是在0的左边还是右边从而决定是否流动。
        return sigma * X + u;                   //StdX = (X-u)/sigma，即X = sigma * StdX + u
    }
}
