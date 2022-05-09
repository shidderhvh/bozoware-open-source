package bozoware.base.event;

@FunctionalInterface
public interface EventConsumer<Event> {
    void call(Event paramEvent);
}
