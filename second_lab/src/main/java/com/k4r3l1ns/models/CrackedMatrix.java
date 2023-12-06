package com.k4r3l1ns.models;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Scanner;

@Getter
@Setter
public class CrackedMatrix {

    private Integer size;

    /**
     * Побочная диагональ матрицы
     */
    private Vector sideDiagonal;

    /**
     * Диагональ слева от побочной
     */
    private Vector lSideDiagonal;

    /**
     * Диагональ справа от побочной
     */
    private Vector rSideDiagonal;

    /**
     * Номер верхней "испорченной" строки
     */
    private Integer uCrackedLinePos;

    /**
     * Верхняя "испорченная" строка
     */
    private Vector uCrackedLine;

    /**
     * Нижняя "испорченная" строка
     */
    private Vector dCrackedLine;

    public CrackedMatrix() {
        sideDiagonal = new Vector(0);
        lSideDiagonal = new Vector(0);
        rSideDiagonal = new Vector(0);
        uCrackedLine = new Vector(0);
        dCrackedLine = new Vector(0);
    }

    public CrackedMatrix(int size) {

        if (size < 0) {
            throw new RuntimeException("Некорректная размерность");
        }

        this.size = size;
        sideDiagonal = new Vector(size);

        lSideDiagonal = new Vector(size);
        lSideDiagonal.setValueAt(lSideDiagonal.getSize() - 1, 0.0);

        rSideDiagonal = new Vector(size);
        rSideDiagonal.setValueAt(0, 0.0);

        uCrackedLine = new Vector(size);
        dCrackedLine = new Vector(size);
    }

    public CrackedMatrix add(CrackedMatrix crackedMatrix) {

        if (!crackedMatrix.size.equals(size)) {
            throw new RuntimeException("Несовместимые размерности");
        }

        sideDiagonal = sideDiagonal.add(crackedMatrix.sideDiagonal);
        lSideDiagonal = lSideDiagonal.add(crackedMatrix.lSideDiagonal);
        rSideDiagonal = rSideDiagonal.add(crackedMatrix.rSideDiagonal);
        uCrackedLine = uCrackedLine.add(crackedMatrix.uCrackedLine);
        dCrackedLine = dCrackedLine.add(crackedMatrix.dCrackedLine);

        return this;
    }

    public CrackedMatrix subtract(CrackedMatrix crackedMatrix) {

        crackedMatrix = crackedMatrix.negate();
        return this.add(crackedMatrix);
    }

    public CrackedMatrix multiplyByScalar(double scalar) {

        sideDiagonal = sideDiagonal.multiplyByScalar(scalar);
        lSideDiagonal = lSideDiagonal.multiplyByScalar(scalar);
        rSideDiagonal = rSideDiagonal.multiplyByScalar(scalar);
        uCrackedLine = uCrackedLine.multiplyByScalar(scalar);
        dCrackedLine = dCrackedLine.multiplyByScalar(scalar);

        return this;
    }

    public CrackedMatrix negate() {
        return this.multiplyByScalar(-1);
    }

    public static CrackedMatrix read(InputStream inputStream) {

        Scanner scanner = new Scanner(inputStream);

        int lineIndex = 0;
        CrackedMatrix result = new CrackedMatrix();

        while (
                scanner.hasNextLine() &&
                        (result.size == null || lineIndex < result.size)
        ) {
            String line = scanner.nextLine();

            Vector row =
                    Vector.read(
                            new ByteArrayInputStream(
                                    line.getBytes()
                            )
                    );

            if (result.size == null) {
                result = new CrackedMatrix(row.getSize());
            } else if (result.size != row.getSize()) {
                throw new RuntimeException("Входные данные не являются матрицей");
            }

            if (row.getValueAt(0) != 0 && result.uCrackedLinePos == null) {
                result.uCrackedLinePos = lineIndex;
            }

            if (result.uCrackedLinePos != null) {
                if (result.uCrackedLinePos == lineIndex) {
                    result.uCrackedLine = row;
                } else if (result.uCrackedLinePos + 1 == lineIndex) {
                    result.dCrackedLine = row;
                }
            }

            result.sideDiagonal.setValueAt(
                    lineIndex,
                    row.getValueAt(row.getSize() - 1 - lineIndex)
            );

            if (lineIndex > 0) {
                result.rSideDiagonal.setValueAt(
                        lineIndex,
                        row.getValueAt(row.getSize() - lineIndex)
                );
            }

            if (lineIndex < result.size - 1) {
                result.lSideDiagonal.setValueAt(
                        lineIndex,
                        row.getValueAt(row.getSize() - 2 - lineIndex)
                );
            }

            ++lineIndex;
        }

        return result;
    }

