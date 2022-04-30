package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.oasipservice.dtos.SimpleEventCategoriesDTO;
import sit.int221.oasipservice.entities.EventCategory;
import sit.int221.oasipservice.repositories.EventCategoryRepository;
import sit.int221.oasipservice.services.EventCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/event-category")
public class EventCategoryController {

    private final EventCategoryRepository repository;

    public EventCategoryController(EventCategoryRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private EventCategoryService eventCategoryService;

    @GetMapping("")
    public List<EventCategory> getAllEventCategory() {
        return repository.findAll();
    }


    @GetMapping("/{id}")
    public SimpleEventCategoriesDTO getSimpleEventCategoriesDto(@PathVariable Integer id) {
        return eventCategoryService.getSimpleEventCategoryById(id);
    }

    //วิธีบ้านๆ
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public EventCategory create(@RequestBody EventCategory newEventCategory) {
        return repository.saveAndFlush(newEventCategory);
    }
}

