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
}
