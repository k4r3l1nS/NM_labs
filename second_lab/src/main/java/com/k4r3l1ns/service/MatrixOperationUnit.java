package com.k4r3l1ns.service;

import com.k4r3l1ns.models.CrackedMatrix;
import com.k4r3l1ns.models.Vector;

public class MatrixOperationUnit {

    public static Vector solveEquation(CrackedMatrix a, Vector f) {

        if (a.getSize() != f.getSize()) {
            throw new RuntimeException("Размерности матрицы и вектора несовместимы");
        }

        a = CrackedMatrix.copyOf(a);
        f = Vector.copyOf(f);

        var size = a.getSize();
        var uCrackedLinePos = a.getUCrackedLinePos();

        // Верхняя кодиагональ
        var lSideDiagonal = a.getLSideDiagonal();

        // Побочная диагональ
        var sideDiagonal = a.getSideDiagonal();

        // Нижняя кодиагональ
        var rSideDiagonal = a.getRSideDiagonal();

        var uCrackedLine = a.getUCrackedLine();
        var dCrackedLine = a.getDCrackedLine();


        // Шаг 1 (прямой ход)
        for (int i = 0; i < uCrackedLinePos; ++i) {

            var r = 1 / sideDiagonal.getValueAt(i);
            sideDiagonal.setValueAt(i, 1.0);
            lSideDiagonal.multiplyValueAt(i, r);
            f.multiplyValueAt(i, r);

            if (i != uCrackedLinePos - 1) {
                r = rSideDiagonal.getValueAt(i + 1);
                rSideDiagonal.setValueAt(i + 1, 0.0);
                sideDiagonal.subtractValueAt(i + 1, r * lSideDiagonal.getValueAt(i));
                f.subtractValueAt(i + 1, r * f.getValueAt(i));
            }

            r = dCrackedLine.getValueAt(size - 1 - i);
            dCrackedLine.setValueAt(size - 1 - i, 0.0);
            dCrackedLine.subtractValueAt(size - 2 - i, r * lSideDiagonal.getValueAt(i));
            f.subtractValueAt(uCrackedLinePos + 1, r * f.getValueAt(i));

            r = uCrackedLine.getValueAt(size - 1 - i);
            uCrackedLine.setValueAt(size - 1 - i, 0.0);
            uCrackedLine.subtractValueAt(size - 2 - i, r * lSideDiagonal.getValueAt(i));
            f.subtractValueAt(uCrackedLinePos, r * f.getValueAt(i));
        }


        // Шаг 2 (Обратный ход)
        for (int i = size - 1; i > uCrackedLinePos + 1; --i) {

            var r = 1 / sideDiagonal.getValueAt(i);
            sideDiagonal.setValueAt(i, 1.0);
            rSideDiagonal.multiplyValueAt(i, r);
            f.multiplyValueAt(i, r);

            if (i > uCrackedLinePos + 2) {
                r = lSideDiagonal.getValueAt(i - 1);
                lSideDiagonal.setValueAt(i - 1, 0.0);
                sideDiagonal.subtractValueAt(i - 1, r * rSideDiagonal.getValueAt(i));
                f.subtractValueAt(i - 1, r * f.getValueAt(i));
            }

            r = dCrackedLine.getValueAt(size - 1 - i);
            dCrackedLine.setValueAt(size - 1 - i, 0.0);
            dCrackedLine.subtractValueAt(size - i, r * rSideDiagonal.getValueAt(i));
            f.subtractValueAt(uCrackedLinePos + 1, r * f.getValueAt(i));

            r = uCrackedLine.getValueAt(size - 1 - i);
            uCrackedLine.setValueAt(size - 1 - i, 0.0);
            uCrackedLine.subtractValueAt(size - i, r * rSideDiagonal.getValueAt(i));
            f.subtractValueAt(uCrackedLinePos, r * f.getValueAt(i));
        }


        // Шаг 3 (калибровка линий-помех)
        var r = 1 / dCrackedLine.getValueAt(size - uCrackedLinePos - 2);
        dCrackedLine.setValueAt(size - uCrackedLinePos - 2, 1.0);
        dCrackedLine.multiplyValueAt(size - uCrackedLinePos - 1, r);
        f.multiplyValueAt(uCrackedLinePos + 1, r);

        r = uCrackedLine.getValueAt(size - uCrackedLinePos - 2);
        uCrackedLine.setValueAt(size - uCrackedLinePos - 2, 0.0);
        uCrackedLine.subtractValueAt(
                size - uCrackedLinePos - 1,
                r * dCrackedLine.getValueAt(size - uCrackedLinePos - 1)
        );
        f.subtractValueAt(uCrackedLinePos, r * f.getValueAt(uCrackedLinePos + 1));

        r = 1 / uCrackedLine.getValueAt(size - uCrackedLinePos - 1);
        uCrackedLine.setValueAt(size - uCrackedLinePos - 1, 1.0);
        f.multiplyValueAt(uCrackedLinePos, r);

        r = dCrackedLine.getValueAt(size - uCrackedLinePos - 1);
        dCrackedLine.setValueAt(size - uCrackedLinePos - 1, 0.0);
        f.subtractValueAt(uCrackedLinePos + 1, r * f.getValueAt(uCrackedLinePos));

        // Значения "cracked lines" верны, осталось подогнать под них диагонали
        lSideDiagonal.setValueAt(uCrackedLinePos, 0.0);
        lSideDiagonal.setValueAt(uCrackedLinePos + 1, 0.0);
        sideDiagonal.setValueAt(uCrackedLinePos, 1.0);
        sideDiagonal.setValueAt(uCrackedLinePos + 1, 1.0);
        rSideDiagonal.setValueAt(uCrackedLinePos, 0.0);
        rSideDiagonal.setValueAt(uCrackedLinePos + 1, 0.0);

        var x = new Vector(size);

        // Прогонка "вверх"
        x.setValueAt(size - uCrackedLinePos - 1, f.getValueAt(uCrackedLinePos));
        for (int i = size - uCrackedLinePos - 1; i >= 0; --i) {
            x.setValueAt(
                    size - i - 1,
                    f.getValueAt(i) - lSideDiagonal.getValueAt(i) * x.getValueAt(size - i - 2)
            );
        }

        // Прогонка "вниз"
        x.setValueAt(size - uCrackedLinePos - 2, f.getValueAt(uCrackedLinePos + 1));
        for (int i = uCrackedLinePos + 2; i < size; ++i) {
            x.setValueAt(
                    size - i - 1,
                    f.getValueAt(i) - rSideDiagonal.getValueAt(i) * x.getValueAt(size - i)
            );
        }

        return x;
    }

