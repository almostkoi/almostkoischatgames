package almostkoi.game;

import java.util.List;

public abstract class ChatGame {
    protected final GameType type;
    protected final String question;
    protected final List<String> answers;

    protected ChatGame(GameType type, String question, List<String> answers) {
        this.type = type;
        this.question = question;
        this.answers = answers;
    }

    public GameType getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public boolean isCorrect(String input, boolean caseSensitive) {
        if (type == GameType.TYPE_RANDOM) {
            caseSensitive = true;
        }
        for (String ans : answers) {
            if (caseSensitive) {
                if (ans.equals(input)) {
                    return true;
                }
            } else {
                if (ans.equalsIgnoreCase(input)) {
                    return true;
                }
            }
        }
        return false;
    }
}
