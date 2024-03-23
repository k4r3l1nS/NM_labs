package com.k4r3l1ns.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TapeMatrix {

    int size;
    int tapeWidth;

    Vector values;

    public TapeMatrix(int size, int tapeWidth) {

        if (size <= 0 || tapeWidth <= 0) {
            throw new RuntimeException("Размерность матрицы не может быть неположительной");
        }

        this.size = size;
        this.tapeWidth = Math.min(tapeWidth, size);

        values = new Vector(this.size * (2 * this.tapeWidth - 1));
    }

    public static TapeMatrix wellConditionedMatrix(
            int size,
            int tapeWidth,
            double[] range
    ) {
        if (range.length != 2 || range[0] > range[1]) {
            throw new RuntimeException("Некорретный диапазон");
        }

        TapeMatrix wellConditionedMatrix = new TapeMatrix(size, tapeWidth);
        wellConditionedMatrix.values.fillWithRandomValues(range[0], range[1]);

        return wellConditionedMatrix;
    }

    public static TapeMatrix poorConditionedMatrix(
            int size,
            int tapeWidth,
            double[] range,
            double divisor
    ) {
        TapeMatrix poorConditionedMatrix = wellConditionedMatrix(size, tapeWidth, range);

        var values = poorConditionedMatrix.getValues();
        for (int i = 0; i < size; ++i) {
            values.multiplyValueAt(
                    tapeWidth - 1 + i * (2 * tapeWidth - 1),
                    1 / divisor
            );
        }

        return poorConditionedMatrix;
    }

    public static TapeMatrix copyOf(TapeMatrix a) {

        TapeMatrix copy = new TapeMatrix(a.size, a.tapeWidth);
        copy.values = Vector.copyOf(a.values);

        return copy;
    }

    public int k0(int index) {
        return index < tapeWidth ? 0 : index - tapeWidth + 1;
    }

    public int kN(int index) {
        return index >= size - tapeWidth ? size - 1 : index + tapeWidth - 1;
    }

    public int hash(int i, int j) {

        if (Math.abs(i - j) >= tapeWidth ||
                i < 0 || i >= size ||
                j < 0 || j >= size
        ) {
            return -1;
        }

        return i * (2 * tapeWidth - 2) + tapeWidth - 1 + j;
    }

    public double valueAt(int i, int j) {

        int index = hash(i, j);
        return index == -1 ? 0.0 : values.getValueAt(index);
    }

    public void setValueAt(int i, int j, double value) {

        int index = hash(i, j);
        if (index != -1) {
            values.setValueAt(index, value);
        }
    }

    public void print() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                int index = hash(i, j);
                if (index == -1) {
                    System.out.print("\t\t");
                } else {
                    System.out.printf("%.2f\t", values.getValueAt(index));
                }
            }
            System.out.println();
        }
    }
}
