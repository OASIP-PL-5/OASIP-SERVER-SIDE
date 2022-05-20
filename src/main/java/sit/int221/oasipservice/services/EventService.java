package sit.int221.oasipservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.NewEventDTO;
import sit.int221.oasipservice.dtos.SimpleEventDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.repositories.EventRepository;
import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private ListMapper listMapper;


    public SimpleEventDTO getSimpleEventById(Integer bookingId) {
        Event event = repository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        , "Booking Id: " + bookingId + "doesn't exist"));
        return modelMapper.map(event, SimpleEventDTO.class);
    }

    public List<SimpleEventDTO> getEvents() {
        List<Event> eventList = repository.findAll();
        return listMapper.mapList(eventList, SimpleEventDTO.class, modelMapper);
    }

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

        Event event = modelMapper.map(newEvent, Event.class);
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
