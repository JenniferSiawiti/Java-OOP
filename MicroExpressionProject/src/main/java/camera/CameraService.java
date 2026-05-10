package camera;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraService {

    private VideoCapture camera;

    public CameraService() {
        try {
            OpenCV.loadLocally();
            camera = new VideoCapture(0);
        } catch (Exception e) {
            System.out.println("Failed to load OpenCV: " + e.getMessage());
        }
    }

    public boolean startCamera() {
        return camera != null && camera.isOpened();
    }

    public Mat captureFrame() {
        Mat frame = new Mat();

        if (camera != null && camera.isOpened()) {
            camera.read(frame);
        }

        return frame;
    }

    public void stopCamera() {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }
}