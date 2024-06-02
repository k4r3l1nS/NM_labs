package lab.numeric.methods.core.models.methods.impl;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.core.models.methods.DESolvingMethod;

public class RungeKuttMethod extends DESolvingMethod {

    @Override
    protected void calculateNextY(int i) {

        var k1 = h * f.calculateExpression(
                x[i],
                y[i]
        );
        var k2 = h * f.calculateExpression(
                x[i] + 0.25 * h,
                y[i] + 0.25 * k1
        );
        var k3 = h * f.calculateExpression(
                x[i] + 0.375 * h,
                y[i] + 3.0 / 32 * k1 + 9.0 / 32 * k2
        );
        var k4 = h * f.calculateExpression(
                x[i] + 12.0 / 13 * h,
                y[i] + 1932.0 / 2197 * k1 - 7200.0 / 2197 * k2 + 7296.0 / 2197 * k3
        );
        var k5 = h * f.calculateExpression(
                x[i] + h,
                y[i] + 439.0 / 216 * k1 - 8 * k2 + 3680.0 / 513 * k3
                        - 815.0 / 4104 * k4
        );
        var k6 = h * f.calculateExpression(
                x[i] + 0.5 * h,
                y[i] - 8.0 / 27 * k1 + 2 * k2 - 3544.0 / 2565 * k3
                        + 1859.0 / 4104 * k4 - 0.275 * k5
        );

        var deltaYn = 16.0 / 135 * k1 + 6656.0 / 12825 * k3 + 28561.0 / 56430 * k4
                - 0.18 * k5 + 2.0 / 55 * k6;

        y[i + 1] = y[i] + deltaYn;
    }

    public RungeKuttMethod(Function f, double[] args, double y0, double eps) {
        this.eps = eps;
        double previousY = Double.MAX_VALUE;
        double previousDiff;
        double diff;
        do {
            previousDiff = y == null ? eps : Math.abs(y[y.length - 1] - previousY);
            previousY = y == null ? Double.MAX_VALUE : y[y.length - 1];

            this.f = f;
            x = x == null ? args : new Section(
                    x[0],
                    x[x.length - 1],
                    (x.length - 1) * 2,
                    SeparationType.UNIFORM
            ).getSeparation();
            h = x[1] - x[0];

            y = new double[x.length];

            y[0] = y0;
            for (int i = 0; i < x.length - 1; ++i) {
                calculateNextY(i);
            }
            diff = Math.abs(y[y.length - 1] - previousY) / 31;
        } while (diff > eps && x.length < SPLIT_RESTRICTION && diff != previousDiff);

        if (diff <= eps) {
            errorIndicator = ErrorIndicator.ENDED_SUCCESSFULLY;
        } else if (x.length >= SPLIT_RESTRICTION) {
            errorIndicator = ErrorIndicator.INTERRUPTED_DUE_TO_INVALID_STEP;
        } else if (diff == previousDiff) {
            errorIndicator = ErrorIndicator.INTERRUPTED_DUE_TO_CONSTANT_DIFFERENCE;
        } else {
            errorIndicator = ErrorIndicator.UNKNOWN;
        }
    }

    /**
     * Конструктор для тестировки
     */
    public RungeKuttMethod(Function f, double[] args, double y0, double eps, double yPrecise) {
        this.eps = eps;
        double previousY = Double.MAX_VALUE;
        double previousDiff;
        double diff;
        do {
            previousDiff = y == null ? eps : Math.abs(y[y.length - 1] - previousY);
            previousY = y == null ? Double.MAX_VALUE : y[y.length - 1];

            this.f = f;
            x = x == null ? args : new Section(
                    x[0],
                    x[x.length - 1],
                    (x.length - 1) * 2,
                    SeparationType.UNIFORM
            ).getSeparation();
            h = x[1] - x[0];

            y = new double[x.length];

            y[0] = y0;
            for (int i = 0; i < x.length - 1; ++i) {
                calculateNextY(i);
            }
            diff = Math.abs(y[y.length - 1] - yPrecise) / 31;
        } while (diff > eps && x.length < SPLIT_RESTRICTION && diff != previousDiff);

        if (Math.abs(y[y.length - 1] - previousY) <= eps) {
            errorIndicator = ErrorIndicator.ENDED_SUCCESSFULLY;
        } else if (x.length >= SPLIT_RESTRICTION) {
            errorIndicator = ErrorIndicator.INTERRUPTED_DUE_TO_INVALID_STEP;
        } else if (diff == previousDiff) {
            errorIndicator = ErrorIndicator.INTERRUPTED_DUE_TO_CONSTANT_DIFFERENCE;
        } else {
            errorIndicator = ErrorIndicator.UNKNOWN;
        }
    }
}
