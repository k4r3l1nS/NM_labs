package lab.numeric.methods.gui;

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
import java.util.function.Function;

public class SimpleGUI extends JFrame {

    public SimpleGUI(
            double[] args,
            double[][] result,
            Function<Double, Double> analyticalFunction,
            Function<Double, Double> analyticalDerivative
    ) {
        JFreeChart[] graphs = createGraphs(
                args,
                result,
                analyticalFunction,
                analyticalDerivative
        );
        displayGraphs(graphs);
    }

    private void displayGraphs(JFreeChart[] graphs) {
        JFrame graphFrame = new JFrame("Graphs");
        graphFrame.setSize(1600, 1200);
        graphFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane();
        graphFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel graphPanel = new JPanel(new GridLayout(2, 2));
        scrollPane.setViewportView(graphPanel);

        for (JFreeChart graph : graphs) {
            ChartPanel chartPanel = new ChartPanel(graph, true);
            graphPanel.add(chartPanel);
        }

        graphFrame.setVisible(true);
    }

    private JFreeChart[] createGraphs(
            double[] args,
            double[][] result,
            Function<Double, Double> analyticalFunction,
            Function<Double, Double> analyticalDerivative
    ) {
        XYSeries analyticalFunctionSeries = new XYSeries("Analytical function");
        XYSeries analyticalDerivativeSeries = new XYSeries("Analytical derivative");
        XYSeries shootingMethodSeries = new XYSeries("Shooting method");
        XYSeries shootingMethodDerivativeSeries = new XYSeries("Derivative by shooting method");

        int n = args.length;
        for (int i = 0; i < n; ++i) {
            analyticalFunctionSeries.add(args[i], analyticalFunction.apply(args[i]));
            analyticalDerivativeSeries.add(args[i], analyticalDerivative.apply(args[i]));
            shootingMethodSeries.add(args[i], result[i][0]);
            shootingMethodDerivativeSeries.add(args[i], result[i][1]);
        }

        NumberAxis xAxis = new NumberAxis("X");
        NumberAxis yAxis = new NumberAxis("Y");

        xAxis.setAutoRange(false);
        yAxis.setAutoRange(false);

        xAxis.setRange(
                args[0] - (args[args.length - 1] - args[0]) / 7,
                args[args.length - 1] + (args[args.length - 1] - args[0]) / 7
        );
        yAxis.setRange(
                analyticalFunctionSeries.getMinY() -
                        (analyticalFunctionSeries.getMaxY() - analyticalFunctionSeries.getMinY()) / 7,
                analyticalFunctionSeries.getMaxY() +
                        (analyticalFunctionSeries.getMaxY() - analyticalFunctionSeries.getMinY()) / 7
        );

        NumberAxis yAxisDerivative = new NumberAxis("Y");

        yAxisDerivative.setAutoRange(false);

        yAxisDerivative.setRange(
                analyticalDerivativeSeries.getMinY() -
                        (analyticalDerivativeSeries.getMaxY() - analyticalDerivativeSeries.getMinY()) / 7,
                analyticalDerivativeSeries.getMaxY() +
                        (analyticalDerivativeSeries.getMaxY() - analyticalDerivativeSeries.getMinY()) / 7
        );

        // Создаем графики
        JFreeChart chartAnalytical = ChartFactory.createXYLineChart("Analytical function", "X", "Y",
                new XYSeriesCollection(analyticalFunctionSeries), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartAnalyticalDerivative = ChartFactory.createXYLineChart("Analytical derivative", "X", "Y",
                new XYSeriesCollection(analyticalDerivativeSeries), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartShooting = ChartFactory.createXYLineChart("Shooting method", "X", "Y",
                new XYSeriesCollection(shootingMethodSeries), PlotOrientation.VERTICAL, true, true, false);

        JFreeChart chartShootingDerivative = ChartFactory.createXYLineChart("Derivative by shooting method", "X", "Y",
                new XYSeriesCollection(shootingMethodDerivativeSeries), PlotOrientation.VERTICAL, true, true, false);

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


        XYLineAndShapeRenderer renderer4 = new XYLineAndShapeRenderer();
        renderer3.setSeriesLinesVisible(0, true);
        renderer3.setSeriesShapesVisible(0, true);
        renderer3.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        renderer3.setSeriesPaint(0, Color.BLACK);

        chartAnalytical.getXYPlot().setDomainAxis(xAxis);
        chartAnalytical.getXYPlot().setRangeAxis(yAxis);
        chartAnalytical.getXYPlot().setRenderer(renderer1);

        chartShooting.getXYPlot().setDomainAxis(xAxis);
        chartShooting.getXYPlot().setRangeAxis(yAxis);
        chartShooting.getXYPlot().setRenderer(renderer2);

        chartAnalyticalDerivative.getXYPlot().setDomainAxis(xAxis);
        chartAnalyticalDerivative.getXYPlot().setRangeAxis(yAxisDerivative);
        chartAnalyticalDerivative.getXYPlot().setRenderer(renderer3);

        chartShootingDerivative.getXYPlot().setDomainAxis(xAxis);
        chartShootingDerivative.getXYPlot().setRangeAxis(yAxisDerivative);
        chartShootingDerivative.getXYPlot().setRenderer(renderer4);

        return new JFreeChart[] {
                chartAnalytical,
                chartAnalyticalDerivative,
                chartShooting,
                chartShootingDerivative
        };
    }
}