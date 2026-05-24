package almostkoi.api.event;

import almostkoi.game.GameType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.List;

public class ChatGameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameType type;
    private final List<String> answers;
    private final String content;

    public ChatGameStartEvent(GameType type, List<String> answers, String content) {
        super(true);
        this.type = type;
        this.answers = answers;
        this.content = content;
    }

    public GameType getType() {
        return type;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public String getContent() {
        return content;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
