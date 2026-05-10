package report;

import analysis.EmotionResult;

import java.io.FileWriter;
import java.io.IOException;

public class ReportManager {

    public void saveReport(
            EmotionResult result,
            int frames
    ) {

        try {

            FileWriter writer =
                    new FileWriter("report.txt");

            writer.write(result.toString());

            writer.write(
                    "\n\nFrames Processed: "
                            + frames
            );

            writer.close();

            System.out.println(
                    "Report saved."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error saving report."
            );
        }
    }
}