    public static Vector multiply(CrackedMatrix crackedMatrix, Vector vector) {

        if (!crackedMatrix.getSize().equals(vector.getSize())) {
            throw new RuntimeException("Размерности матрицы и вектора несовместимы");
        }

        int size = vector.getSize();

        var uCrackedLinePos = crackedMatrix.getUCrackedLinePos();

        var uCrackedLine = crackedMatrix.getUCrackedLine();
        var dCrackedLine = crackedMatrix.getDCrackedLine();

        Vector result = new Vector(size);

        // Верхняя кодиагональ
        var lSideDiagonal = crackedMatrix.getLSideDiagonal();

        // Побочная диагональ
        var sideDiagonal = crackedMatrix.getSideDiagonal();

        // Нижняя кодиагональ
        var rSideDiagonal = crackedMatrix.getRSideDiagonal();

        for (int i = 0; i < size; ++i) {

            if (i == uCrackedLinePos) {
                double uRowSum = 0.0;
                for (int j = 0; j < size; ++j) {
                    uRowSum += uCrackedLine.getValueAt(j) * vector.getValueAt(j);
                }
                result.setValueAt(uCrackedLinePos, uRowSum);
                continue;
            }

            if (i == uCrackedLinePos + 1) {
                double dRowSum = 0.0;
                for (int j = 0; j < size; ++j) {
                    dRowSum += dCrackedLine.getValueAt(j) * vector.getValueAt(j);
                }
                result.setValueAt(uCrackedLinePos + 1, dRowSum);
                continue;
            }

            double lValue = i == size - 1 ? 0.0 : vector.getValueAt(size - 2 - i);
            double mValue = vector.getValueAt(size - 1 - i);
            double rValue = i == 0 ? 0.0 : vector.getValueAt(size - i);

            result.setValueAt(
                    i,
                    lSideDiagonal.getValueAt(i) * lValue +
                            sideDiagonal.getValueAt(i) * mValue +
                            rSideDiagonal.getValueAt(i) * rValue
            );
        }

        return result;
    }
}