package lab.numeric.methods;

import lab.numeric.methods.core.enums.InterpolationMethod;
import lab.numeric.methods.core.enums.SeparationType;
import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.util.DifferenceCalculator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.DecimalFormat;

public class CommonDifferenceTests {

    // x = x0 | func3(x0) = -6
    // x = xN | func1(xN) = -3 * xN^2 + 2 * xN
    private static final Function FUNCTION = x -> -Math.pow(x, 3) + Math.pow(x, 2) + 9;
//    private static final Function FUNCTION = Math::sin;

    @ParameterizedTest
    @ValueSource(strings = { "POLYNOMIAL", "CUBIC_SPLINE" })
    public void degreeTest(String interpolationMethodName) {

        System.out.println("Difference test for method: " + interpolationMethodName);

        var interpolationMethod = InterpolationMethod.valueOf(interpolationMethodName);

        for (
                int i = (interpolationMethod.equals(InterpolationMethod.POLYNOMIAL) ? 5 : 2);
                i <= (interpolationMethod.equals(InterpolationMethod.POLYNOMIAL) ? 70 : 8192);
                i = interpolationMethod.equals(InterpolationMethod.POLYNOMIAL) ? i + 5 : 2 * i
        ) {
            final Section CHEBYSHEV_SECTION =
                    new Section(-5, 5, i, SeparationType.CHEBYSHEV);
            final Section UNIFORM_SECTION =
                    new Section(-5, 5, i, SeparationType.UNIFORM);
            final var maxDiffUniform = DifferenceCalculator.maxDiff(
                    UNIFORM_SECTION,
                    FUNCTION,
                    InterpolationMethod.valueOf(interpolationMethodName)
            );
            final var maxDiffChebyshev = DifferenceCalculator.maxDiff(
                    CHEBYSHEV_SECTION,
                    FUNCTION,
                    interpolationMethod
            );

            DecimalFormat df = new DecimalFormat("0.###E0");
            System.out.println(
                    "|n=" + i +
                            "\t\t|uniformMD = " + df.format(maxDiffUniform) +
                            "\t\t\t|chebyshevMD = " + df.format(maxDiffChebyshev)
            );
        }

        System.out.println();
    }
}
