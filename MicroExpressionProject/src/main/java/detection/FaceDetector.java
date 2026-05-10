package detection;

import org.opencv.core.Mat;

public class FaceDetector {

    public boolean detectFace(Mat frame) {

        return !frame.empty();
    }
}