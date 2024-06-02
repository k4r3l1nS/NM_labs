package lab.numeric.methods;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.gui.SimpleGUI;

public class FirstTaskApplication {

    // y' = ...
    private final static Function RIGHT_HAND_FUNCTION = (x, y) -> x / 3 - y;

    // Result of pure calculations
    private final static Function ANALYTICAL_FUNCTION = (x, y) -> 71 * Math.exp(1.4 - x) / 30 + x / 3 - 1.0 / 3;

    public static void main(String[] args) {
        SimpleGUI app = new SimpleGUI(
                RIGHT_HAND_FUNCTION,
                ANALYTICAL_FUNCTION
        );
        app.setVisible(true);
    }
}