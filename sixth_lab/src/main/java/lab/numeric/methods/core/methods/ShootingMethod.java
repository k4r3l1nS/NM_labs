package lab.numeric.methods.core.methods;

import java.util.function.BiFunction;

import static lab.numeric.methods.core.methods.RungeKuttMethod.rk4Method;

public class ShootingMethod {

    public static double newton(
            double mu0,
            double nu0,
            double a,
            double b,
            double A,
            double B,
            BiFunction<Double, double[], double[]> ode,
            double tol,
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
                    100
            ); // Решение задачи Коши методом Рунге-Кутта

            double F = yb[yb.length - 1][0] - B; // Разница между решением и граничным условием в точке b

            if (Math.abs(F) < tol) {
                return du0; // Возвращаем корректное значение U'(a), если достигнута требуемая точность
            }

            double[] y0Prime = {(A - nu0 * (du0 + tol)) / mu0, du0 + tol};
            double[][] ybPrime = rk4Method(
                    ode,
                    y0Prime,
                    a,
                    b,
                    100
            );

            double FPrime = ybPrime[ybPrime.length - 1][0] - B;
            double dFdu0 = (FPrime - F) / tol; // Численное вычисление производной F по du0

            du0 -= F / dFdu0; // Корректировка начальной производной методом Ньютона
        }

        // Метод Ньютона не сошёлся / недостаточно быстро сходится
        return du0;
    }
}
