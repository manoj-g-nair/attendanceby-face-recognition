package facerecognitionattendance.GUI;

import facerecognitionattendance.Recognizer.JMFCapture;
import facerecognitionattendance.additional.MatchResult;
import facerecognitionattendance.Recognizer.FaceRecognition;
import facerecognitionattendance.GUI.NewStudPanel;
import facerecognitionattendance.GUI.AttendancePanel;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.text.DecimalFormat;
import java.io.*;
import javax.imageio.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacpp.Loader;
import facerecognitionattendance.Recognizer.BuildEigenFaces;
import facerecognitionattendance.additional.DBConnection;
import facerecognitionattendance.additional.ImageUtils;
import facerecognitionattendance.additional.Student;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import java.util.concurrent.Executors;

public class FaceRecogPanel extends JPanel implements Runnable {

    private static final Dimension PANEL_SIZE = new Dimension(200, 50);
    private static final int DELAY = 30;
    private static final int IM_SCALE = 4;
    private static final int DETECT_DELAY = 0;
    private static final int MAX_TASKS = 4;
    private static final String FACE_CASCADE_FNM = "haarcascade_frontalface_alt.xml";

    private static final String FACE_DIR = "trainingImages";
    private int fileCount = 0;
    private static final int FACE_WIDTH = 125;
    private static final int FACE_HEIGHT = 150;

    private JFrame top;
    private BufferedImage image = null;
    private JMFCapture camera;
    private volatile boolean isRunning;
    private int imageCount = 0;
    private long totalTime = 0;
    private DecimalFormat df;
    private Font msgFont;
    private CvHaarClassifierCascade classifier;
    private CvMemStorage storage;
    private ExecutorService executor;
    private AtomicInteger numTasks;
    private long detectStartTime = 0;

    private Rectangle faceRect;

    private volatile boolean recognizeFace = false;
    private FaceRecognition faceRecog;
    private String faceID = null;
    private volatile boolean saveFace = false;
    private String ID = "";
    private long recogStartTime = 0;
    private double minDist = Double.MAX_VALUE;
    private String maxDistFaceName;
    private AttendancePanel attPanel;
    private NewStudPanel newStudPanel;
    private DBConnection connection;

    public FaceRecogPanel(AttendancePanel attPanel, NewStudPanel newStudPanel) {
        this.attPanel = attPanel;
        this.newStudPanel = newStudPanel;
        connection = new DBConnection();
        setBackground(Color.white);
        setPreferredSize(PANEL_SIZE);
        df = new DecimalFormat("0.#");
        msgFont = new Font("SansSerif", Font.BOLD, 18);
        faceRecog = new FaceRecognition(22);
        executor = Executors.newSingleThreadExecutor();
        numTasks = new AtomicInteger(0);
        faceRect = new Rectangle();

        initOpenCV();
        new Thread(this).start();
    }

    private void initOpenCV() {
        Loader.load(opencv_objdetect.class);
        classifier = new CvHaarClassifierCascade(cvLoad(FACE_CASCADE_FNM));
        if (classifier.isNull()) {
            System.out.println("\nClassifier file: " + FACE_CASCADE_FNM);
            System.exit(1);
        }
        storage = CvMemStorage.create();
    }

    public void run() {
        camera = new JMFCapture();
        Dimension frameSize = camera.getFrameSize();
        if (frameSize != null) {
            setSize(frameSize);
        }

        long duration, startTime;
        BufferedImage im = null;
        isRunning = true;
        while (isRunning) {
            startTime = System.currentTimeMillis();
            im = camera.getImage();
            if (im == null) {
                System.out.println("Problem  " + (imageCount + 1));
                duration = System.currentTimeMillis() - startTime;
            } else {
                if (((System.currentTimeMillis() - detectStartTime) > DETECT_DELAY)
                        && (numTasks.get() < MAX_TASKS)) {
                    trackFace(im);
                }

                image = im;
                imageCount++;
                duration = System.currentTimeMillis() - startTime;
                totalTime += duration;
                repaint();
            }

            if (duration < DELAY) {
                try {
                    Thread.sleep(DELAY - duration);
                } catch (Exception ex) {
                }
            }
        }
        camera.close();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (image != null) {
            g2.drawImage(image, 0, 0, this);
        }
        drawRect(g2);
        writeName(g2);
        writeRecognizing(g2);
    }

