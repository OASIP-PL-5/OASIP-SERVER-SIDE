package sit.int221.oasipservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.EventDTO;
import sit.int221.oasipservice.dtos.NewEventDTO;
import sit.int221.oasipservice.dtos.UserDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.EventCategoryOwnerRepository;
import sit.int221.oasipservice.repositories.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    private EventCategoryOwnerRepository eventCategoryOwnerRepository;

    @Autowired
    private ListMapper listMapper;

    // service: get-all-events
    public List<EventDTO> getAllEventByDTO() {
        return repository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
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
        return repository.getDetailByUserIdAndBookingId(userId,bookingId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    //    service: filter-by-eventCategoryId
    public List<EventDTO> getByEventCategory(Integer eventCategoryId) {

        return repository.getByEventCategory(eventCategoryId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

//service: get-by-eventcategory (role:lecturer)
    public List<EventDTO> getByEventCategoryLecturer(Integer eventCategoryId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (repository.getByEventCategory(eventCategoryId).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getByEventCategory(eventCategoryId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

//service: สำหรับเรียก event-lists ที่สัมพันธ์กับ lecturer
    public List<EventDTO> getEventsFromLecturerId(Integer userId){
        return repository.findEventsFromUserId(userId).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }


    //    service: filter-by-upcoming
    public List<EventDTO> getEventsByUpcoming(){
        if (repository.getEventsByUpcoming().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByUpcoming().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    //    service: filter-by-past
    public List<EventDTO> getEventsByPast(){
        if (repository.getEventsByPast().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByPast().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    //    service: filter-by-specificDate
    public List<EventDTO> getEventsByEventStartTime(String eventStartTime){
        if (repository.getEventsByEventStartTime(eventStartTime).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return repository.getEventsByEventStartTime(eventStartTime).stream().map(this::convertEntityToDto).collect(Collectors.toList());
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