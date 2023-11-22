package com.k4r3l1ns.models;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Vector {

    private double[] values;
    private int size;

    public Vector() {
        size = 0;
        values = new double[0];
    }

    public Vector(int size) {

        if (size < 0) {
            throw new RuntimeException("Некорректная размерность");
        }

        this.size = size;
        values = new double[size];
    }

    public void fill(double[] values) {

        if (values.length != size) {
            throw new RuntimeException("Размеры не совпадают");
        }

        System.arraycopy(values, 0, this.values, 0, size);
    }

    public Vector add(Vector vector) {

        if (size != vector.size) {
            throw new RuntimeException("Размеры не совпадают");
        }

        for (int i = 0; i < size; ++i) {
            values[i] += vector.values[i];
        }

        return this;
    }

    public Vector subtract(Vector vector) {

        vector = vector.negate();
        return this.add(vector);
    }

    private Vector negate() {
        return this.multiplyByScalar(-1);
    }

    public Vector multiplyByScalar(double scalar) {

        for (int i = 0; i < size; ++i) {
            values[i] *= scalar;
        }

        return this;
    }

    /**
     * Вычисление нормы как максимального элемента вектора
     *
     * @return Норма
     */
    public double findNorm() {
        return Arrays.stream(values).max().orElseThrow();
    }

    public Vector fillWithRandomValues(double min, double max) {

        for (int i = 0; i < size; ++i) {
            values[i] = ThreadLocalRandom.current().nextDouble(min, max);
        }
        return this;
    }

    public static Vector read(InputStream inputStream) {

        Scanner scanner = new Scanner(inputStream);
        List<Double> values = new ArrayList<>();
        while (scanner.hasNext()) {
            values.add(scanner.nextDouble());
        }

        Vector result = new Vector(values.size());
        result.values = values.stream().mapToDouble(x -> x).toArray();

        return result;
    }

    public void write(Writer writer) {
        try {
            for (var value : values) {
                writer.write(String.format("%.2f\t\t", value));
//                writer.write((int) value + " ");
            }
            writer.write("\n");
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void print() {
        for (var value : values) {
            System.out.printf("%.2f\t\t", value);
//            System.out.print((int) value + " ");
        }
        System.out.println();
    }

    public double getValueAt(int pos) {
        return values[pos];
    }

    public void setValueAt(int pos, double value) {
        values[pos] = value;
    }

    public void multiplyValueAt(int pos, double scalar) {
        values[pos] *= scalar;
    }

    public void addValueAt(int pos, double value) {
        values[pos] += value;
    }

    public void subtractValueAt(int pos, double value) {
        values[pos] -= value;
    }
}
