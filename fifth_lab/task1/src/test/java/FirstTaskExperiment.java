import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.core.models.methods.DESolvingMethod;
import lab.numeric.methods.core.models.methods.impl.EulerMethod;
import lab.numeric.methods.core.models.methods.impl.RungeKuttMethod;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FirstTaskExperiment {

    // y' = ...
    private final static Function RIGHT_HAND_FUNCTION = (x, y) -> 1 + 0.8 * y * Math.sin(x) - 2 * y * y;

    private final static Section SECTION = new Section(
            0, 5, 2, SeparationType.UNIFORM
    );

    private final static double y0 = 0;

    @ParameterizedTest
    @ValueSource(doubles = {1e-2, 1e-3, 1e-4, 1e-5, 1e-6, 1e-7, 1e-8, 1e-9})
    public void testCorrectness(double eps) {
        System.out.println("Test for eps = " + eps);
        DESolvingMethod eulerMethod = new EulerMethod(RIGHT_HAND_FUNCTION, SECTION.getSeparation(), y0, eps);
        DESolvingMethod rungeKuttMethod = new RungeKuttMethod(RIGHT_HAND_FUNCTION, SECTION.getSeparation(), y0, eps);
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
