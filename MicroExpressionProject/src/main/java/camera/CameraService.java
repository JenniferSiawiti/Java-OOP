package camera;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CameraService {

    private VideoCapture camera;
    private String cameraInfo = "No camera opened";

    public CameraService() {
        OpenCV.loadLocally();
    }

    public boolean startCamera() {
        int[] indexes = {0, 1, 2};
        int[] backends = {Videoio.CAP_DSHOW, Videoio.CAP_MSMF, Videoio.CAP_ANY};

        for (int index : indexes) {
            for (int backend : backends) {
                camera = new VideoCapture(index, backend);

                if (camera.isOpened()) {
                    camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
                    camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
                    camera.set(Videoio.CAP_PROP_FPS, 30);

                    Mat test = new Mat();

                    for (int i = 0; i < 20; i++) {
                        camera.read(test);

                        if (!test.empty()) {
                            cameraInfo = "Camera index " + index + " opened";
                            return true;
                        }

                        sleep(100);
                    }

                    camera.release();
                }
            }
        }

        cameraInfo = "Camera failed to send image";
        return false;
    }

    public Mat captureFrame() {
        Mat frame = new Mat();

        if (camera != null && camera.isOpened()) {
            camera.read(frame);
        }

        return frame;
    }

    public String getCameraInfo() {
        return cameraInfo;
    }

    public void stopCamera() {
        if (camera != null) {
            camera.release();
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}