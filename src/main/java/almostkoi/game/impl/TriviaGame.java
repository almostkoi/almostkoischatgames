package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.List;

public class TriviaGame extends ChatGame {
    public TriviaGame(String question, List<String> answers) {
        super(GameType.TRIVIA, question, answers);
    }
}
