package adeo.leroymerlin.cdp.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import adeo.leroymerlin.cdp.entity.Band;
import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.entity.Member;
import adeo.leroymerlin.cdp.repository.EventRepository;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @InjectMocks
    EventService eventService;
    
    @Mock
    EventRepository eventRepository;

    private Event testEvent;

    @Before
    public void before() {
        Member member = Member.builder()
            .id(1L)
            .name("Alain Connu")
            .build();
        
        Set<Member> members = new HashSet<Member>(){{add(member);}};

        Band band = Band.builder()
            .id(1L)
            .name("Led Zepplin")
            .members(members)
            .build();
        
        Set<Band> bands = new HashSet<Band>(){{add(band);}};

        testEvent = Event.builder()
            .id(1L)
            .title("Concert")
            .imgUrl(null)
            .nbStars(5)
            .bands(bands)
            .comment("Perfect")
            .build();
    }

    @Test
    public void updateEvent_Success() throws Exception {
        int newStars = 0;
        String newComment = "Nul !";

        Event updatedEvent = testEvent;
        updatedEvent.setNbStars(newStars);
        updatedEvent.setComment(newComment);
        
        when(eventRepository.exists(testEvent.getId())).thenReturn(true);
        when(eventRepository.findOne(testEvent.getId())).thenReturn(testEvent);
        when(eventRepository.save(updatedEvent)).thenReturn(updatedEvent);

        Event dbEvent = eventService.updateEvent(1L, updatedEvent);

        assertNotNull(dbEvent);
        assertEquals(1L, (long) dbEvent.getId());
        assertEquals("Concert", dbEvent.getTitle());
        assertEquals(0, (int) dbEvent.getNbStars());
        assertEquals("Nul !", dbEvent.getComment());
    }

    @Test
    public void updateEvent_CommentOnly() throws Exception {
        String newComment = "Nul !";

        Event updatedEvent = testEvent;
        updatedEvent.setComment(newComment);

        when(eventRepository.exists(testEvent.getId())).thenReturn(true);
        when(eventRepository.findOne(testEvent.getId())).thenReturn(testEvent);
        when(eventRepository.save(updatedEvent)).thenReturn(updatedEvent);

        Event dbEvent = eventService.updateEvent(1L, updatedEvent);

        assertNotNull(dbEvent);
        assertEquals(1L, (long) dbEvent.getId());
        assertEquals("Concert", dbEvent.getTitle());
        assertEquals(5, (int) dbEvent.getNbStars());
        assertEquals("Nul !", dbEvent.getComment());
    }

    @Test
    public void updateEvent_NbStarsOnly() throws Exception {
        int newStars = 0;

        Event updatedEvent = testEvent;
        updatedEvent.setNbStars(newStars);

        when(eventRepository.exists(testEvent.getId())).thenReturn(true);
        when(eventRepository.findOne(testEvent.getId())).thenReturn(testEvent);
        when(eventRepository.save(updatedEvent)).thenReturn(updatedEvent);

        Event dbEvent = eventService.updateEvent(1L, updatedEvent);

        assertNotNull(dbEvent);
        assertEquals(1L, (long) dbEvent.getId());
        assertEquals("Concert", dbEvent.getTitle());
        assertEquals(0, (int) dbEvent.getNbStars());
        assertEquals("Perfect", dbEvent.getComment());
    }


    @Test
    public void updateEvent_FailEventNotExist() {
        int newStars = 0;
        String newComment = "Nul !";

        Event updatedEvent = testEvent;
        updatedEvent.setNbStars(newStars);
        updatedEvent.setComment(newComment);
        
        when(eventRepository.exists(testEvent.getId())).thenReturn(false);

        assertThrows("The event with id 1L doesn't exist - Update failed", Exception.class,
                () -> eventService.updateEvent(1L, updatedEvent));
    }

    @Test
    public void searchMembers_OneMember(){
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findAllBy()).thenReturn(events);

        List<Event> results = eventService.getFilteredEvents("Alain");

        assertNotNull(results);
        assertTrue(results.stream().anyMatch(resultEvent ->
                "Concert [2]".equals(resultEvent.getTitle())
                        && resultEvent.getBands().stream().anyMatch(resultBand ->
                        "Led Zepplin [1]".equals(resultBand.getName())
                                && resultBand.getMembers().stream().allMatch(resultMember ->
                                resultMember.getName().toLowerCase().contains("Alain".toLowerCase())))
        ));
    }

    @Test
    public void searchMembers_NoMember(){
        List<Event> events = new ArrayList<>(Arrays.asList(testEvent));
        when(eventRepository.findAllBy()).thenReturn(events);

        List<Event> results = eventService.getFilteredEvents("Sarah");

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void searchMembers_MultipleMembers(){
        Member member1 = Member.builder()
                .id(2L)
                .name("Alain Fini")
                .build();
        Member member2 = Member.builder()
                .id(3L)
                .name("Marie Nière")
                .build();

        Set<Member> members = new HashSet<Member>(){{add(member1); add(member2);}};

        Band band = Band.builder()
                .id(2L)
                .name("Rolling Stones")
                .members(members)
                .build();

        Set<Band> bands = new HashSet<Band>(){{add(band);}};

        Event event = Event.builder()
                .id(2L)
                .title("Carnaval")
                .imgUrl(null)
                .nbStars(null)
                .bands(bands)
                .comment(null)
                .build();

        List<Event> events = Arrays.asList(testEvent, event);
        when(eventRepository.findAllBy()).thenReturn(events);

        List<Event> results = eventService.getFilteredEvents("Alain");

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(resultEvent ->
                "Carnaval [2]".equals(resultEvent.getTitle())
                        && resultEvent.getBands().stream().allMatch(resultBand ->
                        "Rolling Stones [1]".equals(resultBand.getName())
                                && resultBand.getMembers().stream().allMatch(resultMember ->
                                resultMember.getName().toLowerCase().contains("Alain".toLowerCase())))
        ));
        assertTrue(results.stream().anyMatch(resultEvent ->
                "Concert [2]".equals(resultEvent.getTitle())
                && resultEvent.getBands().stream().allMatch(resultBand ->
                        "Led Zepplin [1]".equals(resultBand.getName())
                        && resultBand.getMembers().stream().allMatch(resultMember ->
                                resultMember.getName().toLowerCase().contains("Alain".toLowerCase())))
        ));
    }
}
