package com.k4r3l1ns;

import com.k4r3l1ns.models.TapeMatrix;
import com.k4r3l1ns.models.Vector;
import com.k4r3l1ns.service.TapeMatrixOperationUnit;

public class SecondLabApplication {

    public static final int SIZE = 7;

    public static final int TAPE_WIDTH = 3;

    public static final double[] RANGE = {1.0, 10.0};

    public static void main(String[] args) {

        TapeMatrix tapeMatrix = TapeMatrix.wellConditionedMatrix(SIZE, TAPE_WIDTH, RANGE);
        Vector xPrecise = new Vector(SIZE).fillWithRandomValues(RANGE[0], RANGE[1]);

        var vector = TapeMatrixOperationUnit.multiply(tapeMatrix, xPrecise);
        var result = TapeMatrixOperationUnit.solveEquation(tapeMatrix, vector);

        xPrecise.print();
        result.print();

        System.out.println(Vector.copyOf(result).subtract(xPrecise).findNorm());

    }
}