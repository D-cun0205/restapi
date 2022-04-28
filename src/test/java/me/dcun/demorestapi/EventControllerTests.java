package me.dcun.demorestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.dcun.common.RestDocsConfiguration;
import me.dcun.demorestapi.events.Event;
import me.dcun.demorestapi.events.EventDto;
import me.dcun.demorestapi.events.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists());
    }
}