package io.crud.portal.resource;

import io.crud.portal.constant.Constant;
import io.crud.portal.domain.Applicant;
import io.crud.portal.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/apply")
@RequiredArgsConstructor
public class ApplicantResource {
    private final ApplicantService applicantService;

    @PostMapping
    public ResponseEntity<Applicant> createApplicant(@RequestBody Applicant applicant) {
        Applicant createdApplicant = applicantService.createApplicant(applicant);
        URI location = URI.create(String.format("/apply/%s", createdApplicant.getId()));
        return ResponseEntity.created(location).body(createdApplicant);
    }

    @GetMapping
    public ResponseEntity<Page<Applicant>> getApplicants(@RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(applicantService.getAllApplicants(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Applicant> getApplicant(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(applicantService.getApplicant(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id, @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = applicantService.uploadPhoto(id, file);
            return ResponseEntity.ok().body(photoUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while uploading the photo");
        }
    }

    @PutMapping("/cv")
    public ResponseEntity<String> uploadCv(@RequestParam("id") String id, @RequestParam("file") MultipartFile file) {
        try {
            String cvUrl = applicantService.uploadCv(id, file);
            return ResponseEntity.ok().body(cvUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while uploading the CV");
        }
    }

    @GetMapping(path = "/image/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + "/" + filename));
    }

    @GetMapping(path = "/document/{filename}")
    public byte[] getCv(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + "/" + filename));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplicant(@PathVariable("id") String id) {
        applicantService.deleteApplicant(id);
        return ResponseEntity.noContent().build();
    }
}
