package lab.numeric.methods;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.gui.SimpleGUI;

public class Main {

//    private static final Function FUNCTION = x -> -Math.pow(x, 3) + Math.pow(x, 2) + 9;
    private static final Function FUNCTION = Math::abs;

    public static void main(String[] args) {
        SimpleGUI app = new SimpleGUI(FUNCTION);
        app.setVisible(true);
    }
}
