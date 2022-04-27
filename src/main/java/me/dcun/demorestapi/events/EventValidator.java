package me.dcun.demorestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong");
        }

        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();

        if (beginEventDateTime.isAfter(endEventDateTime) ||
            beginEventDateTime.isBefore(beginEnrollmentDateTime) ||
            beginEventDateTime.isBefore(closeEnrollmentDateTime)) {
            errors.rejectValue("beginEventDateTime", "wrongBeginEventDateTime",
                    "BeginEventDateTime is wrong");
        }

        if (endEventDateTime.isBefore(beginEventDateTime) ||
                endEventDateTime.isBefore(beginEnrollmentDateTime) ||
                endEventDateTime.isBefore(closeEnrollmentDateTime)) {
            errors.rejectValue("endEventDateTime", "wrongEndEventDateTime",
                    "EndEventDateTime is wrong");
        }

        if (beginEnrollmentDateTime.isAfter(closeEnrollmentDateTime) ||
                beginEnrollmentDateTime.isAfter(beginEventDateTime) ||
                beginEnrollmentDateTime.isAfter(endEventDateTime)) {
            errors.rejectValue("beginEnrollmentDateTime", "wrongBeginEnrollmentDateTime",
                    "BeginEnrollmentDateTime is wrong");
        }

        if (closeEnrollmentDateTime.isBefore(beginEnrollmentDateTime) ||
                closeEnrollmentDateTime.isAfter(beginEventDateTime) ||
                closeEnrollmentDateTime.isAfter(endEventDateTime)) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongCloseEnrollmentDateTime",
                    "CloseEnrollmentDateTime is wrong");
        }
    }
}
