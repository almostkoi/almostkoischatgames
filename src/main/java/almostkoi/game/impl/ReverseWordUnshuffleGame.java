package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.*;

public class ReverseWordUnshuffleGame extends ChatGame {
    public ReverseWordUnshuffleGame(String originalPhrase) {
        super(GameType.REVERSE_WORD_UNSHUFFLE, reverseAndShuffle(originalPhrase), Collections.singletonList(originalPhrase));
    }

    private static String reverseAndShuffle(String phrase) {
        if (phrase == null) {
            return phrase;
        }
        String[] split = phrase.split(" ");
        List<String> list = new ArrayList<>();
        for (String s : split) {
            list.add(new StringBuilder(s).reverse().toString());
        }
        if (list.size() > 1) {
            int attempts = 0;
            List<String> shuffledList = new ArrayList<>(list);
            while (attempts < 10) {
                Collections.shuffle(shuffledList);
                boolean identical = true;
                for (int i = 0; i < list.size(); i++) {
                    if (!shuffledList.get(i).equals(list.get(i))) {
                        identical = false;
                        break;
                    }
                }
                if (!identical) {
                    break;
                }
                attempts++;
            }
            return String.join(" ", shuffledList);
        }
        return String.join(" ", list);
    }
}
