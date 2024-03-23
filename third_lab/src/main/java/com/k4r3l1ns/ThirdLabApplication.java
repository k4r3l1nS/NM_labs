package com.k4r3l1ns;

import com.k4r3l1ns.models.Matrix;
import com.k4r3l1ns.service.JacobiRotationMethod;

import java.io.FileInputStream;
import java.io.IOException;

public class ThirdLabApplication {

    public static final String SOURCE_DIRECTORY = "/home/k4r3l1ns/Desktop/NM_labs/third_lab/src/main/resources/";

    public static final String MATRIX_FILENAME = "matrix.txt";

    public static final int ROTATION_LIMIT = 1000000000;
    public static final double EPSILON = 1e-3;

    public static void main(String[] args) {

        try {

            Matrix symmetricMatrix =
                    Matrix.read(new FileInputStream(SOURCE_DIRECTORY + MATRIX_FILENAME));

            symmetricMatrix.print();

            double[] eigenvector = JacobiRotationMethod.apply(symmetricMatrix, ROTATION_LIMIT, EPSILON);

            System.out.println("\nСобственный вектор имеет значения:");
            for (double value : eigenvector) {
                System.out.printf("%.2f\t", value);
            }
            System.out.println();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}