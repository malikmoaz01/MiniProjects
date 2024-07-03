public class Quiz {
    private String[] questions = {
        "What is the largest planet in our solar system?",
        "Which planet is known as the Red Planet?",
        "What planet is famous for its rings?",
        "Which planet is closest to the Sun?",
        "Which planet is known as the Earth's twin?",
        "What is the smallest planet in our solar system?",
        "Which planet has a storm called the Great Red Spot?",
        "Which planet is known for having the most moons?",
        "Which planet is known as the ice giant?",
        "Which planet orbits the sun the fastest?"
    };

    private String[][] answers = {
        {"Earth", "Jupiter", "Mars", "Venus"},
        {"Mercury", "Venus", "Mars", "Jupiter"},
        {"Jupiter", "Saturn", "Uranus", "Neptune"},
        {"Mercury", "Venus", "Earth", "Mars"},
        {"Mars", "Jupiter", "Venus", "Saturn"},
        {"Mercury", "Mars", "Venus", "Earth"},
        {"Jupiter", "Mars", "Saturn", "Neptune"},
        {"Mars", "Jupiter", "Saturn", "Uranus"},
        {"Jupiter", "Saturn", "Uranus", "Neptune"},
        {"Mercury", "Venus", "Mars", "Jupiter"}
    };

    private int[] correctAnswers = {1, 2, 1, 0, 2, 0, 0, 1, 2, 0};
    private int currentQuestionIndex = 0;
    private int score = 0;

    public String getQuestion(int index) {
        return questions[index];
    }

    public String[] getAnswers(int index) {
        return answers[index];
    }

    public boolean checkAnswer(int questionIndex, int answerIndex) {
        boolean isCorrect = (answerIndex == correctAnswers[questionIndex]);
        if (isCorrect) {
            score++;
        }
        return isCorrect;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void incrementCurrentQuestionIndex() {
        if (currentQuestionIndex < getTotalQuestions() - 1) {
            currentQuestionIndex++;
        }
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return questions.length;
    }
    
}
