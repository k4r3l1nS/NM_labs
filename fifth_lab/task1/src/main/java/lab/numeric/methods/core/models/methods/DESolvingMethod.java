package lab.numeric.methods.core.models.methods;

import lab.numeric.methods.core.models.Function;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public abstract class DESolvingMethod {

    protected static final int SPLIT_RESTRICTION = 67108864;

    protected Function f;
    protected double[] y;
    protected double[] x;
    protected double h;
    protected double eps;
    protected ErrorIndicator errorIndicator;

    protected abstract void calculateNextY(int i);

    protected enum ErrorIndicator {
        ENDED_SUCCESSFULLY,
        INTERRUPTED_DUE_TO_CONSTANT_DIFFERENCE,
        INTERRUPTED_DUE_TO_INVALID_STEP,
        UNKNOWN
    }
}
