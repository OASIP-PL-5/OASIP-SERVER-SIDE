package sit.int221.oasipservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.oasipservice.dtos.FileDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.File;
import sit.int221.oasipservice.repositories.EventRepository;
import sit.int221.oasipservice.repositories.FileRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static java.lang.String.valueOf;

@Service
public class DBFileService {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private EventRepository eventRepository;

    public File storeFile(MultipartFile file, String eventStartTime) throws Exception {
        System.out.println("eventStartTime from form/data : " + eventStartTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(eventStartTime, formatter);
        System.out.println("eventStarttime from booking-event : " + startTime);
        List<Event> bookingId = eventRepository.findEventByStartTime(startTime);
        System.out.println("booking Id from eventStartTime : " + bookingId.get(0).getId());
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (fileName.contains("..")) {
                throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
            }
//constructor::  File(String id, String fileName,String fileType, byte[] data)
            File file1 = new File(bookingId.get(0).getId(), fileName, file.getContentType(), file.getBytes());
            return fileRepository.save(file1);
        } catch (IOException ex) {
            throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public File getFileById(String fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
    }

    public File getFileByBookingId(Integer bookingId) {
        return (File) fileRepository.getFileByBookingId(bookingId);
    }


//    public File updateFile(MultipartFile file, String eventStartTime) throws Exception {
//        System.out.println("eventStartTime from form/data : " + eventStartTime);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//        LocalDateTime startTime = LocalDateTime.parse(eventStartTime, formatter);
//        System.out.println("eventStarttime from booking-event : " + startTime);
//        List<Event> bookingId = eventRepository.findEventByStartTime(startTime);
//        System.out.println("booking Id from eventStartTime : " + bookingId.get(0).getId());
//        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//
//        try {
//            if (fileName.contains("..")) {
//                throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
//            }
//        }
//    }

    //    service for update-file
    public File updateFile(Integer bookingId, MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
            }
//            File file1 = new File(bookingId, fileName, file.getContentType(), file.getBytes());//<-- Id มันเป็นหาจาก bookingId
            File fileDetail = fileRepository.getFileByBookingId(bookingId);
            fileDetail.setFileName(fileName);
            fileDetail.setFileType(file.getContentType());
            fileDetail.setData(file.getBytes());

//            return (File) fileRepository.updateFile(fileId,fileName, file.getContentType(), file.getBytes());
            return fileRepository.saveAndFlush(fileDetail);
        } catch (Exception ex) {
            System.out.println(bookingId);
            System.out.println(fileName);
            System.out.println(file.getContentType());
            System.out.println(file.getBytes());
//            throw new Exception("Could not update file " + fileName + ". Please try again!", ex);
            throw new Exception("Could not update file" + fileName + ". Please try again!", ex);
        }
    }
}