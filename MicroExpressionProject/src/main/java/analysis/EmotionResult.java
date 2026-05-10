package analysis;

public class EmotionResult {

    public int happy;
    public int sad;
    public int angry;
    public int surprised;
    public int neutral;

    public String dominantEmotion;
    public int confidence;

    @Override
    public String toString() {

        return
                "\nHappy      : " + happy + "%" +
                        "\nSad        : " + sad + "%" +
                        "\nAngry      : " + angry + "%" +
                        "\nSurprised  : " + surprised + "%" +
                        "\nNeutral    : " + neutral + "%" +

                        "\n\nDominant Emotion : "
                        + dominantEmotion +

                        "\nConfidence       : "
                        + confidence + "%";
    }
}