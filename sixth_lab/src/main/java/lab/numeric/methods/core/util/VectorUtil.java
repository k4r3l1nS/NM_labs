package lab.numeric.methods.core.util;

public class VectorUtil {

    public static double[] scaleVector(double[] v, double s) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; ++i) {
            result[i] = v[i] * s;
        }
        return result;
    }

    public static double[] sumVectors(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        for (int i = 0; i < v1.length; ++i) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }
}
