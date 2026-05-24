package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.Collections;

public class UnreverseGame extends ChatGame {
    public UnreverseGame(String originalWord) {
        super(GameType.UNREVERSE, new StringBuilder(originalWord).reverse().toString(), Collections.singletonList(originalWord));
    }
}
