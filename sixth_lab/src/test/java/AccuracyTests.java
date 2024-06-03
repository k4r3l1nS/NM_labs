import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import org.junit.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

import static lab.numeric.methods.core.methods.RungeKuttMethod.rk4Method;
import static lab.numeric.methods.core.methods.ShootingMethod.newton;

public class AccuracyTests {

    private static final Section SECTION = new Section(
            0,
            Math.PI / 2,
            100,
            SeparationType.UNIFORM
    );
    private static final double MU0 = 1.0;
    private static final double NU0 = 0.0;
    private static final double A = 1.0;
    private static final double B = 0.0;
    private static final double EPS = 1e-6;
    private static final int ITERATION_LIMIT = 1_000_000;


    // Уравнение U'' + U = 0
    private static final BiFunction<Double, double[], double[]> ODE = (t, y) -> {
        double u = y[0];
        double du = y[1];
        double ddu = -u;
        return new double[]{du, ddu};
    };

    // Аналитическое решение: U(t) = cos(t)
    private static final Function<Double, Double> ANALYTICAL_FUNCTION = Math::cos;

    // Аналитическое решение: U'(t) = -sin(t)
    private static final Function<Double, Double> ANALYTICAL_DERIVATIVE = (t) -> -Math.sin(t);

    @Test
    public void testAccuracy() {

        double du0 = newton(
                MU0,
                NU0,
                SECTION.getA(),
                SECTION.getB(),
                A,
                B,
                ODE,
                EPS,
                ITERATION_LIMIT
        );
        double[] y0 = {A / MU0, du0};

        // Решение задачи Коши с найденным значением U'(a)
        double[][] result = rk4Method(
                ODE,
                y0,
                SECTION.getA(),
                SECTION.getB(),
                SECTION.getN()
        );

        double maxDiff = 0.0;
        for (int i = 0; i < result.length; i++) {
            double t = SECTION.getA() + i * (SECTION.getB() - SECTION.getA()) / 100;
            double diff = Math.abs(result[i][0] - ANALYTICAL_FUNCTION.apply(t));
            if (diff > maxDiff) {
                maxDiff = diff;
            }
        }

        System.out.println("Погрешность метода составляет " + maxDiff);
    }
}
