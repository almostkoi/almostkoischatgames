package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.Collections;

public class TypeGame extends ChatGame {
    public TypeGame(String word) {
        super(GameType.TYPE, word, Collections.singletonList(word));
    }
}
