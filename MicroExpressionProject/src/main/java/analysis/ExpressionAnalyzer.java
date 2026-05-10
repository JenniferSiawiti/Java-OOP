package analysis;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class ExpressionAnalyzer {

    private int happyTotal = 0;
    private int sadTotal = 0;
    private int angryTotal = 0;
    private int surprisedTotal = 0;
    private int neutralTotal = 0;

    private int frameCount = 0;
    private Mat previousGray = null;

    public void processFrame(Mat frame, Rect face) {
        if (frame == null || frame.empty() || face == null) return;

        Mat faceROI = new Mat(frame, face);

        Mat gray = new Mat();
        Imgproc.cvtColor(faceROI, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(gray, gray, new Size(120, 120));
        Imgproc.equalizeHist(gray, gray);

        Mat upperFace = gray.submat(0, 45, 0, 120);
        Mat eyeArea = gray.submat(30, 60, 0, 120);
        Mat mouthArea = gray.submat(75, 120, 0, 120);

        double upperContrast = getContrast(upperFace);
        double eyeContrast = getContrast(eyeArea);
        double mouthContrast = getContrast(mouthArea);

        double mouthBrightness = Core.mean(mouthArea).val[0];
        double eyeBrightness = Core.mean(eyeArea).val[0];

        double motion = 0;
        if (previousGray != null) {
            Mat diff = new Mat();
            Core.absdiff(gray, previousGray, diff);
            motion = Core.mean(diff).val[0];
        }

        previousGray = gray.clone();

        int happy = 20;
        int sad = 20;
        int angry = 20;
        int surprised = 20;
        int neutral = 20;

        // Smile approximation: mouth area changes/brightens
        if (mouthContrast > 52 && mouthBrightness > 95) {
            happy += 18;
        }

        // Sad approximation: low movement + darker mouth/eye area
        if (motion < 4 && mouthBrightness < 105 && eyeBrightness < 120) {
            sad += 20;
        }

        // Angry approximation: strong eye/upper-face contrast
        if (upperContrast > 58 && eyeContrast > 52) {
            angry += 18;
        }

        // Surprised approximation: high movement or large face change
        if (motion > 8) {
            surprised += 22;
        }

        // Neutral only if nothing strongly changes
        if (motion < 3 && mouthContrast < 50 && upperContrast < 55) {
            neutral += 12;
        } else {
            neutral -= 8;
        }

        if (neutral < 5) neutral = 5;

        int total = happy + sad + angry + surprised + neutral;

        happyTotal += (happy * 100) / total;
        sadTotal += (sad * 100) / total;
        angryTotal += (angry * 100) / total;
        surprisedTotal += (surprised * 100) / total;
        neutralTotal += (neutral * 100) / total;

        frameCount++;
    }

    private double getContrast(Mat mat) {
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(mat, mean, std);
        return std.toArray()[0];
    }

    public EmotionResult calculateFinalResult() {
        EmotionResult result = new EmotionResult();

        if (frameCount == 0) {
            result.happy = 0;
            result.sad = 0;
            result.angry = 0;
            result.surprised = 0;
            result.neutral = 0;
            result.dominantEmotion = "No Face Detected";
            result.confidence = 0;
            return result;
        }

        result.happy = happyTotal / frameCount;
        result.sad = sadTotal / frameCount;
        result.angry = angryTotal / frameCount;
        result.surprised = surprisedTotal / frameCount;
        result.neutral = neutralTotal / frameCount;

        result.confidence = result.happy;
        result.dominantEmotion = "Happy";

        if (result.sad > result.confidence) {
            result.confidence = result.sad;
            result.dominantEmotion = "Sad";
        }

        if (result.angry > result.confidence) {
            result.confidence = result.angry;
            result.dominantEmotion = "Angry";
        }

        if (result.surprised > result.confidence) {
            result.confidence = result.surprised;
            result.dominantEmotion = "Surprised";
        }

        if (result.neutral > result.confidence + 5) {
            result.confidence = result.neutral;
            result.dominantEmotion = "Neutral";
        }

        return result;
    }
}