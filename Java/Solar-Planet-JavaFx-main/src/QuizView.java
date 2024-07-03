import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class QuizView {
    private QuizController controller;
    private VBox root; // Main layout container
    private Label questionLabel; // Displays the question
    private VBox answersBox; // Container for answer choices
    private Button submitButton; // Button to submit the answer

    public QuizView() {
        controller = QuizController.getInstance();
        createView();
    }

    private void createView() {
        root = new VBox(10);
        questionLabel = new Label();
        answersBox = new VBox(5);
        submitButton = new Button("Next Question");
        submitButton.setOnAction(e -> handleAnswerSubmission());

        root.getChildren().addAll(questionLabel, answersBox, submitButton);
        updateQuestionAndAnswers();
    }

    public Scene createScene() {
        return new Scene(root, 300, 250);
    }

    private void updateQuestionAndAnswers() {
        questionLabel.setText(controller.getCurrentQuestion());
        answersBox.getChildren().clear(); // Clear previous answers
        ToggleGroup answerGroup = new ToggleGroup();

        String[] answers = controller.getCurrentAnswers();
        for (String answer : answers) {
            RadioButton rb = new RadioButton(answer);
            rb.setToggleGroup(answerGroup);
            answersBox.getChildren().add(rb);
        }
    }
    private void handleAnswerSubmission() {
        // We need to find which RadioButton is selected from the answersBox.
        RadioButton selected = answersBox.getChildren().stream()
            .filter(node -> node instanceof RadioButton && ((RadioButton) node).isSelected())
            .map(node -> (RadioButton) node)  // Cast safely to RadioButton.
            .findFirst()
            .orElse(null);
    
        if (selected != null) {
            ToggleGroup group = selected.getToggleGroup();
            int index = ((VBox) selected.getParent()).getChildren().indexOf(selected);
            controller.checkAnswer(index); // Check the answer, increment score if correct
        }
    
        if (controller.getCurrentQuestionIndex() < controller.getTotalQuestions() - 1) {
            controller.nextQuestion(); // Move to next question
            updateQuestionAndAnswers(); // Update the display for the next question
        } else {
            displayResults(); // All questions answered, display results
        }
    }
    
    private void displayResults() {
        questionLabel.setText("Quiz completed! Your score: " + controller.getScore() + " out of " + controller.getTotalQuestions());
        answersBox.getChildren().clear(); // Clear the answers
        submitButton.setDisable(true); // Disable the submit button
    }
}
