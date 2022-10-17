package sit.int221.oasipservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.EventDTO;
import sit.int221.oasipservice.dtos.NewEventDTO;
import sit.int221.oasipservice.dtos.UserDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.EventRepository;
import sit.int221.oasipservice.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;


    @Autowired
    private ModelMapper modelMapper;

    public EventService(EventRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository=userRepository;
    }

    @Autowired
    private ListMapper listMapper;

    // service: get-all-events
    public List<EventDTO> getAllEventByDTO() {
        return repository.findAll(Sort.by(Sort.Direction.DESC,"eventStartTime")).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    //get all event by email
    public List<EventDTO> getAllUserByEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByEmail(email).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    //  service: get-by-bookingId
    public List<EventDTO> getSimpleEventById(Integer bookingId) {

        return repository.findById(bookingId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    //    service: getEventDetails-by-bookingId-and-lecturerId
    public List<EventDTO> getDetailByLecturerIdAndBookingId(Integer userId, Integer bookingId) {
        return repository.findEventDetailFromUserIdAndBookingId(userId, bookingId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }


    //    service: filter-by-eventCategoryId
    public List<EventDTO> getByEventCategory(Integer eventCategoryId) {
        return repository.getByEventCategory(eventCategoryId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }
    //   service: filter-by-eventCategoryIdByEmail
    public List<EventDTO> getByEventCategoryByEmail(Integer eventCategoryId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return repository.getByEventCategoryAndEmail(eventCategoryId, email).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }
    //    service: filter-by-eventCategoryIdBylecturerOwner
    public List<EventDTO> getByEventCategoryByLecturerOwner(Integer eventCategoryId, Integer userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.getByEventCategoryByCategoryOwner(eventCategoryId, userId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }



    //service: get-by-eventcategory (role:lecturer)
    public List<EventDTO> getByEventCategoryLecturer(Integer eventCategoryId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (repository.getByEventCategory(eventCategoryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getByEventCategory(eventCategoryId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }


    //service: สำหรับเรียก event-lists ที่สัมพันธ์กับ lecturer
    public List<EventDTO> getEventsFromLecturerId(Integer userId) {
        return repository.findEventsFromUserId(userId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    //service: สำหรับเรียก event-detail ที่สัมพันธ์กับ lecturer


    //    service: filter-by-upcoming
    public List<EventDTO> getEventsByUpcoming() {
        if (repository.getEventsByUpcoming().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByUpcoming().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }
    public List<EventDTO> getEventsByUpcomingByEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (repository.getEventsByUpcoming().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByUpcomingByEmail(email).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }
    public List<EventDTO> getEventsByUpcomingByCategoryOwner(Integer userId) {
        if (repository.getEventsByUpcoming().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByUpcomingByCategoryOwner(userId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }









    //    service: filter-by-past
    public List<EventDTO> getEventsByPast() {
        if (repository.getEventsByPast().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByPast().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }
    public List<EventDTO> getEventsByPastByEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (repository.getEventsByUpcoming().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByPastByEmail(email).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }
    public List<EventDTO> getEventsByPastByCategoryOwner(Integer userId) {
        if (repository.getEventsByUpcoming().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByPastByCategoryOwner(userId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }










    //    service: filter-by-specificDate
    public List<EventDTO> getEventsByEventStartTime(String eventStartTime) {
        if (repository.getEventsByEventStartTime(eventStartTime).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByEventStartTime(eventStartTime).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }
    public List<EventDTO> getEventsByEventStartTimeByEmail(String eventStartTime) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (repository.getEventsByEventStartTime(eventStartTime).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByEventStartTimeByEmail(eventStartTime,email).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }
    public List<EventDTO> getEventsByEventStartTimeByCategoryOwner(String eventStartTime,Integer userId) {
        if (repository.getEventsByEventStartTime(eventStartTime).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByEventStartTimeByCategoryOwner(eventStartTime,userId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }












    //    serviceMethod: convert-Entity-to-DTO (ใช้งานใน serviceMethod เกี่ยวกับการ GET-EVENTS ทั้งสิ้น )
    private EventDTO convertEntityToDto(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setBookingName(event.getBookingName());
        eventDTO.setBookingEmail(event.getBookingEmail());
        eventDTO.setEventStartTime(event.getEventStartTime());
        eventDTO.setEventDuration(event.getEventDuration());
        eventDTO.setEventNotes(event.getEventNotes());
        eventDTO.setEventCategoryId(event.getEventCategory().getId());
        eventDTO.setEventCategoryName(event.getEventCategory().getEventCategoryName());
        return eventDTO;
    }

//    public List<SimpleEventDTO> getEventCatNameBySearch(String eventCategoryName) {
//        Event event = repository.findCategoryByName(eventCategoryName)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
//                        , "Event Category: " + eventCategoryName + "doesn't exist"));
//        return modelMapper.map(event, SimpleEventDTO.class);
//    }


//    public List<SimpleEventDTO> getEvents() {
//        List<Event> eventList = repository.findAll();
//        return listMapper.mapList(eventList, SimpleEventDTO.class, modelMapper);
//    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
//
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//        return errors;
//    }

//    service สำหรับ edit-PUT (ไม่ใช่แล้ว แต่เก็บไว้เป็นความทรงจำ)
//    public Event update(EditEventDTO updateEvent, Integer id) {
//        Optional<Event> existingEvent = repository.findById(id);
//        modelMapper.map(updateEvent, existingEvent);
//        Event e = modelMapper.map(updateEvent, Event.class);
//        return repository.saveAndFlush(e);
//    }

    public Event save(NewEventDTO newEvent) {   //, SimpleEventDTO originEvent
//convert dateTime to String : แปลงค่า dateTime ของ origin เป็น string เพื่อจะนำมาใช้ รับการบวก duration
//        LocalDateTime date = originEvent.getEventStartTime();
//        // เราจะต้องนำ dateFormat ไปใช้ในเงื่อนไข
//        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String strOriginDate = dateFormat.format(date);
//
//// จากนั้น นำ strOriginDate ไปเพื่อเตรียมรับการ + duration
//        LocalDateTime ltOrigin = LocalDateTime.parse(strOriginDate);
//
//// กำหนดเงื่อนไข ถ้าหาก newEvent.getEventStartTime น้อยกว่าหรือเท่ากับ origin(ที่ได้รับการบวก duration แล้ว จะ throw-400)
//// .before & .equals === return 400
//        if (
//         (newEvent.getEventStartTime().isBefore(originEvent.getEventStartTime().plusMinutes(originEvent.getEventDuration()))) ||
//                 (newEvent.getEventStartTime().isEqual(originEvent.getEventStartTime().plusMinutes(originEvent.getEventDuration())))
//        ) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please Select Start Time.");
//        }
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString(); || role.contains("admin")

        //if start time is not exactly same with other event in the same category
        List<Event> eventsList = repository.findAll();
        for (int i = 0; i < eventsList.size(); i++) {
            if (newEvent.getEventStartTime().equals(eventsList.get(i).getEventStartTime())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "StartTime cannot be the same.");
            }
        }

        // Creating a simple mail message
        SimpleMailMessage mailMessage
                = new SimpleMailMessage();

        // Setting up necessary details
        mailMessage.setFrom(sender);
        mailMessage.setTo(newEvent.getBookingEmail());
        mailMessage.setText("booking_name: " + newEvent.getBookingName() + "\n" + "email: " + newEvent.getBookingEmail() + "\n" + "start time: " + newEvent.getEventStartTime().toLocalDate() + "\n" + "duration: " + newEvent.getEventDuration() + " minutes" + "\n" + "notes: " + newEvent.getEventNotes());
        mailMessage.setSubject("Event Booking Confirmation");

        // Sending the mail
        javaMailSender.send(mailMessage);


        Event event = modelMapper.map(newEvent, Event.class);
//        if(email == null || email == newEvent.getBookingEmail()){
//            return repository.saveAndFlush(event);
//        }else{
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
        return repository.saveAndFlush(event);
    }


//    public static SimpleEventDTO convertEntityToDto(Event event) {
//        SimpleEventDTO simpleEventDTO = new SimpleEventDTO();
//        simpleEventDTO.setBookingName(event.getBookingName());
//        simpleEventDTO.setBookingEmail(event.getBookingEmail());
//        simpleEventDTO.setEventStartTime(event.getEventStartTime());
//        simpleEventDTO.setEventDuration(event.getEventDuration());
//        simpleEventDTO.setEventCategoryId(Integer.valueOf(event.getId() + " " + event.getEventCategory()));
//        return simpleEventDTO;
//    }


//    public SimpleEventDTO getSimpleEventById(Integer bookingId) {
//        Event event = repository.findById(bookingId).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, bookingId + " does not exist !"));
//        return modelMapper.map(event, SimpleEventDTO.class);
//    }

}