package lab.numeric.methods;

import lab.numeric.methods.core.enums.InterpolationMethod;
import lab.numeric.methods.core.models.impl.CubicSpline;
import lab.numeric.methods.core.models.InterpolatedFunction;
import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.enums.SeparationType;
import lab.numeric.methods.core.models.impl.Polynomial;
import lab.numeric.methods.gui.SimpleGUI;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CorrectnessTests {

    private static final Function FUNCTION = x -> -Math.pow(x, 3) + Math.pow(x, 2) + 9;

    private static final Section SECTION =
            new Section(0, 1.5, 3, SeparationType.CHEBYSHEV);

    private static final double a = -6;
    private static final double b = -65;


    @ParameterizedTest
    @ValueSource(strings = { "POLYNOMIAL", "CUBIC_SPLINE" })
    public void correctnessTest(String interpolationMethodName) {

        System.out.println("Correctness test for method: " + interpolationMethodName + "\n");
        var interpolationMethod = InterpolationMethod.valueOf(interpolationMethodName);

        var args = SECTION.getSeparation();

        System.out.println("f(x0), ..., f(xN):");
        for (var arg : args) {
            System.out.println(FUNCTION.calculateExpression(arg));
        }
        System.out.println();

        InterpolatedFunction interpolatedFunction = interpolationMethod.equals(InterpolationMethod.POLYNOMIAL) ?
                new Polynomial(FUNCTION, SECTION) :
                new CubicSpline(FUNCTION, SECTION, a, b);

        System.out.println("interp_func(x0), ..., interp_func(xN):");
        for (var arg : args) {
            System.out.println(interpolatedFunction.calculateExpression(arg));
        }
        System.out.println();

        if (interpolatedFunction instanceof CubicSpline cubicSpline) {
            System.out.println("(a, b) = (" + a + ", " + b + ")");
            System.out.println();
            System.out.println("s1(x0), s3(xN):");
            System.out.println(cubicSpline.calculateExpression(args[0], 3));
            System.out.println(cubicSpline.calculateExpression(args[args.length - 1], 1));
        }

        System.out.println();
        System.out.println("Continuity of function (step by 1e-7):");
        System.out.println(interpolatedFunction.calculateExpression(args[1] - 5e-8));
        System.out.println(interpolatedFunction.calculateExpression(args[1] + 5e-8));
        System.out.println("Difference: " +
                Math.abs(
                        interpolatedFunction.calculateExpression(args[1] - 5e-8)
                                - interpolatedFunction.calculateExpression(args[1] + 5e-8)
                )
        );

        System.out.println("\n");
    }
}
