package sit.int221.oasipservice.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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

    //assign role จาก string ดิบๆ เก็บเป็น object-String เลย เพื่อความสะดวกในการเช็ค roles
    private final String admin = "admin";
    private final String lecturer = "lecturer";
    private final String student = "student";

    // Get all-events
// lecturer ดู events-lists  ของตัวเองเท่านั้น
// guest, student, admin สามารถดู event ทั้งหมดได้ ...
// แต่ guest ดูแบบ blinded-event & student-blinded-event เฉพาะ event ที่ไม่ใช่ของตัวเอง
    @GetMapping("")
    public List<EventDTO> getAllEvent(HttpServletRequest request) {
        System.out.println("\n--------\nการทำงานของ getAllEvent()\n--------");
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user");
            return eventService.getAllEventByDTO();
        } else {
//        ทั้งหมดนี้ ต้องทำในเงื่อนไขที่รับ token เท่านั้น ถ้าไม่มี token ให้เป็น guest
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
//        เช็คว่าตรงกับ algorithm ไหน เป็นของ azure ดีกว่า
            System.out.println(tokenDecoded.getAlgorithm());
//หาก algorithm เป็น RS256 ก็เท่ากับ เป็น token จาก azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
                if (tokenDecoded.getClaims().get("roles") == null) {
                    System.out.println("หากไม่มี role ใน token-claims == guest");
                    return eventService.getAllEventByDTO();
                } else if (tokenDecoded.getClaims().get("roles") != null) {
                    String msRole = tokenDecoded.getClaims().get("roles").toString();
                    String msEmail = tokenDecoded.getClaims().get("preferred_username").toString();
                    String msName = tokenDecoded.getClaims().get("name").toString();
                    System.out.println("this is cliaims from jwtAzure(roles) : " + msRole);
                    System.out.println("this is cliaims from jwtAzure(email) : " + msEmail);
                    System.out.println("this is cliaims from jwtAzure(name) : " + msName);

                    if (msRole.contains(admin) || msRole.contains(student)) {
                        System.out.println("ms [admin] or [student] role : " + msRole);
                        return eventService.getAllEventByDTO();
                    } else if (msRole.contains(lecturer)) {
                        System.out.println("ms [lecturer] role : " + msRole);
                        User user = userRepository.findByEmail(msEmail.replaceAll("^\"|\"$", ""));

                        System.out.println("user : " + user);
                        System.out.println(userRepository.findByEmail(msEmail));

                        Integer lecId = user.getId();
                        System.out.println("this is lecturer id : " + lecId);
                        return eventService.getEventsFromLecturerId(lecId);
                    }
                }
            }
//        หาก algorithm != RS256 ก็เท่ากับว่า ไม่ใช่ azuretoken (เป็น token ปกติจากระบบ oasip)
//            token from oasip system
            else {
                System.out.println("token from oasip-system");
                String userTokenEmail = SecurityContextHolder.getContext().getAuthentication().getName();
                String userTokenRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                System.out.println("user email from token : " + userTokenEmail);
                System.out.println("user role from token : " + userTokenRole);

                //get users'id from email by token
                String lecEmail = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(lecEmail);
//        ขั้นตอนนี้จะ
                int lecId = user.getId();
//
//                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                if (role.contains(lecturer)) {
                    System.out.println("this user is [lecturer] : " + role);
                    return eventService.getEventsFromLecturerId(lecId);
                } else if (role.contains(admin) || role.contains(student)) {
                    System.out.println("this user is [admin] or [student] : " + role);
                    return eventService.getAllEventByDTO();
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong");
    }

    // Get event-by-bookingId
