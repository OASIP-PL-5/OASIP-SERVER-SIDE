package sit.int221.oasipservice.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sit.int221.oasipservice.entities.File;
import sit.int221.oasipservice.payload.Response;
import sit.int221.oasipservice.repositories.FileRepository;
import sit.int221.oasipservice.services.DBFileService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/files")
public class FilesController {

    @Autowired
    private DBFileService dbFileService;
    @Autowired
    private FileRepository fileRepository;

//    public FileUploadController(DBFileService dbFileService) {
//        this.dbFileService = dbFileService;
//    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public Response uploadFile(@RequestParam("file") MultipartFile file,
                               @RequestParam("eventStartTime") String eventStartTime) throws Exception {
        System.out.println("size of file: "+file.getSize());
        File fileName = dbFileService.storeFile(file,eventStartTime);


        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(fileName.getId())
                .toUriString();

        return new Response(fileName.getFileName(), fileDownloadUri, file.getContentType(), file.getSize());

    }

    //---------------------------------------------------------------
    @GetMapping("")
    public List<File> getAllFile(){
        return fileRepository.findAll();
    }

//    // test post man ใส่ /downloadFile/<fileId>
//    @GetMapping("/download/{fileId:.+}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, HttpServletRequest request) {
//        // Load file as Resource
//        File file = dbFileService.getFileById(fileId);
//
//
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(file.getFileType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
//                .body(new ByteArrayResource(file.getData()));
//    }

//    getFileById
    @GetMapping("/{bookingId}")
    public File getFileByBookingId(@PathVariable Integer bookingId, HttpServletRequest request) {
        // Load file as Resource
        File file = fileRepository.getFileByBookingId(bookingId);
        if (file == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return fileRepository.getFileByBookingId(bookingId);
    }

//    download file เราจะ download โดยใช้ bookingId
    @GetMapping("/download/{fileId:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, HttpServletRequest request) {
        // Load file as Resource
        File file = dbFileService.getFileById(fileId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(new ByteArrayResource(file.getData()));




    }


    //---------------------------------------------------------------
    //Patch
    @PatchMapping("/update/{bookingId}")
    public void updateFile(@PathVariable Integer bookingId,@RequestParam("file") MultipartFile file) throws Exception {
        File getFile = dbFileService.getFileByBookingId(bookingId);

        if (getFile == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"File not found");
        }
        dbFileService.updateFile(bookingId,file);
    }


//---------------------------------------------------------------

    //    Delete
    @DeleteMapping("/delete/{fileId}")
    public void deleteFile(@PathVariable String fileId) {
        fileRepository.findById(fileId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, fileId + " does not exist !"));
        fileRepository.deleteById(fileId);
    }


}