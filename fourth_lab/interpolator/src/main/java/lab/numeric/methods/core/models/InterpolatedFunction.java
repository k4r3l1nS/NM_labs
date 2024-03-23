package lab.numeric.methods.core.models;

import lab.numeric.methods.core.enums.InterpolationMethod;
import lombok.Getter;

@Getter
public abstract class InterpolatedFunction implements Function {

    private final Section section;

    private final InterpolationMethod interpolationMethod;

    protected InterpolatedFunction(
            Section section,
            InterpolationMethod interpolationMethod
    ) {
        this.interpolationMethod = interpolationMethod;
        this.section = section;
    }
}
