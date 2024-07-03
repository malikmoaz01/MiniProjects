public class QuizController {
    private static QuizController instance = new QuizController();
    private Quiz quiz;

    private QuizController() {
        quiz = new Quiz();
    }

    public static QuizController getInstance() {
        return instance;
    }

    public String getCurrentQuestion() {
        return quiz.getQuestion(quiz.getCurrentQuestionIndex());
    }

    public String[] getCurrentAnswers() {
        return quiz.getAnswers(quiz.getCurrentQuestionIndex());
    }

    public void nextQuestion() {
        if (quiz.getCurrentQuestionIndex() < quiz.getTotalQuestions() - 1) {
            quiz.incrementCurrentQuestionIndex();
        }
    }

    public boolean checkAnswer(int answerIndex) {
        return quiz.checkAnswer(quiz.getCurrentQuestionIndex(), answerIndex);
    }

    public int getScore() {
        return quiz.getScore();
    }

    public int getTotalQuestions() {
        return quiz.getTotalQuestions();
    }
    public int getCurrentQuestionIndex() {
        return quiz.getCurrentQuestionIndex();
    }
    
}
