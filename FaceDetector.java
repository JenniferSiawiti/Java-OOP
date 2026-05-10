import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector {

    private CascadeClassifier faceCascade;

    // Constructornya yah gengz
    public FaceDetector() {

        faceCascade =
                new CascadeClassifier(
                        "haarcascade_frontalface_default.xml"
                );
    }

   
    public FaceData detectFace(Mat frame) {

        MatOfRect faces = new MatOfRect();

        
        faceCascade.detectMultiScale(frame, faces);

     
        if (faces.toArray().length == 0) {

            System.out.println("No face detected!");

            return null;
        }

        
        Rect rect = faces.toArray()[0];

        Imgproc.rectangle(
                frame,
                new Point(rect.x, rect.y),
                new Point(rect.x + rect.width,
                          rect.y + rect.height),
                new Scalar(0, 255, 0),
                2
        );

        System.out.println("Face detected!");

        double eyeMovement = 0.0;
        double mouthMovement = 0.0;
        double eyebrowPosition = 0.0;

        return new FaceData(
                rect.x,
                rect.y,
                rect.width,
                rect.height,
                eyeMovement,
                mouthMovement,
                eyebrowPosition
        );
    }
}
