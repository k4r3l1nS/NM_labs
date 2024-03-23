package com.k4r3l1ns.service;

import com.k4r3l1ns.models.Matrix;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JacobiRotationMethod {


    /**
     * Максимальное по модулю значение внедиагональных
     * элементов преобразованной матрицы
     */
    double eps;

    /**
     * Поиск собственных значений симметричной матрицы методом вращения Якоби
     *
     * @param symmetricMatrix Симметричная матрица
     * @param rotationLimit Максимальное допустимое число вращений
     * @param eps Максимальное по модулю значение внедиагональных элементов преобразованной матрицы
     * @return Собственный вектор
     */
    public static double[] apply(
            Matrix symmetricMatrix,
            int rotationLimit,
            double eps
    ) {
        Matrix matrix = Matrix.copyOf(symmetricMatrix);
        int size = matrix.getSize();

        double[] eigenvector = matrix.getDiagValues();

        int rotationCount;
        for (rotationCount = 0; rotationCount < rotationLimit; ++rotationCount) {

            double norm = matrix.findNorm();
            if (norm < eps) {
                break;
            }

            int[] maxElemIndexes = matrix.findMaxElemIndexes();
            int p = maxElemIndexes[0];
            int q = maxElemIndexes[1];

            var diagValues = matrix.getDiagValues();
            double rotationAngle = 0.5 * Math.atan2(
                    2 * norm, (diagValues[q] - diagValues[p])
            );

            Matrix matrixT = Matrix.identityMatrix(size);
            var matrixTDiagValues = matrixT.getDiagValues();
            var matrixTValues = matrixT.getValues();
            matrixTDiagValues[p] = matrixTDiagValues[q] =
                    matrixTValues[p][p] =
                            matrixTValues[q][q] = Math.cos(rotationAngle);
            matrixTValues[p][q] = -Math.sin(rotationAngle);
            matrixTValues[q][p] = Math.sin(rotationAngle);

            matrix = Matrix.multiply(
                    Matrix.copyOf(matrixT).transpose(),
                    matrix
            );
            matrix = Matrix.multiply(matrix, matrixT);

            eigenvector = matrix.getDiagValues();
        }

//        System.out.println("Вычисление выполнено успешно.");
        System.out.println("Кол-во ротаций: " + rotationCount);
        return eigenvector;
    }
}