    private void drawRect(Graphics2D g2) {
        synchronized (faceRect) {
            if (faceRect.width == 0) {
                return;
            }
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(faceRect.x, faceRect.y, faceRect.width, faceRect.height);
        }
    }

    private void writeName(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.setFont(msgFont);
        if (faceID != null) {
            g2.drawString("Recognized Face ID: " + faceID,
                    5, getHeight() -10);
        }
    }

    private void writeRecognizing(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.setFont(msgFont);
        if (recognizeFace) {
            g2.drawString("Recognizing...",
                    5, getHeight() - 10);
        }
    }

    public void closeDown() {
        isRunning = false;
        while (!camera.isClosed()) {
            try {
                Thread.sleep(DELAY);
            } catch (Exception ex) {
            }
        }
    }

    private void trackFace(final BufferedImage img) {
        final BufferedImage grayIm = ImageUtils.toScaledGray(img, 1.0 / IM_SCALE);
        numTasks.getAndIncrement();
        executor.execute(new Runnable() {
            public void run() {
                detectStartTime = System.currentTimeMillis();
                CvRect rect = findFace(grayIm);
                if (rect != null) {
                    setRectangle(rect);
                    if (recognizeFace) {
                        recogFace(img);
                        //recognizeFace = false;
                    }
                    if (saveFace) {
                        clipSaveFace(img);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException err) {
                        }
                        if ((System.currentTimeMillis() - saveStartTime) >= 10000) {
                            stopSaveFace();
                        }
                    }
                }
                numTasks.getAndDecrement();

            }
        });
    }

    public String recognitionTime() {
        detectStartTime = System.currentTimeMillis();
        long recognitionDuration = System.currentTimeMillis() - detectStartTime;
        return recognitionDuration + "ms";
    }

    private CvRect findFace(BufferedImage grayIm) {
        IplImage cvImg = IplImage.createFrom(grayIm);
        IplImage equImg = IplImage.create(cvImg.width(), cvImg.height(), IPL_DEPTH_8U, 1);
        cvEqualizeHist(cvImg, equImg);
        CvSeq faces = cvHaarDetectObjects(equImg, classifier, storage, 1.1, 1, CV_HAAR_DO_ROUGH_SEARCH | CV_HAAR_FIND_BIGGEST_OBJECT);

        int total = faces.total();
        if (total == 0) {
            return null;
        } else if (total > 1) {
            System.out.println("Multiple faces");
        }
        CvRect rect = new CvRect(cvGetSeqElem(faces, 0));
        cvClearMemStorage(storage);
        return rect;
    }

    private void setRectangle(CvRect r) {
        synchronized (faceRect) {
            int xNew = r.x() * IM_SCALE;
            int yNew = r.y() * IM_SCALE;
            int widthNew = r.width() * IM_SCALE;
            int heightNew = r.height() * IM_SCALE;
            faceRect.setRect(xNew, yNew, widthNew, heightNew);
        }
    }

    public void setRecog() {
        recogStartTime = System.currentTimeMillis();
        faceID = null;
        recognizeFace = true;
    }

    private void recogFace(BufferedImage img) {
        BufferedImage clipIm = null;
        synchronized (faceRect) {
            if (faceRect.width == 0) {
                System.out.println("No face selected");
                return;
            }
            clipIm = ImageUtils.clipToRectangle(img, faceRect.x, faceRect.y, faceRect.width, faceRect.height);
        }
        if (clipIm != null) {
            matchClip(clipIm);
        }
    }

    private void matchClip(BufferedImage clipIm) {

        BufferedImage faceIm = clipToFace(resizeImage(clipIm));
        MatchResult result = faceRecog.match(faceIm);
        if (result == null) {
            System.out.println("No match found");
        } else {
            System.out.println("Name: " + result.getMatchFileName() + "\tDistance: " + result.getMatchDistance());
            if (result.getMatchDistance() < minDist) {
                minDist = result.getMatchDistance();
                maxDistFaceName = result.getName();
            }
        }
        if ((System.currentTimeMillis() - recogStartTime) >= 5000) {
            if (maxDistFaceName != null) {
                faceID = maxDistFaceName;
                recognizeFace = false;
                attPanel.setRecogButtonState(true);
                minDist = Double.MAX_VALUE;
                maxDistFaceName = null;
                studentFound(faceID);
            } else {
                recognizeFace = false;
                attPanel.setRecogButtonState(true);
                minDist = Double.MAX_VALUE;
                maxDistFaceName = null;
                studentFound("-1");
            }
        }

    }

    private BufferedImage resizeImage(BufferedImage im) {
        double widthScale = FACE_WIDTH / ((double) im.getWidth());
        double heightScale = FACE_HEIGHT / ((double) im.getHeight());
        double scale = (widthScale > heightScale) ? widthScale : heightScale;
        return ImageUtils.toScaledGray(im, scale);
    }

    private BufferedImage clipToFace(BufferedImage im) {
        int xOffset = (im.getWidth() - FACE_WIDTH) / 2;
        int yOffset = (im.getHeight() - FACE_HEIGHT) / 2;
        return ImageUtils.clipToRectangle(im, xOffset, yOffset, FACE_WIDTH, FACE_HEIGHT);
    }
    private long saveStartTime;

    public void startSaveFace(String ID) {
        this.ID = ID;
        saveStartTime = System.currentTimeMillis();
        saveFace = true;

    }

    public void stopSaveFace() {
        saveFace = false;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        BuildEigenFaces eigen = new BuildEigenFaces();
        eigen.build();
        Toolkit.getDefaultToolkit().beep();
        setCursor(null);
        JOptionPane.showMessageDialog(null, "Students image saved successfully");
        newStudPanel.setStartButtonState(true);
    }

    private void clipSaveFace(BufferedImage img) {
        synchronized (faceRect) {

            BufferedImage clipIm = null;
            if (faceRect.width == 0) {
                System.out.println("No face selected");
                return;
            }
            try {
                clipIm = img.getSubimage(faceRect.x, faceRect.y, faceRect.width, faceRect.height);
            } catch (RasterFormatException e) {
                System.out.println("Could not clip the image");
            }
            if (clipIm != null) {
                saveClip(clipIm);
            }
        }
    }

    private void saveClip(BufferedImage clipIm) {

        BufferedImage grayIm = resizeImage(clipIm);
        BufferedImage faceIm = clipToFace(grayIm);
        saveImage(faceIm, FACE_DIR + "/" + ID + "_" + fileCount + ".png");
        fileCount++;
    }

    private void saveImage(BufferedImage im, String fnm) {
        try {
            ImageIO.write(im, "png", new File(fnm));
            System.out.println("Saved image to " + fnm);
        } catch (IOException e) {
            System.out.println("Could not save " + fnm);
        }
    }

    private void studentFound(String ID) {
        if (!ID.equals("-1")) {
            Student student = connection.getStudent(ID);
            int answer = JOptionPane.showConfirmDialog(this, "Your firstname: " + student.getFirstName() + "\n Your lastname: " + student.getLastName()+"\n Are they correct?");
            if (answer == JOptionPane.OK_OPTION) {
                connection.checkAtt(student, System.currentTimeMillis());
                JOptionPane.showMessageDialog(this, "You successfully checked as present");
            } else {
                JOptionPane.showMessageDialog(this, "Please try again");
            }
        }else{
           JOptionPane.showMessageDialog(this, "Didn't find any similar student to you\n Please try again"); 
        }
    }
}
