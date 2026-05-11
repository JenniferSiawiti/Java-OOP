package analysis;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class ExpressionAnalyzer {

    private double happy = 0;
    private double sad = 0;
    private double angry = 0;
    private double surprised = 0;
    private double neutral = 0;

    public void reset() {
        happy = 0;
        sad = 0;
        angry = 0;
        surprised = 0;
        neutral = 0;
    }

    public void processFrame(Mat frame, Rect face) {
        ExpressionResult result = analyzeFrame(frame, face);
    }

    public ExpressionResult analyzeFrame(Mat frame, Rect face) {
        if (frame == null || frame.empty() || face == null) {
            return new ExpressionResult("No Face Detected", 0);
        }

        Mat faceROI = new Mat(frame, face);

        Mat gray = new Mat();
        Imgproc.cvtColor(faceROI, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(gray, gray, new Size(120, 120));
        Imgproc.equalizeHist(gray, gray);

        Mat eyes = gray.submat(25, 58, 15, 105);
        Mat eyebrows = gray.submat(10, 35, 15, 105);
        Mat mouth = gray.submat(72, 115, 25, 95);

        double eyeContrast = contrast(eyes);
        double eyebrowContrast = contrast(eyebrows);
        double mouthContrast = contrast(mouth);

        double eyeDarkness = 255 - Core.mean(eyes).val[0];
        double mouthDarkness = 255 - Core.mean(mouth).val[0];

        double happyScore = 20;
        double sadScore = 20;
        double angryScore = 20;
        double surprisedScore = 20;
        double neutralScore = 20;

        if (mouthContrast > 52 && mouthDarkness > 80 && mouthDarkness < 120) {
            happyScore += 18;
        }

        if (mouthDarkness < 85 && eyeDarkness > 85) {
            sadScore += 18;
        }

        if (eyebrowContrast > 55 || eyeContrast > 58) {
            angryScore += 18;
        }

        if (mouthDarkness > 120 || mouthContrast > 65) {
            surprisedScore += 18;
        }

        if (mouthContrast < 50 && eyeContrast < 55 && eyebrowContrast < 55) {
            neutralScore += 18;
        }

        happy += happyScore;
        sad += sadScore;
        angry += angryScore;
        surprised += surprisedScore;
        neutral += neutralScore;

        return getCurrentResult(happyScore, sadScore, angryScore, surprisedScore, neutralScore);
    }

    private ExpressionResult getCurrentResult(
            double happyScore,
            double sadScore,
            double angryScore,
            double surprisedScore,
            double neutralScore
    ) {
        double max = happyScore;
        String emotion = "Happy";

        if (sadScore > max) {
            max = sadScore;
            emotion = "Sad";
        }

        if (angryScore > max) {
            max = angryScore;
            emotion = "Angry";
        }

        if (surprisedScore > max) {
            max = surprisedScore;
            emotion = "Surprised";
        }

        if (neutralScore > max) {
            max = neutralScore;
            emotion = "Neutral";
        }

        double total = happyScore + sadScore + angryScore + surprisedScore + neutralScore;
        return new ExpressionResult(emotion, max / total);
    }

    private double contrast(Mat mat) {
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(mat, mean, std);
        return std.toArray()[0];
    }

    public ExpressionResult calculateFinalResult() {
        double total = happy + sad + angry + surprised + neutral;

        if (total == 0) {
            return new ExpressionResult("No Face Detected", 0);
        }

        double max = happy;
        String emotion = "Happy";

        if (sad > max) {
            max = sad;
            emotion = "Sad";
        }

        if (angry > max) {
            max = angry;
            emotion = "Angry";
        }

        if (surprised > max) {
            max = surprised;
            emotion = "Surprised";
        }

        if (neutral > max) {
            max = neutral;
            emotion = "Neutral";
        }

        return new ExpressionResult(emotion, max / total);
    }

    public String getPercentageReport(ExpressionResult finalResult, int frames) {
        double total = happy + sad + angry + surprised + neutral;

        if (total == 0) {
            return "Final Result: No Face Detected\nFrames: " + frames;
        }

        return String.format(
                "Final Result: %s\nHappy: %d%% | Sad: %d%% | Angry: %d%% | Surprised: %d%% | Neutral: %d%%",
                finalResult.getEmotion(),
                percent(happy, total),
                percent(sad, total),
                percent(angry, total),
                percent(surprised, total),
                percent(neutral, total)
        );
    }

    private int percent(double value, double total) {
        return (int) Math.round((value * 100.0) / total);
    }

    public int getHappy() { return (int) happy; }
    public int getSad() { return (int) sad; }
    public int getAngry() { return (int) angry; }
    public int getSurprised() { return (int) surprised; }
    public int getNeutral() { return (int) neutral; }
}