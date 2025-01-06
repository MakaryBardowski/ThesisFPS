package events;

public abstract class EventSubscriber {
    public abstract void receiveEventNotification(GameEvent gameEvent);
}
