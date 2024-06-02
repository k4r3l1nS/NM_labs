import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.core.models.methods.DESolvingMethod;
import lab.numeric.methods.core.models.methods.impl.EulerMethod;
import lab.numeric.methods.core.models.methods.impl.RungeKuttMethod;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FirstTaskTests {

    // y' = ...
    private final static Function RIGHT_HAND_FUNCTION = (x, y) -> x / 3 - y;

    // Result of pure calculations
    private final static Function ANALYTICAL_FUNCTION = (x, y) -> 71 * Math.exp(1.4 - x) / 30 + x / 3 - 1.0 / 3;

    private final static Section SECTION = new Section(
            1.4, 5, 2, SeparationType.UNIFORM
    );

    private final static double y0 = 2.5;

    @ParameterizedTest
    @ValueSource(doubles = {1e-2, 1e-3, 1e-4, 1e-5, 1e-6, 1e-7, 1e-8, 1e-9})
    public void testCorrectness(double eps) {
        System.out.println("Test for eps = " + eps);
        DESolvingMethod eulerMethod = new EulerMethod(
                RIGHT_HAND_FUNCTION,
                SECTION.getSeparation(),
                y0,
                eps,
                ANALYTICAL_FUNCTION.calculateExpression(SECTION.getB(), Double.NaN)
        );
        DESolvingMethod rungeKuttMethod = new RungeKuttMethod(
                RIGHT_HAND_FUNCTION,
                SECTION.getSeparation(),
                y0,
                eps,
                ANALYTICAL_FUNCTION.calculateExpression(SECTION.getB(), Double.NaN)
        );
        System.out.println(
                "Result for Euler method: N = " + Math.round(
                        (
                                eulerMethod.getX()[eulerMethod.getX().length - 1] - eulerMethod.getX()[0]
                        ) / eulerMethod.getH()
                ) + "\n" +
                "Result for Runge-Kutt method: N = " + Math.round(
                        (
                                rungeKuttMethod.getX()[rungeKuttMethod.getX().length - 1] - rungeKuttMethod.getX()[0]
                        ) / rungeKuttMethod.getH()
                ) + "\n"
        );
    }
}
