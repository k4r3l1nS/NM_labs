package com.k4r3l1ns.models;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.Scanner;

@Getter
@Setter
public class CrackedMatrix {

    private int size;

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
     * Верхняя "испорченная" строка
     */
    private Vector uCrackedLine;

    /**
     * Нижняя "испорченная" строка
     */
    private Vector dCrackedLine;

    public CrackedMatrix() {
        size = 0;
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

        if (crackedMatrix.size != size) {
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
        result.size = -1;

        while (
                scanner.hasNextLine() &&
                        (result.size == -1 || lineIndex < result.size)
        ) {
            String line = scanner.nextLine();

            Vector row =
                    Vector.read(
                            new ByteArrayInputStream(
                                    line.getBytes()
                            )
                    );

            if (result.size == -1) {
                result = new CrackedMatrix(row.getSize());
            } else if (result.size != row.getSize()) {
                throw new RuntimeException("Входные данные не являются матрицей");
            }

            if (lineIndex == 0) {
                result.uCrackedLine = row;
            } else if (lineIndex == 1) {
                result.dCrackedLine = row;
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
        if (pos == 0) {
            row = uCrackedLine;
        } else if (pos == 1) {
            row = dCrackedLine;
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
}
