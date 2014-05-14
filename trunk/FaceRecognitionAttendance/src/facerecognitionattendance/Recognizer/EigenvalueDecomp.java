package facerecognitionattendance.Recognizer;

import cern.colt.matrix.linalg.EigenvalueDecomposition;

public class EigenvalueDecomp extends EigenvalueDecomposition {

    public EigenvalueDecomp(Matrix2D dmat) {
        super(dmat);
    }

    public double[] getEigenValues() {
        return diag(getD().toArray());
    }

    public double[][] getEigenVectors() {
        return getV().toArray();
    }

    private double[] diag(double[][] m) {
        double[] diag = new double[m.length];
        for (int i = 0; i < m.length; i++) {
            diag[i] = m[i][i];
        }
        return diag;
    }
}
