package almostkoi.game.impl;

import almostkoi.game.ChatGame;
import almostkoi.game.GameType;
import java.util.*;

public class SolveGame extends ChatGame {
    public SolveGame() {
        this(generateEquation());
    }

    private SolveGame(EquationResult eq) {
        super(GameType.SOLVE, eq.question, Collections.singletonList(eq.answer));
    }

    private static class EquationResult {
        String question;
        String answer;

        EquationResult(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    private static EquationResult generateEquation() {
        Random rand = new Random();
        int op = rand.nextInt(4);
        if (op == 0) {
            int a = rand.nextInt(100) + 1;
            int b = rand.nextInt(100) + 1;
            return new EquationResult(a + " + " + b, String.valueOf(a + b));
        } else if (op == 1) {
            int a = rand.nextInt(100) + 51;
            int b = rand.nextInt(50) + 1;
            return new EquationResult(a + " - " + b, String.valueOf(a - b));
        } else if (op == 2) {
            int a = rand.nextInt(12) + 1;
            int b = rand.nextInt(12) + 1;
            return new EquationResult(a + " * " + b, String.valueOf(a * b));
        } else {
            int b = rand.nextInt(12) + 1;
            int ans = rand.nextInt(12) + 1;
            int a = b * ans;
            return new EquationResult(a + " / " + b, String.valueOf(ans));
        }
    }
}
