package analysis;

import java.util.Random;

public class ExpressionAnalyzer {

    private int happyTotal = 0;
    private int sadTotal = 0;
    private int angryTotal = 0;
    private int surprisedTotal = 0;
    private int neutralTotal = 0;

    private int frameCount = 0;

    private Random random = new Random();

    public void processFrame() {

        int h = random.nextInt(100);
        int s = random.nextInt(100);
        int a = random.nextInt(100);
        int su = random.nextInt(100);
        int n = random.nextInt(100);

        int total = h + s + a + su + n;

        happyTotal += (h * 100) / total;
        sadTotal += (s * 100) / total;
        angryTotal += (a * 100) / total;
        surprisedTotal += (su * 100) / total;
        neutralTotal += (n * 100) / total;

        frameCount++;
    }

    public EmotionResult calculateFinalResult() {

        EmotionResult result =
                new EmotionResult();

        result.happy =
                happyTotal / frameCount;

        result.sad =
                sadTotal / frameCount;

        result.angry =
                angryTotal / frameCount;

        result.surprised =
                surprisedTotal / frameCount;

        result.neutral =
                neutralTotal / frameCount;

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

        if (result.neutral > result.confidence) {
            result.confidence = result.neutral;
            result.dominantEmotion = "Neutral";
        }

        return result;
    }
}