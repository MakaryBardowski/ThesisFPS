package events;

public interface EventSubscriber {
    void receiveEventNotification(GameEvent gameEvent);
}
