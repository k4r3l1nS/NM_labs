package lab.numeric.methods.core.models.methods.impl;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.core.models.methods.DESolvingMethod;

public class EulerMethod extends DESolvingMethod {

    @Override
    protected void calculateNextY(int i) {
        u1[i + 1] = u1[i] + h * f1.calculateExpression(x[i], u1[i], u2[i]);
        u2[i + 1] = u2[i] + h * f2.calculateExpression(x[i], u1[i], u2[i]);
    }

    public EulerMethod(Function f1, Function f2, double[] args, double u10, double u20, double eps) {
        this.eps = eps;
        double previousU1 = Double.MAX_VALUE;
        double previousU2 = Double.MAX_VALUE;
        double previousDiff;
        double diff;
        do {
            previousDiff = u1 == null || u2 == null ?
                    eps : Math.max(Math.abs(u1[u1.length - 1] - previousU1), Math.abs(u2[u2.length - 1] - previousU2));
            previousU1 = u1 == null ? Double.MAX_VALUE : u1[u1.length - 1];
            previousU2 = u2 == null ? Double.MAX_VALUE : u2[u2.length - 1];

            this.f1 = f1;
            this.f2 = f2;
            x = x == null ? args : new Section(
                    x[0],
                    x[x.length - 1],
                    (x.length - 1) * 2,
                    SeparationType.UNIFORM
            ).getSeparation();
            h = x[1] - x[0];

            u1 = new double[x.length];
            u2 = new double[x.length];

            u1[0] = u10;
            u2[0] = u20;
            for (int i = 0; i < x.length - 1; ++i) {
                calculateNextY(i);
            }
            diff = Math.max(Math.abs(u1[u1.length - 1] - previousU1), Math.abs(u2[u2.length - 1] - previousU2));
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

//    /**
//     * Конструктор для тестировки
//     */
    public EulerMethod(
            Function f1,
            Function f2,
            double[] args,
            double u10,
            double u20,
            double eps,
            double u1Precise,
            double u2Precise
    ) {
        this.eps = eps;
        double previousDiff;
        double diff;
        do {
            previousDiff = u1 == null || u2 == null ?
                    eps : Math.max(Math.abs(u1[u1.length - 1] - u1Precise), Math.abs(u2[u2.length - 1] - u2Precise));

            this.f1 = f1;
            this.f2 = f2;
            x = x == null ? args : new Section(
                    x[0],
                    x[x.length - 1],
                    (x.length - 1) * 2,
                    SeparationType.UNIFORM
            ).getSeparation();
            h = x[1] - x[0];

            u1 = new double[x.length];
            u2 = new double[x.length];

            u1[0] = u10;
            u2[0] = u20;
            for (int i = 0; i < x.length - 1; ++i) {
                calculateNextY(i);
            }
            diff = Math.max(Math.abs(u1[u1.length - 1] - u1Precise), Math.abs(u2[u2.length - 1] - u2Precise));
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
}
