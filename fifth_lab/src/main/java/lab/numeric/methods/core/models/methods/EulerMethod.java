package lab.numeric.methods.core.models.methods;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;

public class EulerMethod extends DESolvingMethod{

    @Override
    protected void calculateNextY(int i) {
        y[i + 1] = y[i] + h * f.calculateExpression(x[i], y[i]);
    }

    public EulerMethod(Function f, double[] args, double y0, double eps) {
        this.eps = eps;
        double previousY;
        do {
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
        } while (Math.abs(y[y.length - 1] - previousY) > eps);
    }
}
