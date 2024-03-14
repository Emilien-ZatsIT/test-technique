package adeo.leroymerlin.cdp.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import adeo.leroymerlin.cdp.entity.Band;
import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.entity.Member;
import adeo.leroymerlin.cdp.exception.NotFoundException;
import adeo.leroymerlin.cdp.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
public class EventControllerTest {

    @MockBean
    EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    private final String baseUri = "/api/events";

    @Test
    public void deleteEvent_Success() throws Exception {
        mockMvc.perform(delete(baseUri+"/0"))
                .andExpect(status().isOk());
        verify(eventService, times(1)).delete(0L);
    }

    @Test
    public void deleteEvent_Fail() throws Exception {
        doThrow(new NotFoundException("The event with id 0 doesn't exist - Delete failed"))
                .when(eventService).delete(0L);

        mockMvc.perform(delete(baseUri+"/0"))
                .andExpect(status().isNotFound());
        verify(eventService, times(1)).delete(0L);
    }

    @Test
    public void updateEvent_Success() throws Exception {
        Event event = Event.builder().id(0L).build();

        when(eventService.updateEvent(eq(0L), any(Event.class)))
                .thenReturn(event);

        mockMvc.perform(put(baseUri+"/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(event)))
                .andExpect(status().isOk());

        verify(eventService, times(1)).updateEvent(eq(0L), any(Event.class));
    }

    @Test
    public void updateEvent_Fail() throws Exception {
        Event event = Event.builder().id(0L).build();

        when(eventService.updateEvent(eq(0L), any()))
                .thenThrow(new NotFoundException("The event with id 0 doesn't exist - Update failed"));

        mockMvc.perform(put(baseUri+"/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(event)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchMember_Success() throws Exception {
        Member member = Member.builder()
                .id(1L)
                .name("Alain Connu")
                .build();

        Set<Member> members = new HashSet<Member>(){{add(member);}};

        Band band = Band.builder()
                .id(1L)
                .name("Led Zepplin [1]")
                .members(members)
                .build();

        Set<Band> bands = new HashSet<Band>(){{add(band);}};

        Event event = Event.builder()
                .id(1L)
                .title("Concert [2]")
                .imgUrl(null)
                .nbStars(5)
                .bands(bands)
                .comment("Perfect")
                .build();

        when(eventService.getFilteredEvents(eq("al")))
                .thenReturn(Collections.singletonList(event));

        mockMvc.perform(get(baseUri+"/search/al"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Concert [2]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].bands[0].name").value("Led Zepplin [1]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].bands[0].members[0].name").value("Alain Connu"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].bands[0].members[1]").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].bands[1]").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").doesNotExist());
        verify(eventService, times(1)).getFilteredEvents(eq("al"));
    }

    @Test
    public void searchMember_Fail() throws Exception {

        when(eventService.getFilteredEvents(eq("al")))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get(baseUri+"/search/al"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").doesNotExist());
        verify(eventService, times(1)).getFilteredEvents(eq("al"));
    }
}
