package report;

import java.io.FileWriter;
import java.io.IOException;

public class ReportManager {

    public void saveReport(
            int happy,
            int sad,
            int angry,
            int surprised,
            int neutral,
            String dominantEmotion,
            int confidence,
            int frameCount
    ) {

        try {

            FileWriter writer = new FileWriter("emotion_report.txt");

            writer.write("=================================\n");
            writer.write("MICRO-EXPRESSION REPORT\n");
            writer.write("=================================\n\n");

            writer.write("Happy      : " + happy + "%\n");
            writer.write("Sad        : " + sad + "%\n");
            writer.write("Angry      : " + angry + "%\n");
            writer.write("Surprised  : " + surprised + "%\n");
            writer.write("Neutral    : " + neutral + "%\n\n");

            writer.write("Dominant Emotion : " + dominantEmotion + "\n");
            writer.write("Confidence       : " + confidence + "%\n");
            writer.write("Frames Processed : " + frameCount + "\n");

            writer.close();

            System.out.println();
            System.out.println("Report saved successfully.");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}