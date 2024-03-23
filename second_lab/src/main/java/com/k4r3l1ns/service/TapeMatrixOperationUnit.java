package com.k4r3l1ns.service;

import com.k4r3l1ns.models.TapeMatrix;
import com.k4r3l1ns.models.Vector;

public class TapeMatrixOperationUnit {

    public static Vector solveEquation(TapeMatrix tapeMatrix, Vector vector) {

        var a = TapeMatrix.copyOf(tapeMatrix);
        var f = Vector.copyOf(vector);

        if (a.getSize() != f.getSize()) {
            throw new RuntimeException("Размерности матрицы и вектора несовместимы");
        }

        int size = a.getSize();
        int tapeWidth = a.getTapeWidth();

        TapeMatrix b = new TapeMatrix(size, tapeWidth);
        TapeMatrix c = new TapeMatrix(size, tapeWidth);

        for (int i = 0; i < size; ++i) {
            b.setValueAt(i, i, 1.0);
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {

                double valueToSubtract = 0.0;
                double aValue = a.valueAt(i, j);

                if (i <= j) {
                    for (int k = a.k0(j); k <= i; ++k) {
                        valueToSubtract += b.valueAt(i, k) * c.valueAt(k, j);
                    }
                    c.setValueAt(i, j, aValue - valueToSubtract);
                } else {
                    for (int k = a.k0(i); k <= j; ++k) {
                        valueToSubtract += b.valueAt(i, k) * c.valueAt(k, j);
                    }
                    b.setValueAt(i, j, (aValue - valueToSubtract) / c.valueAt(j, j));
                }
            }
        }

        // Получили уравнение BCx = f. Решаем отн-но Cx
        var y = new Vector(size);
        y.setValueAt(0, f.getValueAt(0));
        for (int i = 1; i < size; ++i) {
            double valueToSubtract = 0.0;
            for (int j = 0; j < i; ++j) {
                valueToSubtract += y.getValueAt(j) * b.valueAt(i, j);
            }
            y.setValueAt(i, f.getValueAt(i) - valueToSubtract);
        }

        // Получили уравнение Cx = y. Решаем отн-но x
        var result = new Vector(size);
        result.setValueAt(
                size - 1,
                y.getValueAt(size - 1) / c.valueAt(size - 1, size - 1)
        );
        for (int i = size - 2; i >= 0; --i) {
            double valueToSubtract = 0.0;
            for (int j = size - 1; j > i; --j) {
                valueToSubtract += result.getValueAt(j) * c.valueAt(i, j);
            }
            result.setValueAt(i, (y.getValueAt(i) - valueToSubtract) / c.valueAt(i, i));
        }

        return result;
    }

    public static Vector multiply(TapeMatrix tapeMatrix, Vector vector) {

        var a = TapeMatrix.copyOf(tapeMatrix);
        var x = Vector.copyOf(vector);

        if (a.getSize() != x.getSize()) {
            throw new RuntimeException("Размерности матрицы и вектора несовместимы");
        }

        int size = a.getSize();
        int tapeWidth = a.getTapeWidth();

        var result = new Vector(size);

        for (int i = 0; i < size; ++i) {
            double rowSum = 0.0;
            for (int j = i - tapeWidth + 1; j < i + tapeWidth; ++j) {
                if (j >= 0 && j < size) {
                    rowSum += a.valueAt(i, j) * x.getValueAt(j);
                }
            }
            result.setValueAt(i, rowSum);
        }

        return result;
    }

    public static TapeMatrix multiply(TapeMatrix matrix1, TapeMatrix matrix2) {

        var b = TapeMatrix.copyOf(matrix1);
        var c = TapeMatrix.copyOf(matrix2);

        if (b.getSize() != c.getSize() || b.getTapeWidth() != c.getTapeWidth()) {
            throw new RuntimeException("Размерности матриц несовместимы");
        }

        int size = b.getSize();
        int tapeWidth = b.getTapeWidth();

        var result = new TapeMatrix(size, tapeWidth);

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                double sum = 0.0;
                for (int k = 0; k < size; ++k) {
                    sum += b.valueAt(i, k) * c.valueAt(k, j);
                }
                result.setValueAt(i, j, sum);
            }
        }

        return result;
    }
}
