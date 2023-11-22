package com.k4r3l1ns;


import com.k4r3l1ns.models.CrackedMatrix;
import com.k4r3l1ns.models.Vector;
import com.k4r3l1ns.service.MatrixOperationUnit;

import java.io.*;

public class FirstLabApplication {

    private static final String FILES_DIRECTORY = "/home/k4r3l1ns/Desktop/NM_labs/first_lab/src/main/resources/";

    private static final File MATRIX_FILE = new File(FILES_DIRECTORY + "matrix.txt");
    private static final File VECTOR_FILE = new File(FILES_DIRECTORY + "vector.txt");
    private static final File DESTINATION_FILE = new File(FILES_DIRECTORY + "result.txt");

    public static void main(String[] args) {

        try {

            InputStream matrixFile = new FileInputStream(MATRIX_FILE);
            InputStream vectorFile = new FileInputStream(VECTOR_FILE);
            Writer writer = new FileWriter(DESTINATION_FILE, false);

            var crackedMatrix = CrackedMatrix.read(matrixFile);
            var vector = Vector.read(vectorFile);

            var result = MatrixOperationUnit.solveEquation(crackedMatrix, vector);
            result.write(writer);

        } catch (FileNotFoundException ex) {
            System.out.println("Файл не найден");
        } catch (SecurityException ex) {
            System.out.println("Файл для записи обладает недоступными настройками доступа");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}