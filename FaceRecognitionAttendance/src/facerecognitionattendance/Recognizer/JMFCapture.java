package facerecognitionattendance.Recognizer;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.util.*;

public class JMFCapture implements ControllerListener {

    private static final String CAP_DEVICE = "vfw:Microsoft WDM Image Capture (Win32):0";
    private static final String CAP_LOCATOR = "vfw://0";
    private static final int MAX_TRIES = 7;
    private static final int TRY_PERIOD = 1000;
    private VideoFormat largestVf = null;
    private Dimension frameSize = null;

    private Player p = null;
    private FrameGrabbingControl fg;
    private BufferToImage bufferToImage = null;
    private boolean closedDevice;
    private Object waitSync = new Object();
    private boolean stateTransitionOK = true;

    public JMFCapture() {
        closedDevice = true;
        try {
            MediaLocator ml = findMedia(CAP_DEVICE);
            p = Manager.createRealizedPlayer(ml);
        } catch (Exception e) {
            System.exit(0);
        }

        setToLargestVideoFrame(p);
        p.addControllerListener(this);
        fg = (FrameGrabbingControl) p.getControl(
                "javax.media.control.FrameGrabbingControl");
        if (fg == null) {
            System.exit(0);
        }
        p.start();
        if (!waitForStart()) {
            System.exit(0);
        }

        waitForBufferToImage();
    }

    private MediaLocator findMedia(String requireDeviceName) {
        Vector devices = CaptureDeviceManager.getDeviceList(null);
        if (devices == null) {
            System.exit(0);
        }
        if (devices.size() == 0) {
            System.exit(0);
        }

        CaptureDeviceInfo devInfo = null;
        int idx;
        for (idx = 0; idx < devices.size(); idx++) {
            devInfo = (CaptureDeviceInfo) devices.elementAt(idx);
            String devName = devInfo.getName();
            if (devName.equals(requireDeviceName)) {
                break;
            }
        }

        MediaLocator ml = null;
        if (idx == devices.size()) {
            ml = new MediaLocator(CAP_LOCATOR);
        } else {  
            System.out.println("Found device: " + requireDeviceName);
            storeLargestVf(devInfo);
            ml = devInfo.getLocator();
        }
        return ml;
    }

    private void storeLargestVf(CaptureDeviceInfo devInfo) {
        Format[] forms = devInfo.getFormats();

        largestVf = null;
        double maxSize = -1;
        for (int i = 0; i < forms.length; i++) {
            if (forms[i] instanceof VideoFormat) {
                VideoFormat vf = (VideoFormat) forms[i];
                Dimension dim = vf.getSize();
                double size = dim.getWidth() * dim.getHeight();
                if (size > maxSize) {
                    largestVf = vf;
                    maxSize = size;
                }
            }
        }
    }

    public Dimension getFrameSize() {
        if (p == null) {
            return null;
        }

        FormatControl formatControl = (FormatControl) p.getControl("javax.media.control.FormatControl");
        if (formatControl == null) {
            return null;
        }

        VideoFormat vf = (VideoFormat) formatControl.getFormat();
        if (vf == null) {
            return null;
        }

        return vf.getSize();
    }

    private void setToLargestVideoFrame(Player player) {
        FormatControl formatControl
                = (FormatControl) player.getControl("javax.media.control.FormatControl");
        if (formatControl == null) {
            return;
        }

        Format format = formatControl.setFormat(largestVf);
        if (format == null) {
            return;
        }
    }

    private boolean waitForStart() {
        synchronized (waitSync) {
            try {
                while (p.getState() != Controller.Started && stateTransitionOK) {
                    waitSync.wait();
                }
            } catch (Exception e) {
            }
        }
        return stateTransitionOK;
    }

    public void controllerUpdate(ControllerEvent evt) {
        if (evt instanceof StartEvent) {
            synchronized (waitSync) {
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
        } else if (evt instanceof ResourceUnavailableEvent) {
            synchronized (waitSync) {
                stateTransitionOK = false;
                waitSync.notifyAll();
            }
        }
    }

    private void waitForBufferToImage() {

        int tryCount = MAX_TRIES;
        while (tryCount > 0) {
            if (hasBufferToImage()) {
                break;
            }
            try {
                Thread.sleep(TRY_PERIOD);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            tryCount--;
        }

        if (tryCount == 0) {
            System.exit(0);
        }

        closedDevice = false;
    }

    private boolean hasBufferToImage() {
        Buffer buf = fg.grabFrame();
        if (buf == null) {
            return false;
        }
        VideoFormat vf = (VideoFormat) buf.getFormat();
        if (vf == null) {
            return false;
        }
        bufferToImage = new BufferToImage(vf);
        return true;
    }

    public int getFrameRate() {
        return 30;
    }

    synchronized public BufferedImage getImage() {
        if (closedDevice) {
            return null;
        }
        Buffer buf = fg.grabFrame();
        if (buf == null) {
            System.out.println("No grabbed buffer");
            return null;
        }
        Image im = bufferToImage.createImage(buf);
        if (im == null) {
            System.out.println("No grabbed image");
            return null;
        }

        return (BufferedImage) im;
    }

    synchronized public void close() {
        p.close();
        closedDevice = true;
    }

    public boolean isClosed() {
        return closedDevice;
    }

}
