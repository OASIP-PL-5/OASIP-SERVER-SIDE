package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.oasipservice.dtos.EditEventCateDTO;
import sit.int221.oasipservice.dtos.SimpleEventCategoriesDTO;
import sit.int221.oasipservice.entities.EventCategory;
import sit.int221.oasipservice.repositories.EventCategoryRepository;
import sit.int221.oasipservice.services.EventCategoryService;

import javax.validation.Valid;
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
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
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

    @PutMapping("/{id}")
    public EventCategory updateEventCategory(@Valid @RequestBody EditEventCateDTO updateEventCategory,
                                             @PathVariable Integer id) {
        EventCategory storedEventCategoryDetails = repository.getById(id);
        storedEventCategoryDetails.setEventCategoryName(updateEventCategory.getEventCategoryName());
        storedEventCategoryDetails.setEventDuration(updateEventCategory.getEventDuration());
        storedEventCategoryDetails.setEventCategoryDescription(updateEventCategory.getEventCategoryDescription());
        return repository.saveAndFlush(storedEventCategoryDetails);
    }
}

