import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.geometry.Pos;

public class Main extends Application {
    private Stage primaryStage;
    private VBox root;
    private CarouselView carouselView;
    private QuizView quizView;
    private Button toggleViewButton;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        carouselView = new CarouselView();
        new CarouselController(carouselView);
        quizView = new QuizView();

        toggleViewButton = new Button("Switch to Quiz");
        toggleViewButton.setOnAction(e -> switchView());
        applyButtonStyle(toggleViewButton);

        root = new VBox(20);
        root.getChildren().addAll(toggleViewButton, carouselView);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Interactive Solar System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void switchView() {
        if (primaryStage.getScene().getRoot().equals(root) && root.getChildren().contains(carouselView)) {
            root.getChildren().set(1, quizView.createScene().getRoot());
            toggleViewButton.setText("Switch to Carousel");
        } else {
            root.getChildren().set(1, carouselView);
            toggleViewButton.setText("Switch to Quiz");
        }
    }

    private void applyButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #78909C; -fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #546E7A; -fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #78909C; -fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
