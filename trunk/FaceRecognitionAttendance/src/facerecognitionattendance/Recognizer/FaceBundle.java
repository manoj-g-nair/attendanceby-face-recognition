package facerecognitionattendance.Recognizer;

import java.io.Serializable;
import java.util.*;

public class FaceBundle implements Serializable {

    private double[][] imageRows;
    private ArrayList<String> imageFnms;
    private double[] avgImage;
    private double[][] eigenFaces;
    private double[] eigenValues;
    private int imageWidth, imageHeight;

    public FaceBundle(ArrayList<String> nms, double[][] ims, double[] avgImg,
            double[][] facesMat, double[] evals, int w, int h) {
        imageFnms = nms;
        imageRows = ims;
        avgImage = avgImg;
        eigenFaces = facesMat;
        eigenValues = evals;
        imageWidth = w;
        imageHeight = h;
    }

    public double[][] getImages() {
        return imageRows;
    }

    public double[][] getEigenFaces() {
        return eigenFaces;
    }

    public int getNumEigenFaces() {
        return eigenFaces.length;
    }

    public double[] getAvgImage() {
        return avgImage;
    }

    public double[] getEigenValues() {
        return eigenValues;
    }

    public ArrayList<String> getImageFnms() {
        return imageFnms;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public double[][] calcWeights(int numEFs) {
        Matrix2D imsMat = new Matrix2D(imageRows);

        Matrix2D facesMat = new Matrix2D(eigenFaces);
        Matrix2D facesSubMatTr = facesMat.getSubMatrix(numEFs).transpose();

        Matrix2D weights = imsMat.multiply(facesSubMatTr);
        return weights.toArray();
    }

}
