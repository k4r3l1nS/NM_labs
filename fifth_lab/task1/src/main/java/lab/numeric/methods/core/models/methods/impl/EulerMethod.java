package lab.numeric.methods.core.models.methods.impl;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.core.models.methods.DESolvingMethod;

public class EulerMethod extends DESolvingMethod {

    @Override
    protected void calculateNextY(int i) {
        y[i + 1] = y[i] + h * f.calculateExpression(x[i], y[i]);
    }

    public EulerMethod(Function f, double[] args, double y0, double eps) {
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
            diff = Math.abs(y[y.length - 1] - previousY);
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

    /**
     * Конструктор для тестировки
     */
    public EulerMethod(Function f, double[] args, double y0, double eps, double yPrecise) {
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
            diff = Math.abs(y[y.length - 1] - yPrecise);
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
