package lab.numeric.methods;

import lab.numeric.methods.core.methods.ShootingMethod;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.gui.SimpleGUI;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SixthLabApplication {

    private static final Section SECTION = new Section(
            0,
            5 * Math.PI / 2,
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

    public static void main(String[] args) {

        double[][] result = ShootingMethod.apply(
                MU0,
                NU0,
                SECTION.getA(),
                SECTION.getB(),
                SECTION.getN(),
                A,
                B,
                ODE,
                ANALYTICAL_FUNCTION,
                EPS,
                ITERATION_LIMIT
        );

        SimpleGUI app = new SimpleGUI(
                SECTION.getSeparation(),
                result,
                ANALYTICAL_FUNCTION,
                ANALYTICAL_DERIVATIVE
        );
        app.setVisible(true);
    }
}
