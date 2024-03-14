package adeo.leroymerlin.cdp.controller;

import adeo.leroymerlin.cdp.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(value = "/")
    public List<Event> findEvents() {
        return eventService.getEvents();
    }

    @GetMapping(value = "/search/{query}")
    public List<Event> findEvents(@PathVariable String query) {
        return eventService.getFilteredEvents(query);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEvent(@PathVariable Long id) throws NotFoundException {
        eventService.delete(id);
    }

    @PutMapping(value = "/{id}")
    public void updateEvent(@PathVariable Long id, @RequestBody Event event) throws NotFoundException {
        eventService.updateEvent(id, event);
    }
}
