package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sit.int221.oasipservice.UploadFileResponse;
import sit.int221.oasipservice.dtos.EditEventDTO;
import sit.int221.oasipservice.dtos.EventDTO;
import sit.int221.oasipservice.dtos.NewEventDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.EventCategory;
import sit.int221.oasipservice.entities.EventCategoryOwner;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.EventCategoryOwnerRepository;
import sit.int221.oasipservice.repositories.EventCategoryRepository;
import sit.int221.oasipservice.repositories.EventRepository;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.services.EmailService;
import sit.int221.oasipservice.services.EventService;
import sit.int221.oasipservice.services.FileStorageService;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EventService eventService;
    private EventRepository repository;
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;


    @Autowired
    EventCategoryOwnerRepository eventCategoryOwnerRepository;


    public EventController(EventRepository repository, UserRepository userRepository, EventCategoryOwnerRepository eventCategoryOwnerRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.eventCategoryOwnerRepository = eventCategoryOwnerRepository;
    }


    // Get all-events
// lecturer ดู events-lists  ของตัวเองเท่านั้น
    @GetMapping("")
    public List<EventDTO> getAllEvent() {
        //get users'id from email by token
        String lecEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(lecEmail);
        //get category by users'id
        List<EventCategoryOwner> eventCategoryOwner = eventCategoryOwnerRepository.findByUserid(user.getId());
        //get categoryid from category


//        ขั้นตอนนี้จะ
        int lecId = user.getId();
        List<EventCategoryOwner> categoryId = eventCategoryOwnerRepository.findByUserid(lecId);
//
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
//        if (role.contains("admin")) {
        if (role.contains("student")) {
//            return eventService.getAllEventByDTO();
            return eventService.getAllUserByEmail();
        } else if (role.contains("lecturer")) {
            System.out.println("ผ่าน role: lecturer");
//            System.out.println("ได้ cateID : " + ำ);
            return eventService.getEventsFromLecturerId(lecId);
        } else {
//            return eventService.getAllUserByEmail();
            return eventService.getAllEventByDTO();
        }
    }

    // Get event-by-bookingId
//    lecturer ดู detail
    @GetMapping("/{bookingId}")
    public List<EventDTO> getSimpleEventDTO(@PathVariable Integer bookingId) {
        //get email from token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        //get role from token
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        User userEmail = userRepository.findByEmail(email);
        Event storedEventDetails = repository.getById(bookingId);

//        สำหรับกรองข้อมูลของ lecturer
        String lecEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User lecUser = userRepository.findByEmail(lecEmail);
        int lecId = lecUser.getId();
        if (storedEventDetails.getBookingEmail().equals(email) || role.contains("admin")) {
            return eventService.getSimpleEventById(bookingId);

        } else if (storedEventDetails.getBookingEmail().equals(email) || role.contains("lecturer")) {
            System.out.println("ผ่าน role: lecturer");
            List isEmptyDetail = eventService.getDetailByLecturerIdAndBookingId(lecId, bookingId);
            if (isEmptyDetail.isEmpty()) {
                System.out.println(isEmptyDetail);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD REQUEST");
            }
            return eventService.getDetailByLecturerIdAndBookingId(lecId, bookingId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

    }

    //filter-by-eventCategoryId

    @GetMapping("/getByEventCategories/{eventCategoryId}")
    public List<EventDTO> getByEventCategory(@PathVariable Integer eventCategoryId) {
        //find user id from email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Integer userId = user.getId();
        //getrole
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.contains("student")){
            System.out.println(userId);
            return eventService.getByEventCategoryByEmail(eventCategoryId);
        }else if (role.contains("lecturer")){
            System.out.println(userId);
            return eventService.getByEventCategoryByLecturerOwner(eventCategoryId,userId);
        }else{
            System.out.println(userId);
            return eventService.getByEventCategory(eventCategoryId);
        }
    }
    //this approach is put too much logic in controller
//        //get role from token
//        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
//        if (role.contains("student")) {
//            return eventService.getByEventCategoryByEmail(eventCategoryId);
//        } else if (role.contains("admin")) {
//            return eventService.getByEventCategory(eventCategoryId);
//        } else if (role.contains("lecturer")) {
//            //if lecturer is owner of eventCategory will return all event in eventCategory
//            //get lecId from email
//            String lecEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//            User lecUser = userRepository.findByEmail(lecEmail);
//            int lecId = lecUser.getId();
//            List<EventCategoryOwner> categoryId = eventCategoryOwnerRepository.findByUserid(lecId);
//            System.out.println(categoryId);
//            if (eventCategoryId.equals(categoryId)) {
//                System.out.println(categoryId);
//                System.out.println(lecId);
//                return eventService.getByEventCategory(eventCategoryId);
//            }
//            else {
//                System.out.println("catID"+categoryId);
//                System.out.println("lecId"+lecId);
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//            }
//            //get category id from eventCategoryOwner
//        } else {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//        }
//    }

    //filter-by-date :: filter-by-Upcoming
    @GetMapping("/getEventByUpcoming")
    public List<EventDTO> getEventsByUpcoming() {
        //find user id from email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Integer userId = user.getId();
        //getrole
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.contains("student")){
            return eventService.getEventsByUpcomingByEmail();
        }
        else if (role.contains("lecturer")){
            return eventService.getEventsByUpcomingByCategoryOwner(userId);
        }
        else{
            return eventService.getEventsByUpcoming();
        }
    }

    //filter-by-date :: filter-by-Past
    @GetMapping("/getEventByPast")
    public List<EventDTO> getEventsByPast() {
        //find user id from email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Integer userId = user.getId();
        //getrole
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.contains("student")){
            return eventService.getEventsByPastByEmail();
        }
        else if (role.contains("lecturer")){
            return eventService.getEventsByPastByCategoryOwner(userId);
        }
        else{
            return eventService.getEventsByPast();
        }
    }

    //    filter: specific date
    @GetMapping("/getEventsByEventStartTime/{eventStartTime}")
    public List<EventDTO> getEventsByEventStartTime(@PathVariable String eventStartTime) {
        //find user id from email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Integer userId = user.getId();
        //getrole
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.contains("student")){
            return  eventService.getEventsByEventStartTimeByEmail(eventStartTime);
        }
        else if (role.contains("lecturer")){
            return eventService.getEventsByEventStartTimeByCategoryOwner(eventStartTime,userId);
        }
        else{
            return eventService.getEventsByEventStartTime(eventStartTime);
        }
    }

    //    create new event
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody NewEventDTO newEvent, MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String newEventEmail = newEvent.getBookingEmail();

        System.out.println("before condition post [email] : "+email);
        System.out.println("before condition post [role] : "+role);
        if (role.contains("lecturer")) {
            System.out.println("role: " + role);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (role.contains("admin")
                || role.contains("[ROLE_ANONYMOUS]")
                || role.contains("student")) {
            //change file name to event name
            String fileName = fileStorageService.storeFile(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/fileUpload/")
                    .path(fileName)
                    .toUriString();
            System.out.println("email : "+email);
            System.out.println("role: " + role);
            System.out.println("post event success !!!");
            return eventService.save(newEvent);
        }
//        else if (email.contains("anonymousUser")) {
//            User findEmail = userRepository.findByEmail(newEventEmail);
//            System.out.println("anonymousUser condition passed");
//            if (findEmail == null) {
//                System.out.println("findEmail condition passed");
//                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//            }
//            return eventService.save(newEvent);
//        }
        else {
            System.out.println("else condition");
            System.out.println("email : "+email);
            System.out.println("role: " + role);
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

    //upload file to local folder


}