import com.k4r3l1ns.models.TapeMatrix;
import com.k4r3l1ns.models.Vector;
import com.k4r3l1ns.service.TapeMatrixOperationUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SecondPartTests {

    public static final int SIZE = 7;

    public static final int TAPE_WIDTH = 3;

    public static final double[] RANGE = {1.0, 10.0};

    public static int testNum = 1;

    @ParameterizedTest
    @ValueSource(
            ints = { 50, 500 }
    )
    public void testWellConditionedMatrix(int size) {

        double[] coefficientLToN = { 0.1, 0.25, 0.5 };

        for (double coefficient : coefficientLToN) {

            int tapeWidth = (int) (size / coefficient);

            TapeMatrix wellConditionedMatrix = TapeMatrix.wellConditionedMatrix(size, tapeWidth, RANGE);
            Vector xPrecise = new Vector(size).fillWithRandomValues(RANGE[0], RANGE[1]);

            Vector goodVector = TapeMatrixOperationUnit.multiply(wellConditionedMatrix, xPrecise);

            System.out.println(testNum++ + " тест)");
            System.out.println("Размерность системы: " + size);
            System.out.format("Соотношение L/N: %.2f\n", coefficient);
            System.out.println(
                    "Погрешность для хорошо обусловленной матрицы: " +
                    TapeMatrixOperationUnit.solveEquation(wellConditionedMatrix, goodVector)
                            .subtract(xPrecise).findNorm() + "\n"
            );
        }
    }

    @ParameterizedTest
    @ValueSource(
            doubles = { 100, 10000, 1000000 }
    )
    public void testPoorConditionedMatrix(double divisor) {

        int[] sizes = { 30, 70 };

        for (int size : sizes) {

            int tapeWidth = size / 10;

            TapeMatrix poorConditionedMatrix = TapeMatrix.poorConditionedMatrix(size, tapeWidth, RANGE, divisor);
            Vector xPrecise = new Vector(size).fillWithRandomValues(RANGE[0], RANGE[1]);

            Vector poorVector = TapeMatrixOperationUnit.multiply(poorConditionedMatrix, xPrecise);

            System.out.println(testNum++ + " тест)");
            System.out.println("Параметр обусловленности (делитель): " + divisor);
            System.out.println("Размерность системы: " + size);
            System.out.println(
                    "Погрешность для плохо обусловленной матрицы: " +
                            TapeMatrixOperationUnit.solveEquation(poorConditionedMatrix, poorVector)
                                    .subtract(xPrecise).findNorm() + "\n\n"
            );
        }
    }

    @Test
    public void testLU() {
        TapeMatrix tapeMatrix = TapeMatrix.wellConditionedMatrix(SIZE, TAPE_WIDTH, RANGE);
        Vector xPrecise = new Vector(SIZE).fillWithRandomValues(RANGE[0], RANGE[1]);

        var vector = TapeMatrixOperationUnit.multiply(tapeMatrix, xPrecise);

        var a = TapeMatrix.copyOf(tapeMatrix);
        var f = Vector.copyOf(vector);

        if (a.getSize() != f.getSize()) {
            throw new RuntimeException("Размерности матрицы и вектора несовместимы");
        }

        int size = a.getSize();
        int tapeWidth = a.getTapeWidth();

        TapeMatrix b = new TapeMatrix(size, tapeWidth);
        TapeMatrix c = new TapeMatrix(size, tapeWidth);
    
        for (int i = 0; i < size; ++i) {
            b.setValueAt(i, i, 1.0);
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {

                double valueToSubtract = 0.0;
                double aValue = a.valueAt(i, j);

                if (i <= j) {
                    for (int k = a.k0(j); k <= i; ++k) {
                        valueToSubtract += b.valueAt(i, k) * c.valueAt(k, j);
                    }
                    c.setValueAt(i, j, aValue - valueToSubtract);
                } else {
                    for (int k = a.k0(i); k <= j; ++k) {
                        valueToSubtract += b.valueAt(i, k) * c.valueAt(k, j);
                    }
                    b.setValueAt(i, j, (aValue - valueToSubtract) / c.valueAt(j, j));
                }
            }
        }

        System.out.println("Матрица B:");
        b.print();
        System.out.println("\nМатрица C:");
        c.print();
        System.out.println("\nИсходная матрица A:");
        a.print();
        System.out.println("\nМатрица B x C:");
        TapeMatrixOperationUnit.multiply(b, c).print();
        System.out.println();
    }
}