//    lecturer ดู detail
    @GetMapping("/{bookingId}")
    public List<EventDTO> getSimpleEventDTO(@PathVariable Integer bookingId, HttpServletRequest request) {
        System.out.println("\n--------\nการทำงานของ getEventByBookingId()\n--------");
//ตรวจก่่อนเลยว่า event-found ไหม ถ้าไม่พบ ก็ 404 เลย
        Optional<Event> findEvent = repository.findById(bookingId);
        if (findEvent.isEmpty()) {
            System.out.println("Event not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "event not found");
        }
//guest (ไม่มี token)
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user : guest can view blinded-event");
            return eventService.getBlindEventById(bookingId);
        } else {
//decode token เพื่อเช็ค algorithm
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
            System.out.println(tokenDecoded.getAlgorithm());
//token from azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
//role == null == guest-user
                if (tokenDecoded.getClaims().get("roles") == null) {
                    System.out.println("this is [guest] user : guest can view blinded-event");
                    return eventService.getBlindEventById(bookingId);
                }
//role ใน token != null ก็คือพบ role จากการ decode azure-token ดังกล่าว
                else if (tokenDecoded.getClaims().get("roles") != null) {
                    String msRole = tokenDecoded.getClaims().get("roles").toString();
                    String msEmail = tokenDecoded.getClaims().get("preferred_username").toString();
//admin-role
                    if (msRole.contains(admin)) {
                        System.out.println("ms [admin] role : " + msRole);
                        return eventService.getSimpleEventById(bookingId);
                    }
//student-role
                    else if (msRole.contains(student)) {
                        List<Event> checkEventEmailMS = repository.getEventByBookingEmailAndBookingId(msEmail, bookingId);
//student กรณีที่ email+bookingId ไม่พบ event ของตน แสดงว่า เป็น event ของคนอื่น ในเงื่อนไข isEmpty() นี้เราจะให้ "view-blinded-event"
// (แต่่ถ้า query แล้วเจอ event ก็จะ return แบบ eventdetail ทั้งหมดตามปกติ)
                        if (checkEventEmailMS.isEmpty()) {
                            //blinded-event
                            System.out.println("view blind event success ?");
                            return eventService.getBlindEventById(bookingId);
                        } else {
                            System.out.println("event of this student");
                            return eventService.getSimpleEventById(bookingId);
                        }
                    }
//lecturer-role
                    else if (msRole.contains(lecturer)) {
                        System.out.println("ms [lecturer] role : " + msRole);
                        User lecUser = userRepository.findByEmail(msEmail);
                        Integer lecId = lecUser.getId();
                        List isEmptyDetail = eventService.getDetailByLecturerIdAndBookingId(lecId, bookingId);
                        if (isEmptyDetail.isEmpty()) {
                            System.out.println(isEmptyDetail);
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD REQUEST");
                        }
                        return eventService.getDetailByLecturerIdAndBookingId(lecId, bookingId);
                    }
                }
            }

//token from oasip
            else {
                //get email from token
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                //get role from token
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                Event storedEventDetails = repository.getById(bookingId);

//        สำหรับกรองข้อมูลของ lecturer
                String lecEmail = SecurityContextHolder.getContext().getAuthentication().getName();
                User lecUser = userRepository.findByEmail(lecEmail);
                int lecId = lecUser.getId();
                if (role.contains(admin)) {
                    return eventService.getSimpleEventById(bookingId);
                } else if (role.contains(student)) {
                    List<Event> checkEventEmail = repository.getEventByBookingEmailAndBookingId(email, bookingId);
                    if (checkEventEmail.isEmpty()) {
                        //blinded-event
                        System.out.println("view blind event success ?");
                        return eventService.getBlindEventById(bookingId);
                    } else {
                        System.out.println("event of this student");
                        return eventService.getSimpleEventById(bookingId);
                    }
                } else if (storedEventDetails.getBookingEmail().equals(email) || role.contains(lecturer)) {
                    System.out.println("ผ่าน role: lecturer");
                    List isEmptyDetail = eventService.getDetailByLecturerIdAndBookingId(lecId, bookingId);
                    if (isEmptyDetail.isEmpty()) {
                        System.out.println(isEmptyDetail);
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD REQUEST");
                    }
                    return eventService.getDetailByLecturerIdAndBookingId(lecId, bookingId);
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong");
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
    public Event create(@Valid @RequestBody NewEventDTO newEvent, HttpServletRequest request) {
        System.out.println("\n--------\nการทำงานของ add event\n--------");

//guest (ไม่มี token)
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user : guest cannot add-event");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "guest cannot post-event");
        }

//else:condition ตัวนี้สำหรับทำงานกรณีมี token
        else {
//decode token เพื่อเช็ค algorithm
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
            System.out.println(tokenDecoded.getAlgorithm());
            LocalDateTime newEventStartTime = newEvent.getEventStartTime();
            List<Event> checkEventEndTime = repository.getAllEventsByEventCategory(newEvent.getEventCategoryName());
//token from azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
//guest เมื่อ ไม่พบ role ใน token
                if (tokenDecoded.getClaims().get("roles") == null) {
                    System.out.println("this is [guest] user : guest cannot add-event");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "guest cannot post-event");
                }
//lecturer cannot post-event
                else if (tokenDecoded.getClaims().get("roles").toString().contains(lecturer)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Lecturer not have permission to add-event");
                }
// role:student, role:admin
                else if (tokenDecoded.getClaims().get("roles").toString().contains(student) ||
                        tokenDecoded.getClaims().get("roles").toString().contains(admin)) {
//check:exception starttime overlap
                    for (int i = 0; i < checkEventEndTime.size(); i++) {
                        LocalDateTime eventStartTime = checkEventEndTime.get(i).getEventStartTime();
                        Integer eventDuration = checkEventEndTime.get(i).getEventDuration();
                        System.out.println("eventStartTime: " + eventStartTime);
                        System.out.println("eventDuration: " + eventDuration);
                        LocalDateTime endTime = eventStartTime.plusMinutes(eventDuration);
                        System.out.println("eventEndTime: " + endTime);
                        System.out.println("--------------------");
                        if (eventStartTime.isEqual(newEventStartTime)) {
                            System.out.println("overlap condition");
                            throw new ResponseStatusException(HttpStatus.CONFLICT);
                        }
                    }
//check:exception bookingname length is 0
                    if (newEvent.getBookingName().length() == 0) {
                        System.out.println("Booking Name must be filled out! === status 417");
                        throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
                    }
//check:exception bookingemail length is 0
                    if (newEvent.getBookingEmail().length() == 0) {
                        System.out.println("Booking Email must be filled out! === status 400");
                        throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
                    }
//เมื่อไม่เจอ error ก็จะ post ได้
                    System.out.println("post event success !!!");
                    return eventService.save(newEvent);
                }
            }

//token from oasip
            else {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
//check:past-event-starttime
                if (newEvent.getEventStartTime().isBefore(java.time.LocalDateTime.now())) {
                    System.out.println("check past-event");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                //check:exception starttime overlap
                System.out.println("before condition post [email] : " + email);
                System.out.println("before condition post [role] : " + role);
                for (int i = 0; i < checkEventEndTime.size(); i++) {
                    LocalDateTime eventStartTime = checkEventEndTime.get(i).getEventStartTime();
                    Integer eventDuration = checkEventEndTime.get(i).getEventDuration();
                    System.out.println("--------------------");
                    System.out.println("eventStartTime: " + eventStartTime);
                    System.out.println("eventDuration: " + eventDuration);
                    LocalDateTime endTime = eventStartTime.plusMinutes(eventDuration);
                    System.out.println("eventEndTime: " + endTime);
                    System.out.println("--------------------");
                    if ((!(newEventStartTime.isBefore(eventStartTime)) && newEventStartTime.isBefore(endTime)) || newEventStartTime.isEqual(endTime)) {
                        System.out.println(!newEventStartTime.isBefore(eventStartTime));
                        System.out.println(newEventStartTime.isBefore(endTime));
                        System.out.println(newEventStartTime.isEqual(endTime));
                        System.out.println("overlap condition");
                        System.out.println("eventStartTime: " + eventStartTime);
                        System.out.println("endtime: " + endTime);
                        System.out.println("newEventStartTime: " + newEventStartTime);

                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Previous booking end-time is "
                                + endTime.toLocalDate() + ' ' + endTime.toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a")) + '\n'
                                + "Please try again at " + endTime.plusMinutes(1).toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
                                + " or later");
                    }
                }
//check:exception bookingname length is 0
                if (newEvent.getBookingName().length() == 0) {
                    System.out.println("Booking Name must be filled out! === status 417");
                    throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
                }
//check:exception bookingemail length is 0
                if (newEvent.getBookingEmail().length() == 0) {
                    System.out.println("Booking Email must be filled out! === status 400");
                    throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
                }
                if (role.contains(lecturer)) {
                    System.out.println("role: " + role);
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Lecturer not have permission to add-event");
                }
                if (role.contains(admin) || role.contains(student)) {
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
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong");
    }


    //    update event
    @PutMapping("/{id}")
    public Event updateEvent(@Valid @RequestBody EditEventDTO updateEvent, @PathVariable Integer id, HttpServletRequest request) {
        System.out.println("\n--------\nการทำงานของ edit-event \n--------");
//guest (ไม่มี token) จะไม่ม่ีสิทธิ์ในการ edit-event ใดๆทั้งสิ้น
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user : guest cannot edit-event");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "guest cannot edit-event");
        }
//else-condition เมื่อพบ token
        else {
//กรองว่าเจอ event-booking ในการ edit ไหม หากไม่เจอ จะ throw 404 ทันที
            Optional<Event> findEvent = repository.findById(id);
            if (findEvent.isEmpty()) {
                System.out.println("Event not found");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "event not found");
            }
//ส่วนการทำงาน update-event (ไว้ตรงนี้ เพื่อให้การทำงานมีประสิทธิภาพ และเหมาะสมขึ้น ตาม flow ของ code)
            Event storedEventDetails = repository.getById(id);
            storedEventDetails.setId(updateEvent.getId());
            storedEventDetails.setEventStartTime(updateEvent.getEventStartTime());
            storedEventDetails.setEventNotes(updateEvent.getEventNotes());
//decode token เพื่อเช็ค algorithm
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
            System.out.println(tokenDecoded.getAlgorithm());
//token from azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
//guest เมื่อ ไม่พบ role ใน token
                if (tokenDecoded.getClaims().get("roles") == null) {
                    System.out.println("this is [guest] user : guest cannot edit-event");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "guest cannot edit-event");
                }
//lecturer-role ไม่สามารถ edit-event
                else if (tokenDecoded.getClaims().get("roles").toString().contains(lecturer)) {
                    System.out.println("lecturer not have permission to edit event.");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Lecturer not have permission to edit event.");
                }
//admin-role
                else if (tokenDecoded.getClaims().get("roles").toString().contains(admin)) {
                    System.out.println("admin role");
                    return repository.saveAndFlush(storedEventDetails);
                }
//student-role โดย default เมื่อ role == null จาก azure-token
                else if (tokenDecoded.getClaims().get("roles").toString().contains(student)) {
                    System.out.println("student role");
                    String msEmail = tokenDecoded.getClaims().get("preferred_username").toString();
                    List<Event> checkEventEmailMS = repository.getEventByBookingEmailAndBookingId(msEmail, id);
                    if (checkEventEmailMS.isEmpty()) {
                        System.out.println("Student not have permission to edit this event.");
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This Student not have permission to edit this event.");
                    } else {
                        return repository.saveAndFlush(storedEventDetails);
                    }
                }
            }

//token from oasip
            else {
                System.out.println("token from oasip");
                //get email from token
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                //get role from token
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

                //if user email is equal to event email or role is admin then can update
                if (role.contains(lecturer)) {
                    System.out.println("lecturer not have permission to edit event.");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Lecturer not have permission to edit event.");
                } else if (role.contains(admin)) {
                    System.out.println("admin role");
                    return repository.saveAndFlush(storedEventDetails);
                } else if (role.contains(student)) {
                    System.out.println("student role");
                    List<Event> checkEvent = repository.getEventByBookingEmailAndBookingId(email, id);
                    if (checkEvent.isEmpty()) {
                        System.out.println("This student not have permission to edit this event.");
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This Student not have permission to edit this event.");
                    } else {
                        return repository.saveAndFlush(storedEventDetails);
                    }
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong");
    }

    //   delete event
    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable Integer bookingId, HttpServletRequest request) {
        System.out.println("\n--------\nการทำงานของ delete-event \n--------");
//กรองก่อนเลย ว่าเป็น guest (ไม่มี token) ไหม (เพราะ guest ไม่มีสิทธิ์ในการเข้าถึง feature นี้)
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user : guest cannot delete-event");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "guest cannot delete-event");
        }
// เจอ event-booking ในการ delete ไหม
        Optional<Event> findEvent = repository.findById(bookingId);
        if (findEvent.isEmpty()) {
            System.out.println("Event not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "event not found");
        }
//else-condition เมื่อพบ token
        else {
//decode token เพื่อเช็ค algorithm
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
            System.out.println(tokenDecoded.getAlgorithm());
//token from azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
//guest เมื่อไม่พบ role ใน token
                if (tokenDecoded.getClaims().get("roles") == null) {
                    System.out.println("this is [guest] user : guest cannot delete-event");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "guest cannot delete-event");
                }
//student role (ทำงานในนี้เกี่ยวกับ student role เลย
                else if (tokenDecoded.getClaims().get("roles").toString().contains(student)) {
                    System.out.println("default role is [student] role");
                    String msEmail = tokenDecoded.getClaims().get("preferred_username").toString();
                    List<Event> checkEventEmailMS = repository.getEventByBookingEmailAndBookingId(msEmail, bookingId);
//student กรณีที่ email+bookingId ไม่พบ event ของตน แสดงว่า เป็น event ของคนอื่น ในเงื่อนไข isEmpty() นี้เราจะไม่อนุญาต ให้ student คนนี้ลบ event ของคนอื่น
// (แต่่ถ้า query แล้วเจอ event ก็จะ สามารถ deleteEvent นั้นได้
                    if (checkEventEmailMS.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This student cannot delete other people's events");
                    } else
                        repository.deleteById(bookingId);
                }
//พบ role จาก token-azure
                else if (tokenDecoded.getClaims().get("roles") != null) {
                    String msRole = tokenDecoded.getClaims().get("roles").toString();
//admin-role
                    if (msRole.contains(admin)) {
                        System.out.println("ms [admin] role : " + msRole);
                        repository.deleteById(bookingId);
                    }
//lecturer-role
                    else if (msRole.contains(lecturer)) {
                        System.out.println("lec cannot delete event");
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "lecturer cannot delete-event");
                    }
                }
            }

//token from oasip
            else {
                //get email from token
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                //get role from token
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                User userEmail = userRepository.findByEmail(email);

                Event storedEventDetails = repository.getById(bookingId);
                if (storedEventDetails.getBookingEmail().equals(email) || role.contains(admin)) {
//            return repository.saveAndFlush(storedEventDetails);
                    repository.findById(bookingId).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, bookingId + " does not exist !"));
                    repository.deleteById(bookingId);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }
        }
    }


}