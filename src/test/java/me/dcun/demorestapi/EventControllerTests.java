package me.dcun.demorestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.dcun.common.BaseControllerTest;
import me.dcun.demorestapi.events.Event;
import me.dcun.demorestapi.events.EventDto;
import me.dcun.demorestapi.events.EventRepository;
import me.dcun.demorestapi.events.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventRepository eventRepository;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 3, 27, 11, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 3, 28, 11, 30))
                .beginEventDateTime(LocalDateTime.of(2022, 4, 27, 11, 30))
                .endEventDateTime(LocalDateTime.of(2022, 4, 28, 11, 30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.toString()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-event").description("link to update event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName("Content-Type").description("des content type"),
                                headerWithName("Accept").description("des accept")
                        ),
                        requestFields(
                                fieldWithPath("name").description("des name"),
                                fieldWithPath("description").description("des description"),
                                fieldWithPath("beginEnrollmentDateTime").description("des beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("des closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("des beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("des endEventDateTime"),
                                fieldWithPath("basePrice").description("des basePrice"),
                                fieldWithPath("maxPrice").description("des maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("des limitOfEnrollment"),
                                fieldWithPath("location").description("des location")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("des location"),
                                headerWithName("Content-Type").description("des content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("des id"),
                                fieldWithPath("name").description("des name"),
                                fieldWithPath("description").description("des description"),
                                fieldWithPath("beginEnrollmentDateTime").description("des beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("des closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("des beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("des endEventDateTime"),
                                fieldWithPath("basePrice").description("des basePrice"),
                                fieldWithPath("maxPrice").description("des maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("des limitOfEnrollment"),
                                fieldWithPath("location").description("des location"),
                                fieldWithPath("free").description("des free"),
                                fieldWithPath("offline").description("des offline"),
                                fieldWithPath("eventStatus").description("des eventStatus"),
                                fieldWithPath("_links.query-events.href").description("des _links.query-events"),
                                fieldWithPath("_links.self.href").description("des _links.self"),
                                fieldWithPath("_links.update-event.href").description("des _links.update-event"),
                                fieldWithPath("_links.profile.href").description("des _links.profile")
                        )
                ));
    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러 이벤트를 생성하는 테스트")
    public void createEvent_bad_request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 4, 27, 11, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 4, 28, 11, 30))
                .beginEventDateTime(LocalDateTime.of(2022, 4, 27, 11, 30))
                .endEventDateTime(LocalDateTime.of(2022, 4, 28, 11, 30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_bad_request_valid() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_bad_request_wrong_valid() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 4, 28, 11, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 4, 27, 11, 30))
                .beginEventDateTime(LocalDateTime.of(2022, 4, 25, 11, 30))
                .endEventDateTime(LocalDateTime.of(2022, 4, 24, 11, 30))
                .basePrice(20000)
                .maxPrice(100)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));
    }

    @Test
    @DisplayName("1개 조회")
    public void getEvent() throws Exception {
        Event event = generateEvent(100);

        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"));
    }

    @Test
    @DisplayName("잘못된 ID로 조회시 not found 응답")
    public void notFount() throws Exception {
        this.mockMvc.perform(get("/api/events/12345"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 데이터 수정")
    public void updateEvent() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @DisplayName("Event id 가 없는 데이터를 호출한 경우")
    public void updateEvent404() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}", 12345)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수정하려는 데이터가 없는 데이터인 경우")
    public void updateEvent400Empty() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수정하려는 데이터 정상적이지 않은 데이터인 경우")
    public void updateEvent400Wrong() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(100);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 3, 27, 11, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 3, 28, 11, 30))
                .beginEventDateTime(LocalDateTime.of(2022, 4, 27, 11, 30))
                .endEventDateTime(LocalDateTime.of(2022, 4, 28, 11, 30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        this.eventRepository.save(event);
        return event;
    }
}