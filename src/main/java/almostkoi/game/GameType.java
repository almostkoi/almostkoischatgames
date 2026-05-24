package almostkoi.game;

public enum GameType {
    UNSCRAMBLE,
    SOLVE,
    TYPE,
    TYPE_RANDOM,
    TRIVIA,
    FILL_IN,
    WORD_UNSHUFFLE,
    REVERSE_WORD_UNSHUFFLE,
    UNREVERSE;

    public String getDisplayName() {
        switch (this) {
            case UNSCRAMBLE: return "Unscramble";
            case SOLVE: return "Solve";
            case TYPE: return "Type";
            case TYPE_RANDOM: return "Type Random";
            case TRIVIA: return "Trivia";
            case FILL_IN: return "Fill In";
            case WORD_UNSHUFFLE: return "Word Unshuffle";
            case REVERSE_WORD_UNSHUFFLE: return "Reverse Word Unshuffle";
            case UNREVERSE: return "Unreverse";
            default: return name();
        }
    }
}
