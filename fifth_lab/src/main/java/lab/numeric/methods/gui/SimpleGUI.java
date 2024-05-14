package lab.numeric.methods.gui;

import lab.numeric.methods.core.models.Function;
import lab.numeric.methods.core.models.Section;
import lab.numeric.methods.core.models.enums.SeparationType;
import lab.numeric.methods.core.models.methods.DESolvingMethod;
import lab.numeric.methods.core.models.methods.EulerMethod;
import lab.numeric.methods.core.models.methods.RungeKuttMethod;
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
    private static final JTextField inputA = new JTextField("1.4", 10);
    private static final JTextField inputB = new JTextField("5", 10);
    private static final JTextField inputN0 = new JTextField("64", 10);
    private static final JTextField inputY0 = new JTextField("2.5", 10);
    private static final JTextField inputEps = new JTextField("1e-6", 10);

    private static final JLabel labelA = new JLabel("Section start (a): ");
    private static final JLabel labelB = new JLabel("Section end (b): ");
    private static final JLabel labelN0 = new JLabel("Start split number (n0): ");
    private static final JLabel labelY0 = new JLabel("Initial condition (y0): ");
    private static final JLabel labelEps = new JLabel("Accuracy (eps): ");

    public SimpleGUI(Function rhFunction, Function analyticalFunction) {

        super("Simple Calculator");
        this.setBounds(100, 100, 400, 250);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(6, 1, 2, 2));
        container.add(labelA);
        container.add(inputA);
        container.add(labelB);
        container.add(inputB);
        container.add(labelN0);
        container.add(inputN0);
        container.add(labelEps);
        container.add(inputEps);
        container.add(labelY0);
        container.add(inputY0);

        button.addActionListener(e -> {
                    Section section = new Section(
                            Double.parseDouble(inputA.getText()),
                            Double.parseDouble(inputB.getText()),
                            Integer.parseInt(inputN0.getText()),
                            SeparationType.UNIFORM
                    );
                    JFreeChart[] graphs = createGraphs(
                            rhFunction,
                            analyticalFunction,
                            section,
                            Double.parseDouble(inputY0.getText()),
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
            Function rhFunction,
            Function analyticalFunction,
            Section section,
            double y0,
            double eps
    ) {
        var args = section.getSeparation();

        DESolvingMethod eulerMethod = new EulerMethod(rhFunction, args, y0, eps);
        DESolvingMethod rungeKuttMethod = new RungeKuttMethod(rhFunction, args, y0, eps);

        XYSeries analyticalFunctionSeries = new XYSeries("Analytical function");
        XYSeries eulerMethodFunctionSeries = new XYSeries("Euler method");
        XYSeries rungeKuttMethodFunctionSeries = new XYSeries("Runge-Kutt method");


        int n = section.getN() + 1;
        for (int i = 0; i < n; ++i) {
            var analyticalYValue = i == 0 ?
                    y0 : analyticalFunction.calculateExpression(args[i], Double.NaN);
            analyticalFunctionSeries.add(args[i], analyticalYValue);
            eulerMethodFunctionSeries.add(args[i], eulerMethod.getY()[i * eulerMethod.getY().length / n]);
            rungeKuttMethodFunctionSeries.add(args[i], rungeKuttMethod.getY()[i * rungeKuttMethod.getY().length / n]);
        }

        NumberAxis xAxis = new NumberAxis("X");
        NumberAxis yAxis = new NumberAxis("Y");

        xAxis.setAutoRange(false);
        yAxis.setAutoRange(false);

        xAxis.setRange(
                section.getA() - (section.getB() - section.getA()) / 7,
                section.getB() + (section.getB() - section.getA()) / 7
        );
        yAxis.setRange(
                analyticalFunctionSeries.getMinY() -
                        (analyticalFunctionSeries.getMaxY() - analyticalFunctionSeries.getMinY()) / 7,
                analyticalFunctionSeries.getMaxY() +
                        (analyticalFunctionSeries.getMaxY() - analyticalFunctionSeries.getMinY()) / 7
        );

        // Создаем графики
        JFreeChart chartAnalytical = ChartFactory.createXYLineChart("Analytical function", "X", "Y",
                new XYSeriesCollection(analyticalFunctionSeries), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartEuler = ChartFactory.createXYLineChart("Euler method", "X", "Y",
                new XYSeriesCollection(eulerMethodFunctionSeries), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartRungeKutt = ChartFactory.createXYLineChart("Runge-Kutt method", "X", "Y",
                new XYSeriesCollection(rungeKuttMethodFunctionSeries), PlotOrientation.VERTICAL, true, true, false);

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

        chartAnalytical.getXYPlot().setDomainAxis(xAxis);
        chartAnalytical.getXYPlot().setRangeAxis(yAxis);
        chartAnalytical.getXYPlot().setRenderer(renderer1);

        chartEuler.getXYPlot().setDomainAxis(xAxis);
        chartEuler.getXYPlot().setRangeAxis(yAxis);
        chartEuler.getXYPlot().setRenderer(renderer2);

        chartRungeKutt.getXYPlot().setDomainAxis(xAxis);
        chartRungeKutt.getXYPlot().setRangeAxis(yAxis);
        chartRungeKutt.getXYPlot().setRenderer(renderer3);

        return new JFreeChart[] {
                chartAnalytical,
                chartEuler,
                chartRungeKutt
        };
    }
}