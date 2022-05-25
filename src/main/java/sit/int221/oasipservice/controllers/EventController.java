package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.EditEventDTO;
import sit.int221.oasipservice.dtos.NewEventDTO;
import sit.int221.oasipservice.dtos.SimpleEventDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.repositories.EventRepository;
import sit.int221.oasipservice.services.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;
    private final EventRepository repository;

    public EventController(EventRepository repository) {
        this.repository = repository;
    }


    @GetMapping("")
    public List<Event> getAllEvent() {
        return repository.findAll(Sort.by
                (Sort.Direction.DESC, "eventStartTime"));
//        return repository.findAllByOrderByDateAsc();
    }


    @GetMapping("/{id}")
    public SimpleEventDTO getSimpleEventDTO(@PathVariable Integer id) {
        return eventService.getSimpleEventById(id);
    }

    @GetMapping("/getByEventCategories/{eventCategoryId}")
    public List<Event> getByEventCategory(@PathVariable Integer eventCategoryId) {
        return eventService.getByEventCategory(eventCategoryId);
    }

//Upcoming
    @GetMapping("/getEventByUpcoming")
    public List<Event> getEventsByUpcoming(){
        return repository.getEventsByUpcoming();
    }
//Past
    @GetMapping("/getEventByPast")
    public List<Event> getEventsByPast(){
        return repository.getEventsByPast();
}

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody NewEventDTO newEvent) {
        return eventService.save(newEvent);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@Valid @RequestBody EditEventDTO updateEvent, @PathVariable Integer id) {
        Event storedEventDetails = repository.getById(id);
        storedEventDetails.setId(updateEvent.getId());
        storedEventDetails.setEventStartTime(updateEvent.getEventStartTime());
        storedEventDetails.setEventNotes(updateEvent.getEventNotes());
        return repository.saveAndFlush(storedEventDetails);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable Integer bookingId) {
        repository.findById(bookingId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, bookingId + " does not exist !"));
        repository.deleteById(bookingId);
    }


}
