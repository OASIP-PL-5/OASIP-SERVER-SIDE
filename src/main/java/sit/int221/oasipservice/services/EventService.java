package sit.int221.oasipservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.SimpleEventDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.repositories.EventRepository;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public SimpleEventDTO getSimpleEventById(Integer bookingId) {
        Event booking = repository.findById(bookingId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, bookingId + " does not exist !"));
        return modelMapper.map(booking, SimpleEventDTO.class);
    }

}
