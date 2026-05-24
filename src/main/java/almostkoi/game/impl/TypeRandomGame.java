package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.Collections;
import java.util.Random;

public class TypeRandomGame extends ChatGame {
    public TypeRandomGame() {
        super(GameType.TYPE_RANDOM, generateRandomString(), Collections.singletonList(generateRandomString()));
    }

    private static String currentGeneratedString = "";

    private static String generateRandomString() {
        if (!currentGeneratedString.isEmpty()) {
            String temp = currentGeneratedString;
            currentGeneratedString = "";
            return temp;
        }
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        int length = rand.nextInt(4) + 5;
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        currentGeneratedString = sb.toString();
        return currentGeneratedString;
    }
}
