package lab.numeric.methods.core.models.methods;

import lab.numeric.methods.core.models.Function;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public abstract class DESolvingMethod {

    protected Function f;
    protected double[] y;
    protected double[] x;
    protected double h;
    protected double eps;

    protected abstract void calculateNextY(int i);
}
