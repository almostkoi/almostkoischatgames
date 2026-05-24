package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.*;

public class FillInGame extends ChatGame {
    public FillInGame(String word) {
        super(GameType.FILL_IN, fill(word), Collections.singletonList(word));
    }

    private static String fill(String word) {
        if (word == null || word.length() <= 1) {
            return word;
        }
        char[] chars = word.toCharArray();
        List<Integer> letterIndices = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetterOrDigit(chars[i])) {
                letterIndices.add(i);
            }
        }
        if (letterIndices.isEmpty()) {
            return word;
        }
        int toReplace = Math.max(1, (int) (letterIndices.size() * 0.35));
        if (toReplace >= letterIndices.size()) {
            toReplace = letterIndices.size() - 1;
        }
        Random rand = new Random();
        Collections.shuffle(letterIndices);
        for (int i = 0; i < toReplace; i++) {
            chars[letterIndices.get(i)] = '_';
        }
        return new String(chars);
    }
}
