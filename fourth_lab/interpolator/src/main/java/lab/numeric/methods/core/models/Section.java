package lab.numeric.methods.core.models;

import lab.numeric.methods.core.enums.SeparationType;
import lombok.Getter;

@Getter
public class Section {

    private final double a;
    private final double b;
    private final int n;
    private final double[] separation;
    private final SeparationType separationType;

    public Section(double a, double b, int n, SeparationType separationType) {
        this.a = a;
        this.b = b;
        this.n = n;
        this.separationType = separationType;
        separation = new double[n + 1];
        if (separationType.equals(SeparationType.UNIFORM)) {
            uniformSeparation();
        } else {
            chebyshevSeparation();
        }
    }

    private void uniformSeparation() {
        var h = (b - a) / n;
        for (int i = 0; i <= n; i++) {
            separation[i] = a + i * h;
        }
    }

    private void chebyshevSeparation() {
        for (int i = 0; i <= n; i++) {
            separation[i] = (a + b) + (b - a) * 0.5 * Math.cos(Math.PI * (n - i) / n);
        }
    }
}
