package facerecognitionattendance.additional;

public class ImageDistanceInfo {

    private int index;
    private double value;

    public ImageDistanceInfo(double val, int idx) {
        value = val;
        index = idx;
    }

    public int getIndex() {
        return index;
    }

    public double getValue() {
        return value;
    }

} 

