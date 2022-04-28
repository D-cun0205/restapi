package me.dcun.demorestapi;

import me.dcun.demorestapi.events.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    void builder() {
        Event event = Event.builder()
                .name("REST API")
                .description("REST API")
                .build();

        assertThat(event).isNotNull();
    }

    @Test
    void javaBean() {
        //Given
        String name = "Event";
        String description = "Spring";

        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @ParameterizedTest
    //    @CsvSource({
    //            "0, 0, true",
    //            "100, 0, false",
    //            "0, 100, false"
    //    })
    @MethodSource
    void testFree(int basePrice, int maxPrice, boolean isFree) {
        //Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    static Object[] testFree() {
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false}
        };
    }

    @Test
    void testOffline() {
        //Given
        Event event = Event.builder()
                .location("강남역")
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isTrue();

        //Given
        event = Event.builder().build();

        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isFalse();
    }
}
