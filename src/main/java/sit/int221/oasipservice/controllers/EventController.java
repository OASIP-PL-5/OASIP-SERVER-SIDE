package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.EditEventDTO;
import sit.int221.oasipservice.dtos.EventDTO;
import sit.int221.oasipservice.dtos.NewEventDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.EventRepository;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.services.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;
    private final EventRepository repository;
    private UserRepository userRepository;


    public EventController(EventRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }


    // Get all-events
    @GetMapping("")
    public List<EventDTO> getAllEvent() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if (role.contains("admin")) {
            return eventService.getAllEventByDTO();
        } else {
            return eventService.getAllUserByEmail();
        }
    }

    // Get event-by-bookingId
    @GetMapping("/{id}")
    public List<EventDTO> getSimpleEventDTO(@PathVariable Integer id) {
        //get email from token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        //get role from token
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        User userEmail = userRepository.findByEmail(email);
        Event storedEventDetails = repository.getById(id);
        if (storedEventDetails.getBookingEmail().equals(email) || role.contains("admin")) {
            return eventService.getSimpleEventById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

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
    public List<EventDTO> getEventsByEventStartTime(@PathVariable String eventStartTime) {
        return eventService.getEventsByEventStartTime(eventStartTime);
    }

    //    create new event
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody NewEventDTO newEvent) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String newEventEmail = newEvent.getBookingEmail();

        if (newEvent.getBookingEmail().equals(email) || role.contains("admin")) {
            return eventService.save(newEvent);
        } else if (email.contains("anonymousUser")) {
            User findEmail = userRepository.findByEmail(newEventEmail);
            System.out.println("anonymousUser condition passed");
            if (findEmail == null) {
                System.out.println("findEmail condition passed");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            return eventService.save(newEvent);
        } else {
            System.out.println("else condition");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    //    update event
    @PutMapping("/{id}")
    public Event updateEvent(@Valid @RequestBody EditEventDTO updateEvent, @PathVariable Integer id) {
        //get email from token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        //get role from token
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        User userEmail = userRepository.findByEmail(email);
        Event storedEventDetails = repository.getById(id);
        storedEventDetails.setId(updateEvent.getId());
        storedEventDetails.setEventStartTime(updateEvent.getEventStartTime());
        storedEventDetails.setEventNotes(updateEvent.getEventNotes());
        //if user email is equal to event email or role is admin then can update
        if (storedEventDetails.getBookingEmail().equals(email) || role.contains("admin")) {
            return repository.saveAndFlush(storedEventDetails);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    //   delete event
    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable Integer bookingId) {
        //get email from token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        //get role from token
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        User userEmail = userRepository.findByEmail(email);
        Event storedEventDetails = repository.getById(bookingId);
        if (storedEventDetails.getBookingEmail().equals(email) || role.contains("admin")) {
//            return repository.saveAndFlush(storedEventDetails);
            repository.findById(bookingId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, bookingId + " does not exist !"));
            repository.deleteById(bookingId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
//        repository.findById(bookingId).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, bookingId + " does not exist !"));
//        repository.deleteById(bookingId);
    }


}