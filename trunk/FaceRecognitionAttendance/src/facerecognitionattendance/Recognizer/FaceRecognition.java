package facerecognitionattendance.Recognizer;

import facerecognitionattendance.additional.MatchResult;
import facerecognitionattendance.additional.FileUtils;
import facerecognitionattendance.additional.ImageDistanceInfo;
import facerecognitionattendance.additional.ImageUtils;
import java.awt.image.*;
import java.util.*;

public class FaceRecognition {

    private static final float FACES_FRAC = 0.75f;

    private FaceBundle bundle = null;
    private double[][] weights = null;
    private int numEFs = 0;

    public FaceRecognition(int numEigenFaces) {
        bundle = FileUtils.readCache();
        if (bundle == null) {
            System.exit(1);
        }

        int numFaces = bundle.getNumEigenFaces();
        numEFs = numEigenFaces;
        if ((numEFs < 1) || (numEFs > numFaces - 1)) {
            numEFs = Math.round((numFaces - 1) * FACES_FRAC);
            System.out.println("Number of matching eigenfaces must be in the range (1-"
                    + (numFaces - 1) + ")" + "; using " + numEFs);
        } else {
            System.out.println("Number of eigenfaces: " + numEFs);
        }

        weights = bundle.calcWeights(numEFs);
    }

    public MatchResult match(String imFnm) {
        if (!imFnm.endsWith(".png")) {
            System.out.println("Input image must be a PNG file");
            return null;
        } else {
            System.out.println("Matching " + imFnm);
        }

        BufferedImage image = FileUtils.loadImage(imFnm);
        if (image == null) {
            return null;
        }

        return match(image);
    }

    public MatchResult match(BufferedImage im) {
        if (bundle == null) {
            System.out.println("You must build an Eigenfaces cache before any matching");
            return null;
        }

        return findMatch(im);
    }

    private MatchResult findMatch(BufferedImage im) {
        double[] imArr = ImageUtils.createArrFromIm(im);
        Matrix2D imMat = new Matrix2D(imArr, 1);
        imMat.normalize();

        imMat.subtract(new Matrix2D(bundle.getAvgImage(), 1));
        Matrix2D imWeights = getImageWeights(numEFs, imMat);

        double[] dists = getDists(imWeights);
        ImageDistanceInfo distInfo = getMinDistInfo(dists);

        ArrayList<String> imageFNms = bundle.getImageFnms();
        String matchingFNm = imageFNms.get(distInfo.getIndex());
        double minDist = Math.sqrt(distInfo.getValue());
        if (minDist > 0.4) {
            return null;
        }

        return new MatchResult(matchingFNm, minDist);
    }

    private Matrix2D getImageWeights(int numEFs, Matrix2D imMat) {
        Matrix2D egFacesMat = new Matrix2D(bundle.getEigenFaces());
        Matrix2D egFacesMatPart = egFacesMat.getSubMatrix(numEFs);
        Matrix2D egFacesMatPartTr = egFacesMatPart.transpose();

        return imMat.multiply(egFacesMatPartTr);
    }

    private double[] getDists(Matrix2D imWeights) {
        Matrix2D tempWt = new Matrix2D(weights);
        double[] wts = imWeights.flatten();

        tempWt.subtractFromEachRow(wts);
        tempWt.multiplyElementWise(tempWt);
        double[][] sqrWDiffs = tempWt.toArray();
        double[] dists = new double[sqrWDiffs.length];

        for (int row = 0; row < sqrWDiffs.length; row++) {
            double sum = 0.0;
            for (int col = 0; col < sqrWDiffs[0].length; col++) {
                sum += sqrWDiffs[row][col];
            }
            dists[row] = sum;
        }
        return dists;
    }

    private ImageDistanceInfo getMinDistInfo(double[] dists) {
        double minDist = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < dists.length; i++) {
            if (dists[i] < minDist) {
                minDist = dists[i];
                index = i;
            }
        }
        return new ImageDistanceInfo(dists[index], index);
    }

}
