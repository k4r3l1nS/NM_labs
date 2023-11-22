package com.k4r3l1ns;


import com.k4r3l1ns.models.CrackedMatrix;
import com.k4r3l1ns.models.Vector;

import java.io.*;

public class Main {

    private static final String FILES_DIRECTORY = "/home/k4r3l1ns/Desktop/NM_labs/first_lab/src/main/resources/";

    private static final File SOURCE_FILE = new File(FILES_DIRECTORY + "source.txt");
    private static final File DESTINATION_FILE = new File(FILES_DIRECTORY + "result.txt");

    public static void main(String[] args) {

        try {

            InputStream fileInputStream = new FileInputStream(SOURCE_FILE);
            Writer writer = new FileWriter(DESTINATION_FILE, false);

            CrackedMatrix.read(fileInputStream).write(writer);

        } catch (FileNotFoundException ex) {
            System.out.println("Файл не найден");
        } catch (SecurityException ex) {
            System.out.println("Файл для записи обладает недоступными настройками доступа");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}