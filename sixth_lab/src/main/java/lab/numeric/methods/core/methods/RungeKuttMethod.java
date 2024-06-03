package lab.numeric.methods.core.methods;

import java.util.function.BiFunction;

import static lab.numeric.methods.core.util.VectorUtil.scaleVector;
import static lab.numeric.methods.core.util.VectorUtil.sumVectors;

public class RungeKuttMethod {

    // Метод Рунге-Кутта 4-го порядка для решения задачи Коши
    public static double[][] rk4Method(BiFunction<Double, double[], double[]> ode, double[] y0, double a, double b, int n) {
        double h = (b - a) / n;  // Шаг интегрирования
        double[][] results = new double[n + 1][2]; // Массив для хранения результатов
        double[] y = y0.clone(); // Начальные условия
        double t = a;            // Начальная точка

        // Записываем начальные условия
        results[0][0] = y[0];
        results[0][1] = y[1];

        // Цикл по шагам интегрирования
        for (int i = 1; i <= n; i++) {
            double[] k1 = scaleVector(ode.apply(t, y), h);
            double[] k2 = scaleVector(ode.apply(t + h / 2.0, sumVectors(y, scaleVector(k1, 0.5))), h);
            double[] k3 = scaleVector(ode.apply(t + h / 2.0, sumVectors(y, scaleVector(k2, 0.5))), h);
            double[] k4 = scaleVector(ode.apply(t + h, sumVectors(y, k3)), h);

            // y = y + 1/6 (k1 + 2 * k2 + 2 * k3 + k4)
            y = sumVectors(
                    y,
                    scaleVector(
                            sumVectors(
                                    sumVectors(
                                            k1,
                                            scaleVector(k2, 2.0)
                                    ),
                                    sumVectors(
                                            scaleVector(k3, 2.0),
                                            k4
                                    )
                            ),
                            1.0 / 6.0
                    )
            );
            t += h;

            // Записываем текущее состояние
            results[i][0] = y[0];
            results[i][1] = y[1];
        }

        return results; // Возвращает массив с результатами на всем интервале [a, b]
    }

//    public static double[] rk4Method(
//            BiFunction<Double, double[], double[]> ode,
//            double[] y0,
//            double a,
//            double b,
//            int n
//    ) {
//
//        double h = (b - a) / n;
//        double[] y = y0.clone();
//        double t = a;
//
//        for (int i = 0; i < n; ++i) {
//            double[] k1 = scaleVector(ode.apply(t, y), h);
//            double[] k2 = scaleVector(ode.apply(t + h / 2.0, sumVectors(y, scaleVector(k1, 0.5))), h);
//            double[] k3 = scaleVector(ode.apply(t + h / 2.0, sumVectors(y, scaleVector(k2, 0.5))), h);
//            double[] k4 = scaleVector(ode.apply(t + h, sumVectors(y, k3)), h);
//
//            // y = y + 1/6 (k1 + 2 * k2 + 2 * k3 + k4)
//            y = sumVectors(
//                    y,
//                    scaleVector(
//                            sumVectors(
//                                    sumVectors(
//                                            k1,
//                                            scaleVector(k2, 2.0)
//                                    ),
//                                    sumVectors(
//                                            scaleVector(k3, 2.0),
//                                            k4
//                                    )
//                            ),
//                            1.0 / 6.0
//                    )
//            );
//            t += h;
//        }
//
//        return y;
//    }
}
