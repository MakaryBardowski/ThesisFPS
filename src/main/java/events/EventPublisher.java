package events;

import java.util.ArrayList;

public abstract class EventPublisher {
    private final ArrayList<EventSubscriber> subscribers = new ArrayList(1);
    
    public final void addEventSubscriber(EventSubscriber eventSubscriber){
        subscribers.add(eventSubscriber);
    }
    
    public void removeEventSubscriber(EventSubscriber eventSubscriber){
        subscribers.remove(eventSubscriber);
    }
    
    public void notifyEventSubscribers(GameEvent gameEvent){
        for(var subscriber : subscribers){
            subscriber.receiveEventNotification(gameEvent);
        }
    }
    
}
