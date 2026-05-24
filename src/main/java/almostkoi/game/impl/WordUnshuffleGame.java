package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.*;

public class WordUnshuffleGame extends ChatGame {
    public WordUnshuffleGame(String originalPhrase) {
        super(GameType.WORD_UNSHUFFLE, shuffleWords(originalPhrase), Collections.singletonList(originalPhrase));
    }

    private static String shuffleWords(String phrase) {
        if (phrase == null || !phrase.contains(" ")) {
            return phrase;
        }
        String[] split = phrase.split(" ");
        List<String> list = new ArrayList<>(Arrays.asList(split));
        int attempts = 0;
        String shuffled = "";
        while (attempts < 10) {
            Collections.shuffle(list);
            shuffled = String.join(" ", list);
            if (!shuffled.equalsIgnoreCase(phrase)) {
                break;
            }
            attempts++;
        }
        return shuffled;
    }
}
