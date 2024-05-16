import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.core.models.methods.DESolvingMethod;
import lab.numeric.methods.core.models.methods.impl.EulerMethod;
import lab.numeric.methods.core.models.methods.impl.RungeKuttMethod;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SecondPartTests {

    // u1' = ...
    private final static Function U1_RIGHT_HAND_FUNCTION = (x, u1, u2) -> -2 * u1 / x;

    // u2' = ...
    private final static Function U2_RIGHT_HAND_FUNCTION = (x, u1, u2) -> u2 + (2 + x) * u1 / x;

    // u1 = 1 / x^2
    private final static Function U1_ANALYTICAL_FUNCTION = (x, u1, u2) -> 1 / (x * x);

    // u2 = e^x - 1 / x^2
    private final static Function U2_ANALYTICAL_FUNCTION = (x, u1, u2) -> Math.exp(x) - 1 / (x * x);

    private final static Section SECTION = new Section(
            1, 3, 2, SeparationType.UNIFORM
    );

    private final static double u10 = 1;
    private final static double u20 = Math.E - 1;

    @ParameterizedTest
    @ValueSource(doubles = {1e-2, 1e-3, 1e-4, 1e-5, 1e-6, 1e-7, 1e-8, 1e-9})
    public void testCorrectness(double eps) {
        System.out.println("Test for eps = " + eps);
        DESolvingMethod eulerMethod = new EulerMethod(
                U1_RIGHT_HAND_FUNCTION,
                U2_RIGHT_HAND_FUNCTION,
                SECTION.getSeparation(),
                u10,
                u20,
                eps,
                U1_ANALYTICAL_FUNCTION.calculateExpression(SECTION.getB(), Double.NaN, Double.NaN),
                U2_ANALYTICAL_FUNCTION.calculateExpression(SECTION.getB(), Double.NaN, Double.NaN)
        );
        DESolvingMethod rungeKuttMethod = new RungeKuttMethod(
                U1_RIGHT_HAND_FUNCTION,
                U2_RIGHT_HAND_FUNCTION,
                SECTION.getSeparation(),
                u10,
                u20,
                eps,
                U1_ANALYTICAL_FUNCTION.calculateExpression(SECTION.getB(), Double.NaN, Double.NaN),
                U2_ANALYTICAL_FUNCTION.calculateExpression(SECTION.getB(), Double.NaN, Double.NaN)
        );
        System.out.println(
                "Result for Euler method: N = " + Math.round(
                        (
                                eulerMethod.getX()[eulerMethod.getX().length - 1] - eulerMethod.getX()[0]
                        ) / eulerMethod.getH()
                ) + " | " + eulerMethod.getErrorIndicator() + "\n" +
                        "Result for Runge-Kutt method: N = " + Math.round(
                        (
                                rungeKuttMethod.getX()[rungeKuttMethod.getX().length - 1] - rungeKuttMethod.getX()[0]
                        ) / rungeKuttMethod.getH()
                ) + " | " + rungeKuttMethod.getErrorIndicator() + "\n"
        );
    }
}
