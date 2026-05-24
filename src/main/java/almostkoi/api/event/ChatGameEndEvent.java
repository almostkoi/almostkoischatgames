package almostkoi.api.event;

import almostkoi.game.GameType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChatGameEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameType type;
    private final String answer;
    private final Player winner;
    private final boolean hasWinner;

    public ChatGameEndEvent(GameType type, String answer, Player winner, boolean hasWinner) {
        super(true);
        this.type = type;
        this.answer = answer;
        this.winner = winner;
        this.hasWinner = hasWinner;
    }

    public GameType getType() {
        return type;
    }

    public String getAnswer() {
        return answer;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean hasWinner() {
        return hasWinner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
