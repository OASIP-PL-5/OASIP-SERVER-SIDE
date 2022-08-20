package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.EditEventDTO;
import sit.int221.oasipservice.dtos.EventDTO;
import sit.int221.oasipservice.dtos.NewEventDTO;
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

// Get all-events
    @GetMapping("")
    public List<EventDTO> getAllEvent() {
//         repository.findAll(Sort.by
//                (Sort.Direction.DESC, "eventStartTime"));
//        return repository.findAllByOrderByDateAsc();
        return eventService.getAllEventByDTO();
    }

// Get event-by-bookingId
    @GetMapping("/{id}")
    public List<EventDTO> getSimpleEventDTO(@PathVariable Integer id) {
        return eventService.getSimpleEventById(id);
    }

//  filter-by-eventCategoryId
    @GetMapping("/getByEventCategories/{eventCategoryId}")
    public List<EventDTO> getByEventCategory(@PathVariable Integer eventCategoryId) {
        return eventService.getByEventCategory(eventCategoryId);
    }

    //filter-by-Upcoming
    @GetMapping("/getEventByUpcoming")
    public List<EventDTO> getEventsByUpcoming() {
        return eventService.getEventsByUpcoming();
    }

    //filter-by-Past
    @GetMapping("/getEventByPast")
    public List<EventDTO> getEventsByPast() {
        return eventService.getEventsByPast();
    }

    //    filter: specific date
    @GetMapping("/getEventsByEventStartTime/{eventStartTime}")
    public List<EventDTO> getEventsByEventStartTime(@PathVariable String eventStartTime){
        return eventService.getEventsByEventStartTime(eventStartTime);
    }

    //    create new event
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody NewEventDTO newEvent) {
        return eventService.save(newEvent);
    }


    //    update event
    @PutMapping("/{id}")
    public Event updateEvent(@Valid @RequestBody EditEventDTO updateEvent, @PathVariable Integer id) {
        Event storedEventDetails = repository.getById(id);
        storedEventDetails.setId(updateEvent.getId());
        storedEventDetails.setEventStartTime(updateEvent.getEventStartTime());
        storedEventDetails.setEventNotes(updateEvent.getEventNotes());
        return repository.saveAndFlush(storedEventDetails);
    }

    //   delete event
    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable Integer bookingId) {
        repository.findById(bookingId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, bookingId + " does not exist !"));
        repository.deleteById(bookingId);
    }


}
