public class ExpressionAnalyzer {
    public ExpressionResult analyze(FaceData data) {
        double eye = data.getEyeMovement();
        double mouth = data.getMouthMovement();
        double brow = data.getEyebrowPosition();

        if (eye > 0.85 && mouth > 0.5) { // Surprised
            double confidence = round((eye + mouth) / 2);
            return new ExpressionResult("Surprised", confidence);
        }

        else if (brow > 0.8) { // Angry
            double confidence = round(brow);
            return new ExpressionResult("Angry", confidence);
        }

        else if (mouth > 0.7 && eye > 0.6) { // Happy
            double confidence = round((mouth + eye) / 2);
            return new ExpressionResult("Happy", confidence);
        }

        else if (eye < 0.3 && mouth < 0.3 && brow < 0.3) { // Neutral
            return new ExpressionResult("Neutral", 0.5);
        }

        else { // Sad
            double confidence = round(1 - mouth);
            return new ExpressionResult("Sad", confidence);
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
