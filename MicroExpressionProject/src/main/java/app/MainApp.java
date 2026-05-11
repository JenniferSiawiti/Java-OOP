package app;

import analysis.ExpressionAnalyzer;
import analysis.ExpressionResult;
import camera.CameraService;
import detection.FaceDetector;
import report.ReportManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class MainApp extends Application {

    private ImageView cameraView;
    private Button startButton;
    private ProgressBar progressBar;
    private Label timerLabel;
    private Label faceStatusLabel;
    private Label resultLabel;

    private volatile boolean tracking = false;
    private CameraService cameraService;

    @Override
    public void start(Stage stage) {
        cameraView = new ImageView();
        cameraView.setFitWidth(560);
        cameraView.setFitHeight(360);
        cameraView.setPreserveRatio(true);

        startButton = new Button("START TRACKING");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(360);

        timerLabel = new Label("Timer: 10");
        faceStatusLabel = new Label("Face Status: Waiting");
        resultLabel = new Label("Press START to begin");

        VBox root = new VBox(
                15,
                cameraView,
                startButton,
                progressBar,
                timerLabel,
                faceStatusLabel,
                resultLabel
        );

        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 750, 650);

        stage.setTitle("Micro-Expression Tracking");
        stage.setScene(scene);
        stage.show();

        startButton.setOnAction(e -> startTracking());
    }

    private void startTracking() {
        startButton.setDisable(true);
        tracking = true;

        progressBar.setProgress(0);
        timerLabel.setText("Timer: 10");
        faceStatusLabel.setText("Face Status: Opening camera...");
        resultLabel.setText("Analyzing expression...");

        new Thread(() -> {
            cameraService = new CameraService();
            FaceDetector faceDetector = new FaceDetector();
            ExpressionAnalyzer analyzer = new ExpressionAnalyzer();
            analyzer.reset();

            if (!cameraService.startCamera()) {
                Platform.runLater(() -> {
                    faceStatusLabel.setText("Face Status: Camera Error");
                    resultLabel.setText("Camera cannot open. Close Chrome/Zoom/Meet and try again.");
                    startButton.setDisable(false);
                });
                return;
            }

            long startTime = System.currentTimeMillis();
            int frames = 0;

            while (tracking) {
                long elapsed = System.currentTimeMillis() - startTime;
                int remaining = 10 - (int) (elapsed / 1000);

                if (elapsed >= 10000) {
                    tracking = false;
                    break;
                }

                Mat frame = cameraService.captureFrame();

                if (frame == null || frame.empty()) {
                    Platform.runLater(() -> {
                        faceStatusLabel.setText("Face Status: No camera frame");
                        resultLabel.setText("Camera opened, but image is empty.");
                    });

                    sleep(30);
                    continue;
                }

                Core.flip(frame, frame, 1);

                Rect[] faces = faceDetector.detectFaces(frame);

                if (faces.length > 0) {
                    Rect face = faces[0];

                    Imgproc.rectangle(
                            frame,
                            new Point(face.x, face.y),
                            new Point(face.x + face.width, face.y + face.height),
                            new Scalar(0, 255, 0),
                            3
                    );

                    analyzer.processFrame(frame, face);
                    frames++;

                    ExpressionResult current = analyzer.analyzeFrame(frame, face);

                    Platform.runLater(() -> {
                        faceStatusLabel.setText("Face Status: Face Captured");
                        resultLabel.setText("Current Emotion: " + current);
                    });
                } else {
                    Platform.runLater(() -> {
                        faceStatusLabel.setText("Face Status: Camera On, Face Not Found");
                        resultLabel.setText("Move closer and make sure lighting is bright.");
                    });
                }

                javafx.scene.image.Image image = matToImage(frame);

                Platform.runLater(() -> {
                    cameraView.setImage(image);
                    timerLabel.setText("Timer: " + Math.max(remaining, 0));
                    progressBar.setProgress(elapsed / 10000.0);
                });

                sleep(30);
            }

            cameraService.stopCamera();

            final int finalFrames = frames;

            ExpressionResult finalResult = finalFrames == 0
                    ? new ExpressionResult("No Face Detected", 0)
                    : analyzer.calculateFinalResult();

            new ReportManager().saveReport(finalResult, finalFrames, analyzer);

            Platform.runLater(() -> {
                timerLabel.setText("Timer: 0");
                progressBar.setProgress(1);
                faceStatusLabel.setText("Face Status: Finished");
                resultLabel.setText(analyzer.getPercentageReport(finalResult, finalFrames));
                startButton.setDisable(false);
            });

        }).start();
    }

    private javafx.scene.image.Image matToImage(Mat frame) {
        Mat rgb = new Mat();
        Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_BGR2RGB);

        int width = rgb.width();
        int height = rgb.height();
        int channels = rgb.channels();

        byte[] source = new byte[width * height * channels];
        rgb.get(0, 0, source);

        BufferedImage image = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_3BYTE_BGR
        );

        byte[] target = ((DataBufferByte)
                image.getRaster()
                        .getDataBuffer())
                .getData();

        for (int i = 0; i < source.length; i += 3) {
            target[i] = source[i + 2];
            target[i + 1] = source[i + 1];
            target[i + 2] = source[i];
        }

        return SwingFXUtils.toFXImage(image, null);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void stop() {
        tracking = false;

        if (cameraService != null) {
            cameraService.stopCamera();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}