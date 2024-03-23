import com.k4r3l1ns.models.CrackedMatrix;
import com.k4r3l1ns.models.Vector;
import com.k4r3l1ns.service.CrackedMatrixOperationUnit;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class FirstPartTests {

    @Test
    public void testAll() {

        double[] range1 = { 0.1, 10.0};
        double[] range2 = { 0.01, 100.0};
        double[] range3 = { 0.001, 1000.0};
        double[][] ranges = { range1, range2, range3};

        int[] sizes = { 10, 100, 1000};

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {

                var crackedMatrix = CrackedMatrix.wellConditionedMatrix(sizes[i], ranges[j][0], ranges[j][1]);
                var xPrecise = new Vector(sizes[i]).fillWithRandomValues(ranges[j][0], ranges[j][1]);

                var vector = CrackedMatrixOperationUnit.multiply(crackedMatrix, xPrecise);
                var result = CrackedMatrixOperationUnit.solveEquation(crackedMatrix, vector);

                System.out.println((3 * i + j + 1) + " тест)");
                System.out.println("Размерность системы: " + sizes[i]);
                System.out.println("Диапазон значений элементов: " + Arrays.toString(ranges[j]));
                System.out.println("Значение погрешности: " +
                        Vector.copyOf(result).subtract(xPrecise).findNorm() + "\n");
            }
        }
    }
}
