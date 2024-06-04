package lab.numeric.methods.core.methods;

import java.util.function.BiFunction;
import java.util.function.Function;

import static lab.numeric.methods.core.methods.RungeKuttMethod.rk4Method;

public class ShootingMethod {

    private static final int RK_SPLIT = 100;

    private static double newton(
            double mu0,
            double nu0,
            double a,
            double b,
            double A,
            double B,
            BiFunction<Double, double[], double[]> ode,
            double eps,
            int maxIter
    ) {
        double du0 = 0.0; // Начальное предположение для U'(a)

        for (int iter = 0; iter < maxIter; ++iter) {
            double u0 = (A - nu0 * du0) / mu0; // Вычисляем U(a) исходя из граничного условия
            double[] y0 = {u0, du0}; // Массив начальных условий
            double[][] yb = rk4Method(
                    ode,
                    y0,
                    a,
                    b,
                    RK_SPLIT
            ); // Решение задачи Коши методом Рунге-Кутта

            double F = yb[yb.length - 1][0] - B; // Разница между решением и граничным условием в точке b

            if (Math.abs(F) < eps) {
                return du0; // Возвращаем корректное значение U'(a), если достигнута требуемая точность
            }

            double[] y0Prime = {(A - nu0 * (du0 + eps)) / mu0, du0 + eps};
            double[][] ybPrime = rk4Method(
                    ode,
                    y0Prime,
                    a,
                    b,
                    100
            );

            double FPrime = ybPrime[ybPrime.length - 1][0] - B;
            double dFdu0 = (FPrime - F) / eps; // Численное вычисление производной F по du0

            du0 -= F / dFdu0; // Корректировка начальной производной методом Ньютона
        }

        // Метод Ньютона не сошёлся / недостаточно быстро сходится
        return du0;
    }

    public static double[][] apply(
        double mu0,
        double nu0,
        double a,
        double b,
        int n,
        double A,
        double B,
        BiFunction<Double, double[], double[]> ode,
        Function<Double, Double> analyticalFunction,
        double eps,
        int maxIter

    ) {
        double du0 = newton(
                mu0,
                nu0,
                a,
                b,
                A,
                B,
                ode,
                eps,
                maxIter
        );
        double[] y0 = {A / mu0, du0};

        // Решение задачи Коши с найденным значением U'(a)
        double[][] result = rk4Method(
                ode,
                y0,
                a,
                b,
                RK_SPLIT
        );

        // Выводим решения на всем отрезке
        for (int i = 0; i < result.length; i++) {
            double t = a + i * (b - a) / n;
            System.out.println("t = " + t + ", U(t) = " + result[i][0] + ", U'(t) = " + result[i][1]);
        }

        double maxDiff = 0.0;
        for (int i = 0; i < result.length; i++) {
            double t = a + i * (b - a) / 100;
            double diff = Math.abs(result[i][0] - analyticalFunction.apply(t));
            if (diff > maxDiff) {
                maxDiff = diff;
            }
        }

        System.out.println("\nПогрешность метода составляет " + maxDiff);

        return result;
    }
}
