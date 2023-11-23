import com.k4r3l1ns.models.CrackedMatrix;
import com.k4r3l1ns.models.Vector;
import com.k4r3l1ns.service.MatrixOperationUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FirstLabApplicationTests {

    private static final String FILES_DIRECTORY = "/home/k4r3l1ns/Desktop/NM_labs/first_lab/src/main/resources/";

    private static final File MATRIX_FILE = new File(FILES_DIRECTORY + "matrix.txt");
    private static final File X_PRECISE_FILE = new File(FILES_DIRECTORY + "precise.txt");

    @Test
    public void testIncorrectnessByReverseChecking() {

        int size = 1000;

        var crackedMatrix = CrackedMatrix.wellConditionedMatrix(size, 0.0, 100.0);
        var xPrecise = new Vector(size).fillWithRandomValues(0.0, 100.0);

        var vector = MatrixOperationUnit.multiply(crackedMatrix, xPrecise);
        var result = MatrixOperationUnit.solveEquation(crackedMatrix, vector);

        System.out.println("\nПогрешность при сравнении точного решения с полученным составляет: " +
                result.subtract(xPrecise).findNorm() + "\n\n");
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 50, 500, 5000})
    public void testIncorrectnessByDimension(int size) {

        CrackedMatrix crackedMatrix = CrackedMatrix.wellConditionedMatrix(size, 0.0, 100.0);

        var xPrecise = new Vector(size).fillWithRandomValues(0.0, 100.0);

        var vector = MatrixOperationUnit.multiply(crackedMatrix, xPrecise);
        var result = MatrixOperationUnit.solveEquation(crackedMatrix, vector);

        System.out.println("Погрешность при размерности " + size +
                " составляет: " + result.subtract(xPrecise).findNorm());
    }

    @Test
    public void testPoorConditionedMatrix() {

        final int size = 1000;

        CrackedMatrix poorConditionedMatrix = CrackedMatrix.poorConditionedMatrix(size, 0.0, 100.0);
        CrackedMatrix wellConditionedMatrix = CrackedMatrix.wellConditionedMatrix(size, 0.0, 100.0);

        var xPrecise = new Vector(size).fillWithRandomValues(0.0, 100.0);

        var poorVector = MatrixOperationUnit.multiply(poorConditionedMatrix, xPrecise);
        var goodVector = MatrixOperationUnit.multiply(wellConditionedMatrix, xPrecise);

        var poorResult = MatrixOperationUnit.solveEquation(poorConditionedMatrix, poorVector);
        var goodResult = MatrixOperationUnit.solveEquation(wellConditionedMatrix, goodVector);

        System.out.println("\nЗначение погрешности хорошо обусловленной матрицы: "
                + goodResult.subtract(xPrecise).findNorm());
        System.out.println("Значение погрешности плохо обусловленной матрицы: "
                + poorResult.subtract(xPrecise).findNorm() + "\n");
    }

    @ParameterizedTest
    @ValueSource(doubles = {5.0, 5000.0, 5000000.0, 5000000000.0})
    public void testIncorrectnessByDoubles(double maxDouble) {

        final int size = 1000;

        CrackedMatrix crackedMatrix = CrackedMatrix.wellConditionedMatrix(size, 0.9 * maxDouble, maxDouble);
        var xPrecise = new Vector(size).fillWithRandomValues(0.9 * maxDouble, maxDouble);

        var vector = MatrixOperationUnit.multiply(crackedMatrix, xPrecise);
        var result = MatrixOperationUnit.solveEquation(crackedMatrix, vector);

        System.out.println("Погрешность при диапазоне [" + 0.9 * maxDouble + ", " + maxDouble +
                "] составляет: " + result.subtract(xPrecise).findNorm());
    }

    @Test
    public void testProcessingCorrectness()
            throws FileNotFoundException {

        var a = CrackedMatrix.read(new FileInputStream(MATRIX_FILE));
        var xPrecise = Vector.read(new FileInputStream(X_PRECISE_FILE));

        var f = MatrixOperationUnit.multiply(a, xPrecise);

        if (a.getSize() != f.getSize()) {
            throw new RuntimeException("Размерности матрицы и вектора несовместимы");
        }

        a = CrackedMatrix.copyOf(a);

        int size = a.getSize();

        // Верхняя кодиагональ
        var lSideDiagonal = a.getLSideDiagonal();

        // Побочная диагональ
        var sideDiagonal = a.getSideDiagonal();

        // Нижняя кодиагональ
        var rSideDiagonal = a.getRSideDiagonal();

        var uCrackedLine = a.getUCrackedLine();
        var dCrackedLine = a.getDCrackedLine();

        System.out.println("Проверка на заданной матрице: ");
        a.print();
        System.out.println("\nИ точном решении: ");
        xPrecise.print();
        System.out.println("\nВектор правой части имеет вид: ");
        f.print();
        System.out.println("\nПогрешность в начале вычислений составляет: " +
                MatrixOperationUnit.solveEquation(a, Vector.copyOf(f)).subtract(xPrecise).findNorm());

        // Шаг 2 (шаг 1 пропускается, т.к. первая "испорченная" диагональ на верхней строке матрицы)
        for (int i = size - 1; i > 1; --i) {

            var r = 1 / sideDiagonal.getValueAt(i);
            sideDiagonal.setValueAt(i, 1.0);
            rSideDiagonal.multiplyValueAt(i, r);
            f.multiplyValueAt(i, r);

            if (i > 2) {
                r = lSideDiagonal.getValueAt(i - 1);
                lSideDiagonal.setValueAt(i - 1, 0.0);
                sideDiagonal.subtractValueAt(i - 1, r * rSideDiagonal.getValueAt(i));
                f.subtractValueAt(i - 1, r * f.getValueAt(i));
            }

            r = dCrackedLine.getValueAt(size - 1 - i);
            dCrackedLine.setValueAt(size - 1 - i, 0.0);
            dCrackedLine.subtractValueAt(size - i, r * rSideDiagonal.getValueAt(i));
            f.subtractValueAt(1, r * f.getValueAt(i));

            r = uCrackedLine.getValueAt(size - 1 - i);
            uCrackedLine.setValueAt(size - 1 - i, 0.0);
            uCrackedLine.subtractValueAt(size - i, r * rSideDiagonal.getValueAt(i));
            f.subtractValueAt(0, r * f.getValueAt(i));

            System.out.println("Погрешность на этапе " + (size - i) + " составляет: " +
                    MatrixOperationUnit.solveEquation(a, Vector.copyOf(f)).subtract(xPrecise).findNorm());
        }

        var r = 1 / dCrackedLine.getValueAt(size - 2);
        dCrackedLine.setValueAt(size - 2, 1.0);
        dCrackedLine.multiplyValueAt(size - 1, r);
        f.multiplyValueAt(1, r);

        r = uCrackedLine.getValueAt(size - 2);
        uCrackedLine.setValueAt(size - 2, 0.0);
        uCrackedLine.subtractValueAt(size - 1, r * dCrackedLine.getValueAt(size - 1));
        f.subtractValueAt(0, r * f.getValueAt(1));

        r = 1 / uCrackedLine.getValueAt(size - 1);
        uCrackedLine.setValueAt(size - 1, 1.0);
        f.multiplyValueAt(0, r);

        // Значения "cracked lines" верны, осталось подогнать под них диагонали 0 и 1 рядов
        lSideDiagonal.setValueAt(0, 0.0);
        sideDiagonal.setValueAt(0, uCrackedLine.getValueAt(size - 1));
        sideDiagonal.setValueAt(1, dCrackedLine.getValueAt(size - 2));
        rSideDiagonal.setValueAt(1, dCrackedLine.getValueAt(size - 1));

        System.out.println("Погрешность на последнем этапе составляет: " +
                MatrixOperationUnit.solveEquation(a, Vector.copyOf(f)).subtract(xPrecise).findNorm());

        var x = new Vector(size);

        x.setValueAt(size - 1, f.getValueAt(0));
        for (int i = size - 2; i >= 0; --i) {
            x.setValueAt(
                    i,
                    f.getValueAt(size - 1 - i) - rSideDiagonal.getValueAt(size - 1 - i) * x.getValueAt(i + 1)
            );
        }

        System.out.println("\nОкончательная погрешность составляет: " + x.subtract(xPrecise).findNorm() + "\n");
    }
}
