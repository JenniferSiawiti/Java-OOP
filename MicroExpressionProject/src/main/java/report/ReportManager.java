package report;

import analysis.ExpressionAnalyzer;
import analysis.ExpressionResult;

import java.io.FileWriter;
import java.io.IOException;

public class ReportManager {

    public void saveReport(ExpressionResult result, int frameCount, ExpressionAnalyzer analyzer) {
        try {
            FileWriter writer = new FileWriter("emotion_report.txt");

            int total = analyzer.getHappy()
                    + analyzer.getSad()
                    + analyzer.getAngry()
                    + analyzer.getSurprised()
                    + analyzer.getNeutral();

            writer.write("MICRO-EXPRESSION REPORT\n");
            writer.write("=======================\n\n");

            writer.write("Dominant Emotion: " + result.getEmotion() + "\n");
            writer.write("Confidence: " + Math.round(result.getConfidence() * 100) + "%\n");
            writer.write("Frames Processed: " + frameCount + "\n\n");

            if (total > 0) {
                writer.write("Happy: " + percent(analyzer.getHappy(), total) + "%\n");
                writer.write("Sad: " + percent(analyzer.getSad(), total) + "%\n");
                writer.write("Angry: " + percent(analyzer.getAngry(), total) + "%\n");
                writer.write("Surprised: " + percent(analyzer.getSurprised(), total) + "%\n");
                writer.write("Neutral: " + percent(analyzer.getNeutral(), total) + "%\n");
            } else {
                writer.write("No face detected.\n");
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int percent(int value, int total) {
        return (int) Math.round((value * 100.0) / total);
    }
}