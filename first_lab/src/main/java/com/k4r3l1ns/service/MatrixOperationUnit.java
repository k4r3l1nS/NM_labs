package com.k4r3l1ns.service;

import com.k4r3l1ns.models.CrackedMatrix;
import com.k4r3l1ns.models.Vector;

public class MatrixOperationUnit {

    public static Vector solveEquation(CrackedMatrix a, Vector f) {

        if (a.getSize() != f.getSize()) {
            throw new RuntimeException("Размерности матрицы и вектора несовместимы");
        }

        int size = a.getSize();

        // Верхняя кодиагональ
        var lSideDiagonal = a.getLSideDiagonal();

        // Побочная диагональ
        var sideDiagonal = a.getSideDiagonal();

        // Нижняя кодиагональ
        var rSideDiagonal = a.getRSideDiagonal();

        var uCrackedLine = a.getUCrackedLine();
        var dCrackedLine = a.getDCrackedLine();

        // Шаг 2 (шаг 1 пропускается, т.к. первая "испорченная" диагональ на верхней строке матрицы)
        for (int i = size - 1; i > 1; --i) {

            var r = 1 / sideDiagonal.getValueAt(i);
            sideDiagonal.setValueAt(i, 1);
            rSideDiagonal.multiplyValueAt(i, r);
            f.multiplyValueAt(i, r);

            r = lSideDiagonal.getValueAt(i - 1);
            lSideDiagonal.setValueAt(i - 1, 0.0);
            sideDiagonal.subtractValueAt(i - 1, r * rSideDiagonal.getValueAt(i));
            f.subtractValueAt(i - 1, r * f.getValueAt(i));

            r = dCrackedLine.getValueAt(size - 1 - i);
            dCrackedLine.setValueAt(size - 1 - i, 0.0);
            dCrackedLine.subtractValueAt(size - i, r * rSideDiagonal.getValueAt(i));
            f.subtractValueAt(1, r * f.getValueAt(i));

            r = uCrackedLine.getValueAt(size - 1 - i);
            uCrackedLine.setValueAt(size - 1 - i, 0.0);
            uCrackedLine.subtractValueAt(size - i, r * rSideDiagonal.getValueAt(i));
            f.subtractValueAt(0, r * f.getValueAt(i));
        }

        var r = 1 / dCrackedLine.getValueAt(size - 2);
        dCrackedLine.setValueAt(size - 2, 1.0);

        dCrackedLine.multiplyValueAt(size - 1, r);
        f.multiplyValueAt(1, r);

        r = uCrackedLine.getValueAt(size - 2);
        uCrackedLine.setValueAt(size - 2, 0.0);

        uCrackedLine.subtractValueAt(size - 1, r * dCrackedLine.getValueAt(size - 1));
        f.subtractValueAt(0, r * f.getValueAt(1));

        r = 1 / uCrackedLine.getValueAt(size - 1);
        uCrackedLine.setValueAt(size - 1, 1.0);
        f.multiplyValueAt(0, r);

        // Значения "cracked lines" верны, осталось подогнать под них диагонали 0 и 1 рядов
        lSideDiagonal.setValueAt(0, 0.0);
        sideDiagonal.setValueAt(0, uCrackedLine.getValueAt(size - 1));
        sideDiagonal.setValueAt(1, dCrackedLine.getValueAt(size - 2));
        rSideDiagonal.setValueAt(1, dCrackedLine.getValueAt(size - 1));

        var x = new Vector(size);

        x.setValueAt(size - 1, f.getValueAt(0));
        for (int i = size - 2; i >= 0; --i) {
            x.setValueAt(
                    i,
                    f.getValueAt(size - 1 - i) - rSideDiagonal.getValueAt(size - 1 - i) * x.getValueAt(i + 1)
            );
        }

        return x;
    }
}
