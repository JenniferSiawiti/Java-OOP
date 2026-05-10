public class FaceData {
    private double eyeMovement;
    private double mouthMovement;
    private double eyebrowPosition;

    public FaceData(double eyeMovement, double mouthMovement, double eyebrowPosition) {   // its the constructor y'all!!
        this.eyeMovement = eyeMovement;
        this.mouthMovement = mouthMovement;
        this.eyebrowPosition = eyebrowPosition;
    }

    public double getEyeMovement() {
        return eyeMovement;
    }

    public double getMouthMovement() {
        return mouthMovement;
    }

    public double getEyebrowPosition() {
        return eyebrowPosition;
    }
} 
