package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.EditEventCateDTO;
import sit.int221.oasipservice.dtos.SimpleEventCategoriesDTO;
import sit.int221.oasipservice.entities.EventCategory;
import sit.int221.oasipservice.repositories.EventCategoryRepository;
import sit.int221.oasipservice.services.EventCategoryService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {

    private final EventCategoryRepository repository;

    public EventCategoryController(EventCategoryRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private EventCategoryService eventCategoryService;

    //assign role จาก string ดิบๆ เก็บเป็น object-String เลย เพื่อความสะดวกในการเช็ค roles
    private final String admin = "admin";
    private final String lecturer = "lecturer";
    private final String student = "student";

    @GetMapping("")
    public List<EventCategory> getAllEventCategory() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @GetMapping("/{id}")
    public SimpleEventCategoriesDTO getSimpleEventCategoriesDto(@PathVariable Integer id, HttpServletRequest request) {
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user");
            return eventCategoryService.getSimpleEventCategoryById(id);
        }else{

        return eventCategoryService.getSimpleEventCategoryById(id);
        }
    }

    //วิธีบ้านๆ
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public EventCategory create(@RequestBody EventCategory newEventCategory) {
        return repository.saveAndFlush(newEventCategory);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    public EventCategory updateEventCategory(@Valid @RequestBody EditEventCateDTO updateEventCategory,
                                             @PathVariable Integer id, HttpServletRequest request) {
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"guest cannot put event-cat");
        }
        else{
        EventCategory storedEventCategoryDetails = repository.getById(id);
        storedEventCategoryDetails.setEventCategoryName(updateEventCategory.getEventCategoryName());
        storedEventCategoryDetails.setEventDuration(updateEventCategory.getEventDuration());
        storedEventCategoryDetails.setEventCategoryDescription(updateEventCategory.getEventCategoryDescription());
            return repository.saveAndFlush(storedEventCategoryDetails);
        }
    }



}

