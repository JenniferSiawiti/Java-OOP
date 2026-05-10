package detection;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FaceDetector {

    private CascadeClassifier faceCascade;

    public FaceDetector() {
        try {
            InputStream inputStream = getClass()
                    .getResourceAsStream("/haarcascade/haarcascade_frontalface_alt.xml");

            if (inputStream == null) {
                throw new RuntimeException("haarcascade_frontalface_alt.xml not found.");
            }

            File tempFile = File.createTempFile("haarcascade_frontalface_alt", ".xml");
            tempFile.deleteOnExit();

            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            faceCascade = new CascadeClassifier(tempFile.getAbsolutePath());

            System.out.println("Cascade loaded: " + !faceCascade.empty());

        } catch (Exception e) {
            System.out.println("Error loading Haar Cascade.");
            e.printStackTrace();
        }
    }

    public Rect[] detectFaces(Mat frame) {
        if (frame == null || frame.empty()) {
            return new Rect[0];
        }

        if (faceCascade == null || faceCascade.empty()) {
            System.out.println("Cascade classifier is not loaded.");
            return new Rect[0];
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);

        MatOfRect faces = new MatOfRect();

        faceCascade.detectMultiScale(
                gray,
                faces,
                1.03,
                2,
                0,
                new Size(20, 20),
                new Size()
        );

        return faces.toArray();
    }

    public boolean detectFace(Mat frame) {
        return detectFaces(frame).length > 0;
    }
}