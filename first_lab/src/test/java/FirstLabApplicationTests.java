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
    public void testIncorrectnessByReverseChecking()
            throws FileNotFoundException {

        var crackedMatrix = CrackedMatrix.read(new FileInputStream(MATRIX_FILE));
        var xPrecise = Vector.read(new FileInputStream(X_PRECISE_FILE));

        var vector = MatrixOperationUnit.multiply(crackedMatrix, xPrecise);
        var result = MatrixOperationUnit.solveEquation(crackedMatrix, vector);

        System.out.println("\n\nПогрешность составляет: " + result.subtract(xPrecise).findNorm() + "\n\n");
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 50, 500, 5000})
    public void testIncorrectnessByDimension(int size) {

        CrackedMatrix crackedMatrix = new CrackedMatrix(size);

        crackedMatrix.getSideDiagonal().fillWithRandomValues(0.0, 100.0);
        crackedMatrix.getLSideDiagonal().fillWithRandomValues(0.0, 100.0);
        crackedMatrix.getRSideDiagonal().fillWithRandomValues(0.0, 100.0);
        crackedMatrix.getUCrackedLine().fillWithRandomValues(0.0, 100.0);
        crackedMatrix.getDCrackedLine().fillWithRandomValues(0.0, 100.0);

        var xPrecise = new Vector(size).fillWithRandomValues(0.0, 100.0);

        var vector = MatrixOperationUnit.multiply(crackedMatrix, xPrecise);
        var result = MatrixOperationUnit.solveEquation(crackedMatrix, vector);

        System.out.println("\n\nПогрешность при размерности " + size +
                " составляет: " + result.subtract(xPrecise).findNorm() + "\n\n");
    }

    @ParameterizedTest
    @ValueSource(doubles = {5.0, 5000.0, 5000000.0, 5000000000.0})
    public void testIncorrectnessByDoubles(double maxDouble) {

        final int size = 1000;

        CrackedMatrix crackedMatrix = new CrackedMatrix(size);

        crackedMatrix.getSideDiagonal().fillWithRandomValues(0.9 * maxDouble, maxDouble);
        crackedMatrix.getLSideDiagonal().fillWithRandomValues(0.9 * maxDouble, maxDouble);
        crackedMatrix.getRSideDiagonal().fillWithRandomValues(0.9 * maxDouble, maxDouble);
        crackedMatrix.getUCrackedLine().fillWithRandomValues(0.9 * maxDouble, maxDouble);
        crackedMatrix.getDCrackedLine().fillWithRandomValues(0.9 * maxDouble, maxDouble);

        var xPrecise = new Vector(size).fillWithRandomValues(0.9 * maxDouble, maxDouble);

        var vector = MatrixOperationUnit.multiply(crackedMatrix, xPrecise);
        var result = MatrixOperationUnit.solveEquation(crackedMatrix, vector);

        System.out.println("\n\nПогрешность при диапазоне [" + 0.9 * maxDouble + ", " + maxDouble +
                "] составляет: " + result.subtract(xPrecise).findNorm() + "\n\n");
    }
}
