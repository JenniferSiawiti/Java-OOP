package detection;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

public class FaceDetector {

    private CascadeClassifier faceCascade;

    public FaceDetector() {
        faceCascade = new CascadeClassifier();

        String[] paths = {
                "src/main/resources/haarcascade_frontalface_alt.xml",
                "src/main/resources/haarcascade_frontalface_default.xml",
                "src/main/resources/haarcascade/haarcascade_frontalface_alt.xml",
                "src/main/resources/haarcascade/haarcascade_frontalface_default.xml"
        };

        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                faceCascade.load(file.getAbsolutePath());
                System.out.println("Loaded Haar from: " + file.getAbsolutePath());
                break;
            }
        }

        System.out.println("Cascade loaded: " + !faceCascade.empty());
    }

    public Rect[] detectFaces(Mat frame) {
        if (frame == null || frame.empty()) {
            return new Rect[0];
        }

        if (!faceCascade.empty()) {
            Mat gray = new Mat();
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(gray, gray);

            MatOfRect faces = new MatOfRect();

            faceCascade.detectMultiScale(
                    gray,
                    faces,
                    1.05,
                    3,
                    0,
                    new Size(50, 50),
                    new Size()
            );

            Rect[] detected = faces.toArray();

            if (detected.length > 0) {
                return detected;
            }
        }

        return detectSkinFace(frame);
    }

    private Rect[] detectSkinFace(Mat frame) {
        Mat ycrcb = new Mat();
        Imgproc.cvtColor(frame, ycrcb, Imgproc.COLOR_BGR2YCrCb);

        Mat mask = new Mat();

        Core.inRange(
                ycrcb,
                new Scalar(0, 133, 77),
                new Scalar(255, 173, 127),
                mask
        );

        Imgproc.erode(mask, mask, new Mat());
        Imgproc.dilate(mask, mask, new Mat());

        java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(
                mask,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        Rect bestRect = null;
        double bestArea = 0;

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = rect.width * rect.height;

            boolean reasonableSize = area > 4000;
            boolean faceShape = rect.height > rect.width * 0.7;

            if (reasonableSize && faceShape && area > bestArea) {
                bestArea = area;
                bestRect = rect;
            }
        }

        if (bestRect != null) {
            int newX = bestRect.x + (int) (bestRect.width * 0.15);
            int newY = bestRect.y + (int) (bestRect.height * 0.05);
            int newW = (int) (bestRect.width * 0.70);
            int newH = (int) (bestRect.height * 0.65);

            Rect tighterFace = new Rect(
                    Math.max(newX, 0),
                    Math.max(newY, 0),
                    Math.min(newW, frame.width() - newX),
                    Math.min(newH, frame.height() - newY)
            );

            return new Rect[]{tighterFace};
        }

        return new Rect[0];
    }
}