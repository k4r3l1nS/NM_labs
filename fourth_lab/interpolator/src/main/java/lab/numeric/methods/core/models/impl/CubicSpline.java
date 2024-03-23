package lab.numeric.methods.core.models.impl;

import lab.numeric.methods.EquationSolver;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.enums.InterpolationMethod;
import lab.numeric.methods.core.enums.SeparationType;
import lab.numeric.methods.core.models.InterpolatedFunction;

public class CubicSpline extends InterpolatedFunction {

    private final double[] args;

    /**
     * Коэффициенты кубического сплайна: a(i), b(i), c(i) и d(i)
     */
    private final double[][] coefficients;


    public CubicSpline(Function function, Section section, double a, double b) {
        super(section, InterpolationMethod.CUBIC_SPLINE);
        this.args = section.getSeparation();
        if (section.getSeparationType().equals(SeparationType.CHEBYSHEV)) {
            for (int i = 0; i < this.args.length / 2; ++i) {
                var tmp = args[i];
                args[i] = args[args.length - 1 - i];
                args[args.length - 1 - i] = tmp;
            }
        }
        var fValues = new double[args.length];
        for (int j = 0; j < args.length; j++) {
            fValues[j] = function.calculateExpression(args[j]);
        }
        coefficients = EquationSolver.apply(args, fValues, a, b);
    }

    @Override
    public double calculateExpression(double x) {

        var i = locateIndex(x);

        var h_i = x - args[i];

        return coefficients[i][0]
                + h_i * coefficients[i][1]
                + 0.5 * coefficients[i][2] * Math.pow(h_i, 2)
                + coefficients[i][3] / 6 * Math.pow(h_i, 3);
    }

    /**
     * Вычисление значения n-ой производной сплайна в точке x
     *
     * @param x Точка
     * @param derivativeNumber Порядок производной (0+)
     * @return Значение производной
     */
    public double calculateExpression(double x, int derivativeNumber) {

        if (derivativeNumber < 0) {
            throw new ArithmeticException("Значение производной не может быть ниже 0");
        }

        if (derivativeNumber == 0) {
            return calculateExpression(x);
        }

        var i = locateIndex(x);

        var h_i = x - args[i];

        if (derivativeNumber == 1) {
            return coefficients[i][1] + coefficients[i][2] * h_i + 0.5 * coefficients[i][3] * Math.pow(h_i, 2);
        }

        if (derivativeNumber == 2) {
            return coefficients[i][2] + coefficients[i][3] * h_i;
        }

        if (derivativeNumber == 3) {
            return coefficients[i][3];
        }

        return 0;
    }

    private int locateIndex(double x) {

        var index = args.length / 2;

        int leftBound = 0;
        int rightBound = args.length;
        while (
                index != 0 && index != args.length - 1 &&
                !(x >= args[index - 1] && x <= args[index])
        ) {
            if (x > (args[index])) {
                leftBound = index;
            } else {
                rightBound = index;
            }
            index = (leftBound + rightBound) / 2;
        }

        return index == 0 ? 1 : index;
    }
}
