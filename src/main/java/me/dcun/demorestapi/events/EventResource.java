package me.dcun.demorestapi.events;

import org.springframework.hateoas.EntityModel;

public class EventResource extends EntityModel<Event> {
    public EventResource(Event event) {
        super(event);
    }
}
