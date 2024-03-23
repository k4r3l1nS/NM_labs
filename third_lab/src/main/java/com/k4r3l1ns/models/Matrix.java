package com.k4r3l1ns.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@NoArgsConstructor
public class Matrix {

    private double[][] values;
    private double[] diagValues;
    int size = -1;

    public Matrix(int size) {
        this.size = size;
        values = new double[size][size];
        diagValues = new double[size];
    }

    public static Matrix read(InputStream inputStream) {

        Scanner scanner = new Scanner(inputStream);

        int lineIndex = 0;
        Matrix result = new Matrix();
        result.size = -1;

        while (
                scanner.hasNextLine() &&
                        (result.size == -1 || lineIndex < result.size)
        ) {
            String line = scanner.nextLine();

            List<Double> row = new ArrayList<>();
            Scanner scanner2 = new Scanner(line);

            while (scanner2.hasNext()) {
                row.add(scanner2.nextDouble());
            }

            if (result.size == -1) {
                result = new Matrix(row.size());
            } else if (result.size != row.size()) {
                throw new RuntimeException("Входные данные не являются матрицей");
            }

            result.values[lineIndex] = row.stream().mapToDouble(x -> x).toArray();

            ++lineIndex;
        }

        for (int index = 0; index < result.size; ++index) {
            result.diagValues[index] = result.values[index][index];
        }

        throwIfNotSymmetric(result);

        return result;
    }

    public void print() {

        for (int i = 0; i < size; ++i) {
            for (var value : values[i]) {
                System.out.printf("%.2f\t", value);
            }
            System.out.println();
        }
    }

    private static void throwIfNotSymmetric(Matrix matrix) {

        if (!isSymmetric(matrix)) {
            throw new RuntimeException("Матрица не симметрична");
        }
    }

    private static boolean isSymmetric(Matrix matrix) {

        if (matrix.size == -1) {
            return false;
        }

        for (int lineIndex = 0; lineIndex < matrix.size; ++lineIndex) {
            for (int columnIndex = lineIndex + 1; columnIndex < matrix.size; ++columnIndex) {
                if (
                        matrix.values[lineIndex][columnIndex] !=
                        matrix.values[columnIndex][lineIndex]
                ) {
                    return false;
                }
            }
        }

        return true;
    }

    public static Matrix copyOf(Matrix matrix) {

        Matrix copy = new Matrix(matrix.size);

        System.arraycopy(matrix.values, 0, copy.values, 0, copy.size);
        System.arraycopy(matrix.diagValues, 0, copy.diagValues, 0, copy.size);

        return copy;
    }

    public double findNorm() {

        double norm = 0;

        for (int lineIndex = 0; lineIndex < size; ++lineIndex) {
            for (int columnIndex = lineIndex + 1; columnIndex < size; ++columnIndex) {
                double currentValue = Math.abs(values[lineIndex][columnIndex]);
                if (currentValue > norm) {
                    norm = currentValue;
                }
            }
        }

        return norm;
    }

    public int[] findMaxElemIndexes() {

        int[] result = { -1, -1 };
        double norm = 0;

        for (int lineIndex = 0; lineIndex < size; ++lineIndex) {
            for (int columnIndex = lineIndex + 1; columnIndex < size; ++columnIndex) {
                double currentValue = Math.abs(values[lineIndex][columnIndex]);
                if (currentValue > norm) {
                    norm = currentValue;
                    result[0] = lineIndex;
                    result[1] = columnIndex;
                }
            }
        }

        return result;
    }

    public static Matrix identityMatrix(int size) {

        Matrix result = new Matrix(size);
        for (int i = 0; i < size; ++i) {
            result.values[i][i] = 1;
            result.diagValues[i] = 1;
        }
        return result;
    }

    public Matrix transpose() {

        Matrix result = new Matrix(size);

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                result.values[j][i] = values[i][j];
            }
        }

        result.diagValues = diagValues.clone();

        return result;
    }

    public static Matrix multiply(Matrix matrix1, Matrix matrix2) {

        int size = matrix1.size;
        Matrix result = new Matrix(size);

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                for (int k = 0; k < size; ++k) {
                    result.values[i][j] += matrix1.values[i][k] * matrix2.values[k][j];
                }
            }
        }

        for (int i = 0; i < size; ++i) {
            result.diagValues[i] = result.values[i][i];
        }

        return result;
    }

    /**
     * Матрица, полученная преобразованием Хаусхолдера по вектору собственных значений
     *
     * @param eigenvector Вектор собственных значений
     * @return Матрица с заранее известными собственными значениями
     */
    public static Matrix predefinedMatrix(double[] eigenvector, double[] range) {

        int size = eigenvector.length;

        Matrix result = new Matrix(size);
        for (int i = 0; i < size; ++i) {
            result.values[i][i] = result.diagValues[i] = eigenvector[i];
        }

        double z = 0;
        double[] randomVector = new double[size];
        for (int i = 0; i < size; ++i) {
            randomVector[i] = ThreadLocalRandom.current().nextDouble(range[0], range[1]);
            z += randomVector[i] * randomVector[i];
        }
        z = Math.pow(z, 0.5);
        for (int i = 0; i < size; ++i) {
            randomVector[i] /= z;
        }

        Matrix householderMatrix = new Matrix(size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (i == j) {
                    householderMatrix.values[i][i] =
                            householderMatrix.diagValues[i] = 1 - 2 * randomVector[i] * randomVector[i];
                } else {
                    householderMatrix.values[i][j] = -2 * randomVector[i] * randomVector[j];
                }
            }
        }

        result = Matrix.multiply(householderMatrix, result);
        result = Matrix.multiply(result, Matrix.copyOf(householderMatrix).transpose());

        return result;
    }
}
