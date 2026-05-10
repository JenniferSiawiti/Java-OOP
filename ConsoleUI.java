package ui;

public class ConsoleUI {

    public void showTracking(int frame) {

        System.out.println("Tracking face... Frame: " + frame);
    }

    public void showFinalResult(
            int happy,
            int sad,
            int angry,
            int surprised,
            int neutral,
            String dominantEmotion,
            int confidence,
            int frameCount
    ) {

        System.out.println();
        System.out.println("=================================");
        System.out.println("MICRO-EXPRESSION ANALYSIS RESULT");
        System.out.println("=================================");
        System.out.println();

        System.out.println("Happy      : " + happy + "%");
        System.out.println("Sad        : " + sad + "%");
        System.out.println("Angry      : " + angry + "%");
        System.out.println("Surprised  : " + surprised + "%");
        System.out.println("Neutral    : " + neutral + "%");

        System.out.println();

        System.out.println("Dominant Emotion : " + dominantEmotion);
        System.out.println("Confidence       : " + confidence + "%");

        System.out.println();

        System.out.println("Tracking Duration : 10 seconds");
        System.out.println("Frames Processed  : " + frameCount);
    }
}