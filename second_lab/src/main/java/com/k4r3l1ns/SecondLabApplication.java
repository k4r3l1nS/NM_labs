package com.k4r3l1ns;

import com.k4r3l1ns.models.CrackedMatrix;
import com.k4r3l1ns.models.Vector;
import com.k4r3l1ns.service.MatrixOperationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SecondLabApplication {

    private static final String FILES_DIRECTORY = "/home/k4r3l1ns/Desktop/NM_labs/second_lab/src/main/resources/";

    private static final File MATRIX_FILE = new File(FILES_DIRECTORY + "matrix.txt");
    private static final File VECTOR_FILE = new File(FILES_DIRECTORY + "vector.txt");
    private static final File X_PRECISE_FILE = new File(FILES_DIRECTORY + "precise.txt");
    private static final File DESTINATION_FILE = new File(FILES_DIRECTORY + "result.txt");

    public static void main(String[] args) {

        try {
            InputStream matrixFileStream = new FileInputStream(MATRIX_FILE);
            InputStream vectorFileStream = new FileInputStream(VECTOR_FILE);
            InputStream xPreciseFileStream = new FileInputStream(X_PRECISE_FILE);

            var crackedMatrix = CrackedMatrix.read(matrixFileStream);
            var xPrecise = Vector.read(xPreciseFileStream);
            var vector = Vector.read(vectorFileStream);

//            System.out.println("\n\n" + crackedMatrix.getUCrackedLinePos() + "\n\n");
//            crackedMatrix.print();
            var result = MatrixOperationUnit.solveEquation(crackedMatrix, vector);

            result.print();
            System.out.println(
                    MatrixOperationUnit.multiply(crackedMatrix, result).subtract(vector).findNorm()
            );


//            MatrixOperationUnit.multiply(crackedMatrix, result).print();

//            MatrixOperationUnit.multiply(crackedMatrix, xPrecise).print();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}