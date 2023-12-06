package com.k4r3l1ns.models;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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

        vector = copyOf(vector).negate();
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
        return Arrays.stream(values).map(Math::abs).max().orElseThrow();
    }

    public Vector fillWithRandomValues(double min, double max) {

        for (int i = 0; i < size; ++i) {
            values[i] = ThreadLocalRandom.current().nextDouble(min, max);
        }
        return this;
    }

    public Vector fillWithDependentValues(double min, double max) {

        double coefficient1 = ThreadLocalRandom.current().nextDouble(min, max);
        double coefficient2 = ThreadLocalRandom.current().nextDouble(min, max);

        for (int i = 0; i < size; ++i) {
            values[i] = i * coefficient1 + coefficient2;
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
                writer.write(String.format("%.2f\t", value));
            }
            writer.write("\n");
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void print() {
        for (var value : values) {
            if (value != 0.0) {
                System.out.printf("%.2f\t", value);
            } else {
                System.out.print("\t\t");
            }
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

    public void setValues(double[] values) {
        this.values = values;
        this.size = values.length;
    }

    public static Vector copyOf(Vector vector) {
        Vector result = new Vector(vector.size);
        System.arraycopy(vector.getValues(), 0, result.getValues(), 0, vector.size);
        return result;
    }
}
