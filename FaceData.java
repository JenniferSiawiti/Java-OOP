public class FaceData {

    private int x;
    private int y;
    private int width;
    private int height;

    private double eyeMovement;
    private double mouthMovement;
    private double eyebrowPosition;

    // Constructornya yuh
    public FaceData(int x, int y, int width, int height,
                    double eyeMovement,
                    double mouthMovement,
                    double eyebrowPosition) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.eyeMovement = eyeMovement;
        this.mouthMovement = mouthMovement;
        this.eyebrowPosition = eyebrowPosition;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public void printData() {

        System.out.println("Face Coordinates:");
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);

        System.out.println("Eye Movement: " + eyeMovement);
        System.out.println("Mouth Movement: " + mouthMovement);
        System.out.println("Eyebrow Position: " + eyebrowPosition);
    }
}
