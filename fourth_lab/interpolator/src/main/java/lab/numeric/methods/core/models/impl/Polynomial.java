package lab.numeric.methods.core.models.impl;

import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.enums.InterpolationMethod;
import lab.numeric.methods.core.enums.SeparationType;
import lab.numeric.methods.core.models.InterpolatedFunction;

import java.math.BigInteger;

public class Polynomial extends InterpolatedFunction {
    private final double[] fValues;
    private final double[] args;
    private double[] b;

    public Polynomial(Function function, Section section) {
        super(section, InterpolationMethod.POLYNOMIAL);
        args = section.getSeparation();
        fValues = new double[args.length];
        for (int j = 0; j < args.length; j++) {
            fValues[j] = function.calculateExpression(args[j]);
        }
        if (section.getSeparationType().equals(SeparationType.UNIFORM)) {
            uniformSplitDifference();
        } else {
            chebyshevSplitDifference();
        }
    }

    @Override
    public double calculateExpression(double x) {
        double polynom = 0;
        double expression;
        for (int i = 0; i < b.length; i++) {
            expression = b[i];
            for (int j = 0; j < i; j++) {
                expression *= x - args[j];
            }
            polynom += expression;
        }
        return polynom;
    }

    private void chebyshevSplitDifference() {
        b = new double[args.length];
        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j <= i; j++) {
                double denominator = 1;
                for (int k = 0; k <= i; k++) {
                    if (k != j) {
                        denominator = denominator * (args[j] - args[k]);
                    }
                }
                b[i] += fValues[j] / denominator;
            }
        }
    }

    private void uniformSplitDifference() {
        b = new double[args.length];
        var h = args[1] - args[0];
        for (int k = 0; k < args.length; k++) {
            for (int i = 0; i <= k; i++) {
                // i! * (k - i)! * h^k
                double denominator = (
                        factorial(
                                BigInteger.valueOf(i)
                        ).multiply(
                                factorial(
                                        BigInteger.valueOf(k).subtract(BigInteger.valueOf(i))
                                )
                        )
                ).doubleValue() * (Math.pow(h, k));
                b[k] += fValues[i] * Math.pow(-1, k - i) / denominator;
            }
        }
    }

    private BigInteger factorial(BigInteger x) {
        if (x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE)) {
            return BigInteger.ONE;
        } else {
            return x.multiply(factorial(x.subtract(BigInteger.ONE)));
        }
    }
}
