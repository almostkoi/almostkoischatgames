package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.*;

public class UnscrambleGame extends ChatGame {
    public UnscrambleGame(String originalWord) {
        super(GameType.UNSCRAMBLE, shuffle(originalWord), Collections.singletonList(originalWord));
    }

    private static String shuffle(String word) {
        if (word == null || word.length() <= 1) {
            return word;
        }
        List<Character> chars = new ArrayList<>();
        for (char c : word.toCharArray()) {
            chars.add(c);
        }
        int attempts = 0;
        String shuffled = "";
        while (attempts < 10) {
            Collections.shuffle(chars);
            StringBuilder sb = new StringBuilder();
            for (char c : chars) {
                sb.append(c);
            }
            shuffled = sb.toString();
            if (!shuffled.equalsIgnoreCase(word)) {
                break;
            }
            attempts++;
        }
        return shuffled;
    }
}
