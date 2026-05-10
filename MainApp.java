package app;

import analysis.EmotionResult;
import analysis.ExpressionAnalyzer;
import camera.CameraService;
import detection.FaceDetector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import report.ReportManager;

public class MainApp extends Application {

    private Label resultLabel;
    private Label timerLabel;
    private ProgressBar progressBar;
    private Button startButton;

    @Override
    public void start(Stage stage) {

        startButton = new Button("START TRACKING");
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        timerLabel = new Label("Timer: 10");
        resultLabel = new Label("Press START");

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER); // ✅ center everything
        root.getChildren().addAll(
                startButton,
                progressBar,
                timerLabel,
                resultLabel
        );

        startButton.setOnAction(e -> startTracking());

        Scene scene = new Scene(root, 600, 420);

        stage.setTitle("Micro-Expression Tracking");
        stage.setScene(scene);
        stage.show();
    }

    private void startTracking() {

        startButton.setDisable(true);
        resultLabel.setText("Analyzing...");
        timerLabel.setText("Timer: 10");
        progressBar.setProgress(0);

        new Thread(() -> {

            CameraService cameraService = new CameraService();
            FaceDetector detector = new FaceDetector();
            ExpressionAnalyzer analyzer = new ExpressionAnalyzer();
            ReportManager reportManager = new ReportManager();

            if (!cameraService.startCamera()) {
                Platform.runLater(() -> {
                    resultLabel.setText("Camera not detected or OpenCV failed.");
                    startButton.setDisable(false);
                });
                return;
            }

            long start = System.currentTimeMillis();
            int frames = 0;

            while ((System.currentTimeMillis() - start) < 10000) {

                var frame = cameraService.captureFrame();

                if (detector.detectFace(frame)) {
                    analyzer.processFrame();
                    frames++;
                }

                long elapsed = System.currentTimeMillis() - start;
                double progress = elapsed / 10000.0;
                int remaining = 10 - (int) (elapsed / 1000);

                Platform.runLater(() -> {
                    progressBar.setProgress(progress);
                    timerLabel.setText("Timer: " + Math.max(remaining, 0));
                });
            }

            cameraService.stopCamera();

            EmotionResult result = analyzer.calculateFinalResult();
            reportManager.saveReport(result, frames);

            Platform.runLater(() -> {
                progressBar.setProgress(1);
                timerLabel.setText("Timer: 0");
                resultLabel.setText(result.toString());
                startButton.setDisable(false);
            });

        }).start();
    }

    public static void main(String[] args) {
        launch();
    }
}
