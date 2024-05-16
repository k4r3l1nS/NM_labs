package lab.numeric.methods.gui;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.core.models.methods.DESolvingMethod;
import lab.numeric.methods.core.models.methods.impl.EulerMethod;
import lab.numeric.methods.core.models.methods.impl.RungeKuttMethod;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class SimpleGUI extends JFrame {

    private static final JButton button = new JButton("Calculate");
    private static final JTextField inputA = new JTextField("1", 10);
    private static final JTextField inputB = new JTextField("3", 10);
    private static final JTextField inputN0 = new JTextField("128", 10);
    private static final JTextField inputU10 = new JTextField("1", 15);
    private static final JTextField inputU20 = new JTextField("1.71828182845904523536", 15);
    private static final JTextField inputEps = new JTextField("1e-6", 10);

    private static final JLabel labelA = new JLabel("Section start (a): ");
    private static final JLabel labelB = new JLabel("Section end (b): ");
    private static final JLabel labelN0 = new JLabel("Start split number (n0): ");
    private static final JLabel labelU10 = new JLabel("Initial condition (u10): ");
    private static final JLabel labelU20 = new JLabel("Initial condition (u20): ");
    private static final JLabel labelEps = new JLabel("Accuracy (eps): ");

    public SimpleGUI(
            Function rhFunction1, Function rhFunction2,
            Function analyticalFunction1, Function analyticalFunction2
    ) {

        super("Simple Calculator");
        this.setBounds(100, 100, 400, 250);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(7, 1, 2, 2));
        container.add(labelA);
        container.add(inputA);
        container.add(labelB);
        container.add(inputB);
        container.add(labelN0);
        container.add(inputN0);
        container.add(labelEps);
        container.add(inputEps);
        container.add(labelU10);
        container.add(inputU10);
        container.add(labelU20);
        container.add(inputU20);

        button.addActionListener(e -> {
                    Section section = new Section(
                            Double.parseDouble(inputA.getText()),
                            Double.parseDouble(inputB.getText()),
                            Integer.parseInt(inputN0.getText()),
                            SeparationType.UNIFORM
                    );
                    JFreeChart[] graphs = createGraphs(
                            rhFunction1,
                            rhFunction2,
                            analyticalFunction1,
                            analyticalFunction2,
                            section,
                            Double.parseDouble(inputU10.getText()),
                            Double.parseDouble(inputU20.getText()),
                            Double.parseDouble(inputEps.getText())
                    );
                    displayGraphs(graphs);
                }
        );
        container.add(button);
    }

    private void displayGraphs(JFreeChart[] graphs) {
        JFrame graphFrame = new JFrame("Graphs");
        graphFrame.setSize(1600, 1200);
        graphFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane();
        graphFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel graphPanel = new JPanel(new GridLayout(1, 3));
        scrollPane.setViewportView(graphPanel);

        for (JFreeChart graph : graphs) {
            ChartPanel chartPanel = new ChartPanel(graph, true);
            graphPanel.add(chartPanel);
        }

        graphFrame.setVisible(true);
    }

    private JFreeChart[] createGraphs(
            Function rhFunction1,
            Function rhFunction2,
            Function analyticalFunction1,
            Function analyticalFunction2,
            Section section,
            double u10,
            double u20,
            double eps
    ) {
        var args = section.getSeparation();

        DESolvingMethod eulerMethod = new EulerMethod(rhFunction1, rhFunction2, args, u10, u20, eps);
        DESolvingMethod rungeKuttMethod = new RungeKuttMethod(rhFunction1, rhFunction2, args, u10, u20, eps);

        XYSeries analyticalFunctionSeries1 = new XYSeries("Analytical function (u1)");
        XYSeries eulerMethodFunctionSeries1 = new XYSeries("Euler method (u1)");
        XYSeries rungeKuttMethodFunctionSeries1 = new XYSeries("Runge-Kutt method (u1)");

        XYSeries analyticalFunctionSeries2 = new XYSeries("Analytical function (u2)");
        XYSeries eulerMethodFunctionSeries2 = new XYSeries("Euler method (u2)");
        XYSeries rungeKuttMethodFunctionSeries2 = new XYSeries("Runge-Kutt method (u2)");

        int n = section.getN() + 1;
        for (int i = 0; i < n; ++i) {
            var analyticalYValue1 = i == 0 ?
                    u10 : analyticalFunction1.calculateExpression(args[i], Double.NaN, Double.NaN);
            var analyticalYValue2 = i == 0 ?
                    u20 : analyticalFunction2.calculateExpression(args[i], Double.NaN, Double.NaN);

            analyticalFunctionSeries1.add(args[i], analyticalYValue1);
            eulerMethodFunctionSeries1.add(args[i], eulerMethod.getU1()[(int) ((double) i / n * eulerMethod.getU1().length)]);
            rungeKuttMethodFunctionSeries1.add(args[i], rungeKuttMethod.getU1()[(int) ((double) i / n * rungeKuttMethod.getU1().length)]);

            analyticalFunctionSeries2.add(args[i], analyticalYValue2);
            eulerMethodFunctionSeries2.add(args[i], eulerMethod.getU2()[(int) ((double) i / n * eulerMethod.getU2().length)]);
            rungeKuttMethodFunctionSeries2.add(args[i], rungeKuttMethod.getU2()[(int) ((double) i / n * rungeKuttMethod.getU2().length)]);
        }

        NumberAxis xAxis = new NumberAxis("X");
        NumberAxis yAxis1 = new NumberAxis("Y");
        NumberAxis yAxis2 = new NumberAxis("Y");

        xAxis.setAutoRange(false);
        yAxis1.setAutoRange(false);
        yAxis2.setAutoRange(false);

        xAxis.setRange(
                section.getA() - (section.getB() - section.getA()) / 7,
                section.getB() + (section.getB() - section.getA()) / 7
        );
        yAxis1.setRange(
                analyticalFunctionSeries1.getMinY() -
                        (analyticalFunctionSeries1.getMaxY() - analyticalFunctionSeries1.getMinY()) / 7,
                analyticalFunctionSeries1.getMaxY() +
                        (analyticalFunctionSeries1.getMaxY() - analyticalFunctionSeries1.getMinY()) / 7
        );
        yAxis2.setRange(
                analyticalFunctionSeries2.getMinY() -
                        (analyticalFunctionSeries2.getMaxY() - analyticalFunctionSeries2.getMinY()) / 7,
                analyticalFunctionSeries2.getMaxY() +
                        (analyticalFunctionSeries2.getMaxY() - analyticalFunctionSeries2.getMinY()) / 7
        );

        // Создаем графики
        JFreeChart chartAnalytical1 = ChartFactory.createXYLineChart("Analytical function (u1)", "X", "Y",
                new XYSeriesCollection(analyticalFunctionSeries1), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartEuler1 = ChartFactory.createXYLineChart("Euler method (u1)", "X", "Y",
                new XYSeriesCollection(eulerMethodFunctionSeries1), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartRungeKutt1 = ChartFactory.createXYLineChart("Runge-Kutt method (u1)", "X", "Y",
                new XYSeriesCollection(rungeKuttMethodFunctionSeries1), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartAnalytical2 = ChartFactory.createXYLineChart("Analytical function (u2)", "X", "Y",
                new XYSeriesCollection(analyticalFunctionSeries2), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartEuler2 = ChartFactory.createXYLineChart("Euler method (u2)", "X", "Y",
                new XYSeriesCollection(eulerMethodFunctionSeries2), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartRungeKutt2 = ChartFactory.createXYLineChart("Runge-Kutt method (u2)", "X", "Y",
                new XYSeriesCollection(rungeKuttMethodFunctionSeries2), PlotOrientation.VERTICAL, true, true, false);

        XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
        renderer1.setSeriesLinesVisible(0, true);
        renderer1.setSeriesShapesVisible(0, true);
        renderer1.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        renderer1.setSeriesPaint(0, Color.RED);

        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        renderer2.setSeriesLinesVisible(0, true);
        renderer2.setSeriesShapesVisible(0, true);
        renderer2.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        renderer2.setSeriesPaint(0, Color.GREEN);

        XYLineAndShapeRenderer renderer3 = new XYLineAndShapeRenderer();
        renderer3.setSeriesLinesVisible(0, true);
        renderer3.setSeriesShapesVisible(0, true);
        renderer3.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        renderer3.setSeriesPaint(0, Color.BLUE);

        chartAnalytical1.getXYPlot().setDomainAxis(xAxis);
        chartAnalytical1.getXYPlot().setRangeAxis(yAxis1);
        chartAnalytical1.getXYPlot().setRenderer(renderer1);

        chartEuler1.getXYPlot().setDomainAxis(xAxis);
        chartEuler1.getXYPlot().setRangeAxis(yAxis1);
        chartEuler1.getXYPlot().setRenderer(renderer2);

        chartRungeKutt1.getXYPlot().setDomainAxis(xAxis);
        chartRungeKutt1.getXYPlot().setRangeAxis(yAxis1);
        chartRungeKutt1.getXYPlot().setRenderer(renderer3);

        chartAnalytical2.getXYPlot().setDomainAxis(xAxis);
        chartAnalytical2.getXYPlot().setRangeAxis(yAxis2);
        chartAnalytical2.getXYPlot().setRenderer(renderer1);

        chartEuler2.getXYPlot().setDomainAxis(xAxis);
        chartEuler2.getXYPlot().setRangeAxis(yAxis2);
        chartEuler2.getXYPlot().setRenderer(renderer2);

        chartRungeKutt2.getXYPlot().setDomainAxis(xAxis);
        chartRungeKutt2.getXYPlot().setRangeAxis(yAxis2);
        chartRungeKutt2.getXYPlot().setRenderer(renderer3);

        return new JFreeChart[] {
                chartAnalytical1,
                chartEuler1,
                chartRungeKutt1,
                chartAnalytical2,
                chartEuler2,
                chartRungeKutt2
        };
    }
}