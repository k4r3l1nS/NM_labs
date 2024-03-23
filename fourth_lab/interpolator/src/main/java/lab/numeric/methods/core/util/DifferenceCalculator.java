package lab.numeric.methods.core.util;

import lab.numeric.methods.core.enums.InterpolationMethod;
import lab.numeric.methods.core.enums.SeparationType;
import lab.numeric.methods.core.models.impl.CubicSpline;
import lab.numeric.methods.core.models.impl.Polynomial;
import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.InterpolatedFunction;
import lab.numeric.methods.core.models.Section;

public class DifferenceCalculator {

    private static final double A = -6;
    private static final double B_UNIFORM = -65;
    private static final double B_CHEBYSHEV = 0;

    private DifferenceCalculator() {
        throw new IllegalStateException("Utility class");
    }

    public static double maxDiff(Section section, Function sourceFunc, InterpolationMethod interpolationMethod) {

        InterpolatedFunction interpFunc = interpolationMethod.equals(InterpolationMethod.POLYNOMIAL) ?
                new Polynomial(sourceFunc, section) :
                new CubicSpline(
                        sourceFunc,
                        section,
//                        ThreadLocalRandom.current().nextDouble(GENERATION_RANGE[0], GENERATION_RANGE[1]),
//                        ThreadLocalRandom.current().nextDouble(GENERATION_RANGE[0], GENERATION_RANGE[1])
                        A,
                        section.getSeparationType().equals(SeparationType.UNIFORM) ?
                                B_UNIFORM : B_CHEBYSHEV
                );
        double maxDiff = 0;

        var step = (section.getB() - section.getA()) / section.getN();
        var args = new Section(
                section.getA() + step / 2,
                section.getB() - step / 2,
                section.getN() - 1,
                section.getSeparationType()
        ).getSeparation();

        var fValues = new double[args.length];
        var interpFuncValues = new double[args.length];
        var delta = new double[args.length];

        for (int i = 0; i < args.length; i++) {
            fValues[i] = sourceFunc.calculateExpression(args[i]);
            interpFuncValues[i] = interpFunc.calculateExpression(args[i]);
            delta[i] = Math.abs(fValues[i] - interpFuncValues[i]);
            if (delta[i] > maxDiff) {
                maxDiff = delta[i];
            }

        }
        return maxDiff;
    }
}
