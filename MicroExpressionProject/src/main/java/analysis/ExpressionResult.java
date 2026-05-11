package analysis;

public class ExpressionResult {
    private String emotion;
    private double confidence;

    public ExpressionResult(String emotion, double confidence) {
        this.emotion = emotion;
        this.confidence = confidence;
    }

    public String getEmotion() {
        return emotion;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "Emotion: " + emotion + " | Confidence: " + Math.round(confidence * 100) + "%";
    }
}