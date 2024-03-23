package lab.numeric.methods;

import lab.numeric.methods.core.models.impl.CubicSpline;
import lab.numeric.methods.core.models.InterpolatedFunction;
import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.enums.SeparationType;
import lab.numeric.methods.gui.SimpleGUI;
import org.junit.Test;

public class CubicSplineTests {
    private static final Function FUNCTION = x -> {

        var val1 = Math.pow(x, 3) * 5;
        var val2 = Math.pow(x, 2) * 2;
        var val3 = -x * 5;
        var val4 = 3;
        return val1 + val2 + val3 + val4;
    };

    private static final Section SECTION =
            new Section(0, 1.5, 3, SeparationType.CHEBYSHEV);

    private static final double a = 4;
    private static final double b = -1.3;

    @Test
    public void correctnessTest() {

        var args = SECTION.getSeparation();

        System.out.println("f(x0), ..., f(xN):");
        if (SECTION.getSeparationType().equals(SeparationType.UNIFORM)) {
            for (var arg : args) {
                System.out.println(FUNCTION.calculateExpression(arg));
            }
        } else {
            for (int i = args.length - 1; i >= 0; --i) {
                System.out.println(FUNCTION.calculateExpression(args[i]));
            }
        }
        System.out.println();

        CubicSpline cubicSpline = new CubicSpline(FUNCTION, SECTION, a, b);

        System.out.println("s(x0), ..., s(xN):");
        for (var arg : args) {
            System.out.println(cubicSpline.calculateExpression(arg));
        }
        System.out.println();

        System.out.println("a, b = { " + a + ", " + b + " }");
        System.out.println();
        System.out.println("s3(x0), s3(xN):");
        System.out.println(cubicSpline.calculateExpression(args[0], 3));
        System.out.println(cubicSpline.calculateExpression(args[args.length - 1], 1));

        System.out.println();
        System.out.println("Непрерывность при соединении сплайнов:");
        System.out.println(cubicSpline.calculateExpression(0.4999999));
        System.out.println(cubicSpline.calculateExpression(0.5000001));
    }

    @Test
    public void GUITest() {

        InterpolatedFunction cs = new CubicSpline(
                FUNCTION,
                SECTION,
                a,
                b
        );

        SimpleGUI app = new SimpleGUI(cs);
        app.setVisible(true);
    }
}
