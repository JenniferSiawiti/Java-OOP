package app;

import analysis.EmotionResult;
import analysis.ExpressionAnalyzer;
import camera.CameraService;
import detection.FaceDetector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import report.ReportManager;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class MainApp extends Application {

    private Label resultLabel;
    private Label timerLabel;
    private Label faceStatusLabel;
    private ProgressBar progressBar;
    private Button startButton;
    private ImageView cameraView;

    @Override
    public void start(Stage stage) {
        cameraView = new ImageView();
        cameraView.setFitWidth(480);
        cameraView.setFitHeight(320);
        cameraView.setPreserveRatio(true);

        startButton = new Button("START TRACKING");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        timerLabel = new Label("Timer: 10");
        faceStatusLabel = new Label("Face Status: Waiting");
        resultLabel = new Label("Press START");

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(
                cameraView,
                startButton,
                progressBar,
                timerLabel,
                faceStatusLabel,
                resultLabel
        );

        startButton.setOnAction(e -> startTracking());

        Scene scene = new Scene(root, 700, 650);
        stage.setTitle("Micro-Expression Tracking");
        stage.setScene(scene);
        stage.show();
    }

    private void startTracking() {
        startButton.setDisable(true);
        resultLabel.setText("Analyzing...");
        timerLabel.setText("Timer: 10");
        faceStatusLabel.setText("Face Status: Checking...");
        progressBar.setProgress(0);

        new Thread(() -> {
            CameraService cameraService = new CameraService();
            FaceDetector detector = new FaceDetector();
            ExpressionAnalyzer analyzer = new ExpressionAnalyzer();
            ReportManager reportManager = new ReportManager();

            if (!cameraService.startCamera()) {
                Platform.runLater(() -> {
                    resultLabel.setText("Camera not detected or OpenCV failed.");
                    faceStatusLabel.setText("Face Status: Camera Error");
                    startButton.setDisable(false);
                });
                return;
            }

            long startTime = System.currentTimeMillis();
            int frames = 0;

            while ((System.currentTimeMillis() - startTime) < 10000) {
                Mat frame = cameraService.captureFrame();

                if (frame != null && !frame.empty()) {
                    Rect[] faces = detector.detectFaces(frame);

                    boolean faceDetected = faces.length > 0;

                    if (faceDetected) {
                        analyzer.processFrame(frame, faces[0]);
                        frames++;
                    }

                    for (Rect face : faces) {
                        Imgproc.rectangle(
                                frame,
                                new Point(face.x, face.y),
                                new Point(face.x + face.width, face.y + face.height),
                                new Scalar(255, 0, 0),
                                3
                        );
                    }

                    Mat displayFrame = frame.clone();

                    Platform.runLater(() -> {
                        cameraView.setImage(matToImage(displayFrame));
                        faceStatusLabel.setText(
                                faceDetected
                                        ? "Face Status: Detected (" + faces.length + ")"
                                        : "Face Status: Not Detected"
                        );
                    });
                }

                long elapsed = System.currentTimeMillis() - startTime;
                double progress = elapsed / 10000.0;
                int remaining = 10 - (int) (elapsed / 1000);

                Platform.runLater(() -> {
                    progressBar.setProgress(progress);
                    timerLabel.setText("Timer: " + Math.max(remaining, 0));
                });

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    break;
                }
            }

            cameraService.stopCamera();

            EmotionResult result;

            if (frames == 0) {
                result = new EmotionResult();
                result.happy = 0;
                result.sad = 0;
                result.angry = 0;
                result.surprised = 0;
                result.neutral = 0;
                result.dominantEmotion = "No Face Detected";
                result.confidence = 0;
            } else {
                result = analyzer.calculateFinalResult();
            }

            reportManager.saveReport(result, frames);

            Platform.runLater(() -> {
                progressBar.setProgress(1);
                timerLabel.setText("Timer: 0");
                resultLabel.setText(result.toString());
                startButton.setDisable(false);
            });

        }).start();
    }

    private javafx.scene.image.Image matToImage(Mat frame) {
        BufferedImage image = new BufferedImage(
                frame.width(),
                frame.height(),
                BufferedImage.TYPE_3BYTE_BGR
        );

        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        frame.get(0, 0, data);

        return SwingFXUtils.toFXImage(image, null);
    }

    public static void main(String[] args) {
        launch();
    }
}