    public void write(Writer writer) {

        for (int i = 0; i < size; ++i) {
            rowAt(i).write(writer);
        }

        try {
            writer.flush();
        } catch (IOException ex) {
            System.out.println("Ошибка ввода в файл");
        }
    }

    public void print() {

        for (int i = 0; i < size; ++i) {
            rowAt(i).print();
        }
    }

    public Vector rowAt(int pos) {
        Vector row;
        if (pos == uCrackedLinePos) {
            row = uCrackedLine;
        } else if (pos == uCrackedLinePos + 1) {
            row = dCrackedLine;
        } else if (pos == 0) {
            row = new Vector(size);
            row.setValueAt(size - 1, sideDiagonal.getValueAt(0));
            row.setValueAt(size - 2, lSideDiagonal.getValueAt(0));
        } else if (pos < size - 1) {
            row = new Vector(size);
            row.setValueAt(size - pos - 2, lSideDiagonal.getValueAt(pos));
            row.setValueAt(size - pos - 1, sideDiagonal.getValueAt(pos));
            row.setValueAt(size - pos, rSideDiagonal.getValueAt(pos));
        } else {
            row = new Vector(size);
            row.setValueAt(0, sideDiagonal.getValueAt(size - 1));
            row.setValueAt(1, rSideDiagonal.getValueAt(size - 1));
        }

        return row;
    }

    public static CrackedMatrix copyOf(CrackedMatrix crackedMatrix) {

        CrackedMatrix copy = new CrackedMatrix(crackedMatrix.size);

        copy.size = crackedMatrix.size;
        copy.uCrackedLinePos = crackedMatrix.uCrackedLinePos;
        System.arraycopy(crackedMatrix.rSideDiagonal.getValues(), 0, copy.rSideDiagonal.getValues(), 0, copy.size);
        System.arraycopy(crackedMatrix.lSideDiagonal.getValues(), 0, copy.lSideDiagonal.getValues(), 0, copy.size);
        System.arraycopy(crackedMatrix.sideDiagonal.getValues(), 0, copy.sideDiagonal.getValues(), 0, copy.size);
        System.arraycopy(crackedMatrix.uCrackedLine.getValues(), 0, copy.uCrackedLine.getValues(), 0, copy.size);
        System.arraycopy(crackedMatrix.dCrackedLine.getValues(), 0, copy.dCrackedLine.getValues(), 0, copy.size);

        return copy;
    }

    public static CrackedMatrix poorConditionedMatrix(int size, double min, double max) {

        var poorConditionedMatrix = new CrackedMatrix(size);

        poorConditionedMatrix.getLSideDiagonal().fillWithRandomValues(min, max);
        poorConditionedMatrix.getRSideDiagonal().fillWithRandomValues(min, max);

        poorConditionedMatrix.getSideDiagonal().fillWithDependentValues(min, max);
        poorConditionedMatrix.getUCrackedLine().fillWithDependentValues(min, max);
        poorConditionedMatrix.getDCrackedLine().fillWithDependentValues(min, max);

        return poorConditionedMatrix;
    }

    public static CrackedMatrix wellConditionedMatrix(int size, double min, double max) {

        var wellConditionedMatrix = new CrackedMatrix(size);

        wellConditionedMatrix.getSideDiagonal().fillWithRandomValues(min, max);
        wellConditionedMatrix.getLSideDiagonal().fillWithRandomValues(min, max);
        wellConditionedMatrix.getRSideDiagonal().fillWithRandomValues(min, max);
        wellConditionedMatrix.getUCrackedLine().fillWithRandomValues(min, max);
        wellConditionedMatrix.getDCrackedLine().fillWithRandomValues(min, max);

        return wellConditionedMatrix;
    }
}
