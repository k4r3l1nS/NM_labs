package lab.numeric.methods;

import com.k4r3l1ns.models.TapeMatrix;
import com.k4r3l1ns.models.Vector;
import com.k4r3l1ns.service.TapeMatrixOperationUnit;

public class EquationSolver {

    private static final int SIZE = 4;

    public static double[][] apply(double[] args, double[] fValues, double a, double b) {

        var h = new double[args.length];
        for (int i = 1; i < args.length; ++i) {
            h[i] = args[i] - args[i - 1];
        }

        var coefficients = new double[args.length][SIZE];

        // коэффициенты a: из s(x_i) = f(x_i)
        for (int i = 0; i < args.length; ++i) {
            coefficients[i][0] = fValues[i];
        }

        final int tapeWidth = 2;
        var cCoeffMatrix = new TapeMatrix(args.length, tapeWidth);
        var matrixValueVector = new Vector((2 * tapeWidth - 1) * args.length);
        for (int i = 3; i < (2 * tapeWidth - 1) * args.length - 3; ++i) {
            int line = i / (2 * tapeWidth - 1);
            double value = switch (i % 3) {
                case 0 -> h[line];
                case 1 -> (h[line] + h[line + 1]) * 2;
                case 2 -> h[line + 1];
                default -> 0;
            };
            matrixValueVector.setValueAt(i, value);
        }
        matrixValueVector.setValueAt(1, -1);
        matrixValueVector.setValueAt(2, 1);
        matrixValueVector.setValueAt((2 * tapeWidth - 1) * args.length - 3, 0.5);
        matrixValueVector.setValueAt((2 * tapeWidth - 1) * args.length - 2, 1);

        cCoeffMatrix.setValues(matrixValueVector);

        var fVector = new Vector(args.length);
        for (int i = 1; i < args.length - 1; ++i) {
            var value1 = (fValues[i + 1] - fValues[i]) / h[i + 1];
            var value2 = (fValues[i] - fValues[i - 1]) / h[i];
            fVector.setValueAt(i, 6 * (value1 - value2));
        }
        fVector.setValueAt(0, h[1] * a);
        fVector.setValueAt(
                args.length - 1,
                (b - (fValues[args.length - 1] - fValues[args.length - 2]) / h[args.length - 1])
                        * 3 / h[args.length - 1]
        );

        var cCoeffs = TapeMatrixOperationUnit.solveEquation(cCoeffMatrix, fVector);

        for (int i = 0; i < args.length; ++i) {
            coefficients[i][2] = cCoeffs.getValueAt(i);
        }

        for (int i = 1; i < args.length; ++i) {
            coefficients[i][3] = (coefficients[i][2] - coefficients[i - 1][2]) / h[i];
        }

        // { f(i) - f(i-1) - (h[i]^3 / 6 * coef[i][3]) + (h[i]^2 / 2 * coef[i][2]) } / h[i]
        for (int i = 1; i < args.length; ++i) {
            coefficients[i][1] = (fValues[i] - (fValues[i - 1]) - Math.pow(h[i], 3) / 6 * coefficients[i][3]
                    + Math.pow(h[i], 2) * 0.5 * coefficients[i][2]) / h[i];
        }

        return coefficients;
    }
}
