package facerecognitionattendance.Recognizer;

import facerecognitionattendance.additional.FileUtils;
import facerecognitionattendance.Recognizer.Matrix2D;
import facerecognitionattendance.Recognizer.FaceBundle;
import facerecognitionattendance.Recognizer.EigenvalueDecomp;
import java.awt.image.*;
import java.util.*;

public class BuildEigenFaces {

    public static void build() {
        ArrayList<String> fnms = FileUtils.getTrainingFnms();
        FaceBundle bundle = makeBundle(fnms);
        FileUtils.writeCache(bundle);
    }

    private static FaceBundle makeBundle(ArrayList<String> fnms) {
        BufferedImage[] ims = FileUtils.loadTrainingIms(fnms);

        Matrix2D imsMat = convertToNormMat(ims);
        double[] avgImage = imsMat.getAverageOfEachColumn();
        imsMat.subtractMean();
        Matrix2D imsDataTr = imsMat.transpose();
        Matrix2D covarMat = imsMat.multiply(imsDataTr);
        EigenvalueDecomp egValDecomp = covarMat.getEigenvalueDecomp();
        double[] egVals = egValDecomp.getEigenValues();
        double[][] egVecs = egValDecomp.getEigenVectors();

        sortEigenInfo(egVals, egVecs);

        Matrix2D egFaces = getNormEgFaces(imsMat, new Matrix2D(egVecs));

        FileUtils.saveEFIms(egFaces, ims[0].getWidth());

        return new FaceBundle(fnms, imsMat.toArray(), avgImage,
                egFaces.toArray(), egVals, ims[0].getWidth(), ims[0].getHeight());
    }

    private static Matrix2D convertToNormMat(BufferedImage[] ims) {
        int imWidth = ims[0].getWidth();
        int imHeight = ims[0].getHeight();

        int numRows = ims.length;
        int numCols = imWidth * imHeight;
        double[][] data = new double[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            ims[i].getData().getPixels(0, 0, imWidth, imHeight, data[i]);
        }
        Matrix2D imsMat = new Matrix2D(data);
        imsMat.normalize();
        return imsMat;
    }

    private static Matrix2D getNormEgFaces(Matrix2D imsMat, Matrix2D egVecs) {
        Matrix2D egVecsTr = egVecs.transpose();
        Matrix2D egFaces = egVecsTr.multiply(imsMat);
        double[][] egFacesData = egFaces.toArray();

        for (int row = 0; row < egFacesData.length; row++) {
            double norm = Matrix2D.norm(egFacesData[row]);
            for (int col = 0; col < egFacesData[row].length; col++) {
                egFacesData[row][col] = egFacesData[row][col] / norm;
            }
        }
        return new Matrix2D(egFacesData);
    }

    private static void sortEigenInfo(double[] egVals, double[][] egVecs) {
        Double[] egDvals = getEgValsAsDoubles(egVals);
        Hashtable<Double, double[]> table = new Hashtable<Double, double[]>();
        for (int i = 0; i < egDvals.length; i++) {
            table.put(egDvals[i], getColumn(egVecs, i));
        }

        ArrayList<Double> sortedKeyList = sortKeysDescending(table);
        updateEgVecs(egVecs, table, egDvals, sortedKeyList);
        Double[] sortedKeys = new Double[sortedKeyList.size()];
        sortedKeyList.toArray(sortedKeys);
        for (int i = 0; i < sortedKeys.length; i++) {
            egVals[i] = sortedKeys[i].doubleValue();
        }
    }

    private static Double[] getEgValsAsDoubles(double[] egVals) {
        Double[] egDvals = new Double[egVals.length];
        for (int i = 0; i < egVals.length; i++) {
            egDvals[i] = new Double(egVals[i]);
        }
        return egDvals;
    }

    private static double[] getColumn(double[][] vecs, int col) {
        double[] res = new double[vecs.length];
        for (int i = 0; i < vecs.length; i++) {
            res[i] = vecs[i][col];
        }
        return res;
    }

    private static ArrayList<Double> sortKeysDescending(
            Hashtable<Double, double[]> table) {
        ArrayList<Double> keyList = Collections.list(table.keys());
        Collections.sort(keyList, Collections.reverseOrder());
        return keyList;
    }

    private static void updateEgVecs(double[][] egVecs,
            Hashtable<Double, double[]> table,
            Double[] egDvals, ArrayList<Double> sortedKeyList) {
        for (int col = 0; col < egDvals.length; col++) {
            double[] egVec = table.get(sortedKeyList.get(col));
            for (int row = 0; row < egVec.length; row++) {
                egVecs[row][col] = egVec[row];
            }
        }
    } 
}

