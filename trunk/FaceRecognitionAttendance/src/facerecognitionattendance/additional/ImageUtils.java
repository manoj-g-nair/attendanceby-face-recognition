package facerecognitionattendance.additional;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

public class ImageUtils {

    public static BufferedImage createImFromArr(double[] imData, int width) {
        BufferedImage im = null;
        try {
            im = new BufferedImage(width, imData.length / width, BufferedImage.TYPE_BYTE_GRAY);
            Raster rast = im.getData();
            WritableRaster wr = rast.createCompatibleWritableRaster();
            double maxVal = Double.MIN_VALUE;
            double minVal = Double.MAX_VALUE;

            for (int i = 0; i < imData.length; i++) {
                maxVal = Math.max(maxVal, imData[i]);
                minVal = Math.min(minVal, imData[i]);
            }

            for (int j = 0; j < imData.length; j++) {
                imData[j] = ((imData[j] - minVal) * 255) / (maxVal - minVal);
            }
            wr.setPixels(0, 0, width, imData.length / width, imData);
            im.setData(wr);
        } catch (Exception e) {
            System.out.println(e);
        }
        return im;
    } 

    public static double[] createArrFromIm(BufferedImage im) {
        int imWidth = im.getWidth();
        int imHeight = im.getHeight();

        double[] imArr = new double[imWidth * imHeight];
        im.getData().getPixels(0, 0, imWidth, imHeight, imArr);
        return imArr;
    } 
    public static BufferedImage toScaledGray(BufferedImage im, double scale) // scale and convert to grayscale 
    {
        int imWidth = im.getWidth();
        int imHeight = im.getHeight();

        int nWidth = (int) Math.round(imWidth * scale);
        int nHeight = (int) Math.round(imHeight * scale);
        BufferedImage grayIm = new BufferedImage(nWidth, nHeight,
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = grayIm.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(im, 0, 0, nWidth, nHeight, 0, 0, imWidth, imHeight, null);
        g2.dispose();

        return grayIm;
    }

    public static BufferedImage clipToRectangle(BufferedImage im, int x, int y, int width, int height) {
        BufferedImage clipIm = null;
        try {
            clipIm = im.getSubimage(x, y, width, height);
        } catch (RasterFormatException e) {
            System.out.println("Could not clip the image");
            clipIm = im;
        }
        return clipIm;
    } 

    public static void checkImSizes(ArrayList<String> fNms, BufferedImage[] ims) {
        int imWidth = ims[0].getWidth();
        int imHeight = ims[0].getHeight();
        System.out.println("Image (w,h): (" + imWidth + ", " + imHeight + ")");

        for (int i = 1; i < ims.length; i++) {
            if ((ims[i].getHeight() != imHeight)
                    || (ims[i].getWidth() != imWidth)) {
                System.out.println("All images should have be the same size; "
                        + fNms.get(i) + " is a different size");
                System.exit(1);
            }
        }
    }	
}
