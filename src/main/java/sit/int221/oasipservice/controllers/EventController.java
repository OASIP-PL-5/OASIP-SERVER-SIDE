package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.EditEventDTO;
import sit.int221.oasipservice.dtos.EventDTO;
import sit.int221.oasipservice.dtos.NewEventDTO;
import sit.int221.oasipservice.entities.*;
import sit.int221.oasipservice.repositories.*;
import sit.int221.oasipservice.services.DBFileService;
import sit.int221.oasipservice.services.EmailService;
import sit.int221.oasipservice.services.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;
    private EventRepository repository;
    private UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;


    @Autowired
    private EmailService emailService;

    @Autowired
    private DBFileService dbFileService;


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
//        File getFile = (File) fileRepository.getFileByBookingId(bookingId);
//        System.out.println("ได้ file : " + getFile);

        // check if there is a file with this bookingId
//        List<File> getFile = fileRepository.getFileByBookingId(bookingId);

        File getFile = fileRepository.getFileByBookingId(bookingId);
        if (getFile != null){
            System.out.println(fileRepository.getFileByBookingId(bookingId).getId());
            System.out.println(fileRepository.getFileByBookingId(bookingId).getFileName());
            System.out.println(fileRepository.getFileByBookingId(bookingId).getFileType());
        }else {
            System.out.println("No file found");
        }



//        if (getFile.size() > 0) {
////            System.out.println("ได้ file : " + getFile);
////            System.out.println(fileRepository.getFileByBookingId(bookingId));
//            System.out.println(fileRepository.getFileByBookingId(bookingId).get(0).getId());
//            System.out.println(fileRepository.getFileByBookingId(bookingId).get(0).getFileName());
//            return eventService.getEventWithFileByBookingId(bookingId);

//        }else {
//            System.out.println("no file in this bookingId");
//        }

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
        if (role.contains("student")) {
            System.out.println(userId);
            return eventService.getByEventCategoryByEmail(eventCategoryId);
        } else if (role.contains("lecturer")) {
            System.out.println(userId);
            return eventService.getByEventCategoryByLecturerOwner(eventCategoryId, userId);
        } else {
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
        if (role.contains("student")) {
            return eventService.getEventsByUpcomingByEmail();
        } else if (role.contains("lecturer")) {
            return eventService.getEventsByUpcomingByCategoryOwner(userId);
        } else {
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
        if (role.contains("student")) {
            return eventService.getEventsByPastByEmail();
        } else if (role.contains("lecturer")) {
            return eventService.getEventsByPastByCategoryOwner(userId);
        } else {
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
        if (role.contains("student")) {
            return eventService.getEventsByEventStartTimeByEmail(eventStartTime);
        } else if (role.contains("lecturer")) {
            return eventService.getEventsByEventStartTimeByCategoryOwner(eventStartTime, userId);
        } else {
            return eventService.getEventsByEventStartTime(eventStartTime);
        }
    }

    //    create new event
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody NewEventDTO newEvent) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String newEventEmail = newEvent.getBookingEmail();

        LocalDateTime newEventStartTime = newEvent.getEventStartTime();
        List<Event> eventStartTime = repository.getAllEvents();
        System.out.println("before condition post [email] : " + email);
        System.out.println("before condition post [role] : " + role);
        for (int i = 0; i < eventStartTime.size(); i++) {
            System.out.println(eventStartTime.get(i).getEventStartTime());
            System.out.println(eventStartTime.get(i).getEventStartTime().isEqual(newEventStartTime));
            if (eventStartTime.get(i).getEventStartTime().isEqual(newEventStartTime)) {
                System.out.println("overlap condition");
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }
        if (newEvent.getBookingName().length() == 0) {
            System.out.println("Booking Name must be filled out! === status 417");
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        }
        if (newEvent.getBookingEmail().length() == 0) {
            System.out.println("Booking Email must be filled out! === status 400");
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        }
        if (role.contains("lecturer")) {
            System.out.println("role: " + role);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (role.contains("admin")
                || role.contains("[ROLE_ANONYMOUS]")
                || role.contains("student")) {
            System.out.println("email : " + email);
            System.out.println("role: " + role);
            System.out.println("post event success !!!");
            return eventService.save(newEvent);
        } else {
            System.out.println("else condition");
            System.out.println("email : " + email);
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

    // วิธีแบบ รวม POST event และ file ใน endpoint เดียวกัน
//    @PostMapping(value = "/newEventWithFile")
//    @ResponseStatus(HttpStatus.CREATED)
//    @RequestMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public Event createWithFile(@Valid @RequestBody NewEventDTO newEvent, @RequestBody MultipartFile file) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
//        String newEventEmail = newEvent.getBookingEmail();
//        //create event with file upload
//        System.out.println("before condition post [email] : " + email);
//        System.out.println("before condition post [role] : " + role);
//        if (role.contains("lecturer")) {
//            System.out.println("role: " + role);
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//        }
//        if (role.contains("admin")
//                || role.contains("[ROLE_ANONYMOUS]")
//                || role.contains("student")) {
//            System.out.println("email : " + email);
//            System.out.println("role: " + role);
//            System.out.println("post event success !!!");
//            if (!file.isEmpty()) {
//                eventService.saveWithFile(newEvent, file);
//                throw new ResponseStatusException(HttpStatus.CREATED, "Event created successfully" + newEvent);
//            } else {
//                return eventService.save(newEvent);
//            }
//        } else {
//            System.out.println("else condition");
//            System.out.println("email : " + email);
//            System.out.println("role: " + role);
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        }
//    }
}