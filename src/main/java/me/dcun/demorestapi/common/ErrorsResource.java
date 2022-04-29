package me.dcun.demorestapi.common;

import me.dcun.demorestapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public ErrorsResource(Errors errors) {
        super(errors);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
