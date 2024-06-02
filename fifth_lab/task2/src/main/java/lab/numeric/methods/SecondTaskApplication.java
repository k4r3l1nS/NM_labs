package lab.numeric.methods;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.gui.SimpleGUI;

public class SecondTaskApplication {

    // u1' = ...
    private final static Function U1_RIGHT_HAND_FUNCTION = (x, u1, u2) -> -2 * u1 / x;

    // u2' = ...
    private final static Function U2_RIGHT_HAND_FUNCTION = (x, u1, u2) -> u2 + (2 + x) * u1 / x;

    // u1 = 1 / x^2
    private final static Function U1_ANALYTICAL_FUNCTION = (x, u1, u2) -> 1 / (x * x);

    // u2 = e^x - 1 / x^2
    private final static Function U2_ANALYTICAL_FUNCTION = (x, u1, u2) -> Math.exp(x) - 1 / (x * x);


    public static void main(String[] args) {
        SimpleGUI app = new SimpleGUI(
                U1_RIGHT_HAND_FUNCTION, U2_RIGHT_HAND_FUNCTION,
                U1_ANALYTICAL_FUNCTION, U2_ANALYTICAL_FUNCTION
        );
        app.setVisible(true);
    }
}
