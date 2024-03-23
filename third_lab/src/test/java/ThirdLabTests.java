import com.k4r3l1ns.models.Matrix;
import com.k4r3l1ns.service.JacobiRotationMethod;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ThirdLabTests {

    public int[] SIZES = { 10, 30 };

    public double[][] RANGES = {
            { -2, 2 },
            { -50, 50 }
    };

    public double[] EPSILONS = { 1e-5, 1e-7, 1e-9 };

    private static int testNum = 1;

    @Test
    public void testAll() {

        for (var size : SIZES) {
            for (var range : RANGES) {
                for (var eps : EPSILONS) {

                    System.out.println("Тест " + testNum++ + ")");
                    System.out.println("Размерность исходной матрицы: " + size);
                    System.out.println("Диапазон собственных значений: " + Arrays.toString(range));
                    System.out.println("Максимальный внедиагональный элемент: " + eps);
//                    System.out.println("Выполнение программы...");
                    var preciseEigenvector = randomVector(size, range);
                    var matrix = Matrix.predefinedMatrix(preciseEigenvector, range);
                    var eigenvector = JacobiRotationMethod.apply(matrix, 1000000000, eps);
                    System.out.println("Погрешность вычислений: " + findFault(eigenvector, preciseEigenvector) + "\n");
                }
            }
        }
    }

    private double findFault(double[] vector1, double[] vector2) {

        Arrays.sort(vector1);
        Arrays.sort(vector2);

        double result = 0;
        for (int i = 0; i < vector1.length; ++i) {
            double value = Math.abs(vector1[i] - vector2[i]);
            if (value > result) {
                result = value;
            }
        }

        return result;
    }

    private double[] randomVector(int size, double[] range) {

        double[] result = new double[size];
        for (int i = 0; i < size; ++i) {
            result[i] = ThreadLocalRandom.current().nextDouble(range[0], range[1]);
        }
        return result;
    }
}
