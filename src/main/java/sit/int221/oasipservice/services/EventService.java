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
               .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND
               ,"Booking Id: "+ bookingId + "doesn't exist"));
       return modelMapper.map(event,SimpleEventDTO.class);
    }

    public List<SimpleEventDTO> getEvents(){
        List<Event> eventList = repository.findAll();
        return listMapper.mapList(eventList, SimpleEventDTO.class, modelMapper);
    }

    public Event save(NewEventDTO newEvent){
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
