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
import javax.validation.Valid;
import java.time.LocalDateTime;


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
    public Response uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("eventStartTime") String eventStartTime) throws Exception {
        File fileName = dbFileService.storeFile(file,eventStartTime);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(fileName.getId())
                .toUriString();

        return new Response(fileName.getFileName(), fileDownloadUri, file.getContentType(), file.getSize());

    }

//---------------------------------------------------------------
    // test post man ใส่ /downloadFile/<fileId>
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

    //    Delete
    @DeleteMapping("/delete/{fileId}")
    public void deleteFile(@PathVariable String fileId) {
        fileRepository.findById(fileId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, fileId + " does not exist !"));
        fileRepository.deleteById(fileId);
    }


}
