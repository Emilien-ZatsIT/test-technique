package adeo.leroymerlin.cdp.service;

import adeo.leroymerlin.cdp.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.repository.EventRepository;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAllBy();
    }

    public void delete(Long id) throws NotFoundException {
        if(eventRepository.exists(id)){
            eventRepository.delete(id);
        } else {
            throw new NotFoundException(String.format("The event with id %d doesn't exist - Delete failed", id));
        }
    }

    public List<Event> getFilteredEvents(String query) {
        List<Event> events = eventRepository.findAllBy();
        events.removeIf(event -> {
            event.getBands().removeIf(band -> {
                    band.getMembers().removeIf(member ->
                            !member.getName().toLowerCase().contains(query.toLowerCase())
                    );
                    if(band.getMembers().isEmpty()) {
                        return true;
                    }
                    if(!band.getName().contains(String.format("[%d]", band.getMembers().size()))) {
                        band.setName(String.format("%s [%d]", band.getName(), band.getMembers().size()));
                    }
                    return false;
            });
            if(event.getBands().isEmpty()) {
                return true;
            }
            int nbChildrenBands = event.getBands().stream().mapToInt(band -> band.getMembers().size()).sum();
            event.setTitle(String.format("%s [%d]", event.getTitle(), event.getBands().size() + nbChildrenBands));
            return false;
        });
        return events;
    }

    public Event updateEvent(Long id, Event updatedEvent) throws NotFoundException {
        if(eventRepository.exists(id)){
            Event dbEvent = eventRepository.findOne(id);
            mapUpdate(updatedEvent, dbEvent);
            return eventRepository.save(dbEvent);
        } else {
            throw new NotFoundException(String.format("The event with id %d doesn't exist - Update failed", id));
        }
    }

    private void mapUpdate(Event newEvent, Event oldEvent) {
        if(newEvent.getNbStars() == null) {
            oldEvent.setNbStars(null);
        } else if(!newEvent.getNbStars().equals(oldEvent.getNbStars())){
            oldEvent.setNbStars(newEvent.getNbStars());
        }

        if(newEvent.getComment() == null || newEvent.getComment().isEmpty()) {
            oldEvent.setComment(null);
        } else if(!newEvent.getComment().equals(oldEvent.getComment())){
            oldEvent.setComment(newEvent.getComment());
        }
    }

}
