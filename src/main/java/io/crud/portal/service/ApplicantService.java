package io.crud.portal.service;

import io.crud.portal.constant.Constant;
import io.crud.portal.domain.Applicant;
import io.crud.portal.repo.ApplicantRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ApplicantService {
    private final ApplicantRepo applicantRepo;

    public Page<Applicant> getAllApplicants(int page, int size) {
        return applicantRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Applicant getApplicant(String id) {
        return applicantRepo.findById(id).orElseThrow(() -> new RuntimeException("Applicant not found"));
    }

    public Applicant createApplicant(Applicant applicant) {
        return applicantRepo.save(applicant);
    }

    public void deleteApplicant(String id) {
        log.info("Deleting applicant with ID: {}", id);
        Applicant applicant = getApplicant(id);

        if (applicant.getPhotoUrl() != null && !applicant.getPhotoUrl().isEmpty()) {
            String filename = applicant.getPhotoUrl().substring(applicant.getPhotoUrl().lastIndexOf("/") + 1);
            Path fileStorageLocation = Paths.get(Constant.PHOTO_DIRECTORY).toAbsolutePath().normalize();
            Path filePath = fileStorageLocation.resolve(filename);
            try {
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                log.error("Error occurred while deleting photo", e);
                throw new RuntimeException("Unable to delete applicant photo", e);
            }
        }

        if (applicant.getCvUrl() != null && !applicant.getCvUrl().isEmpty()) {
            String filename = applicant.getCvUrl().substring(applicant.getCvUrl().lastIndexOf("/") + 1);
            Path fileStorageLocation = Paths.get(Constant.PHOTO_DIRECTORY).toAbsolutePath().normalize();
            Path filePath = fileStorageLocation.resolve(filename);
            try {
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                log.error("Error occurred while deleting CV", e);
                throw new RuntimeException("Unable to delete applicant CV", e);
            }
        }

        applicantRepo.delete(applicant);
    }

    public String uploadPhoto(String id, MultipartFile file) {
        log.info("Saving picture for user ID: {}", id);
        Applicant applicant = getApplicant(id);
        String photoUrl = photoFunction.apply(id, file);
        applicant.setPhotoUrl(photoUrl);
        applicantRepo.save(applicant);
        return photoUrl;
    }

    public String uploadCv(String id, MultipartFile file) {
        log.info("Saving CV for user ID: {}", id);
        Applicant applicant = getApplicant(id);
        String cvUrl = cvFunction.apply(id, file);
        applicant.setCvUrl(cvUrl);
        applicantRepo.save(applicant);
        return cvUrl;
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(Constant.PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/apply/image/" + filename).toUriString();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to save image");
        }
    };

    private final BiFunction<String, MultipartFile, String> cvFunction = (id, file) -> {
        String filename = id + "-cv" + fileExtension.apply(file.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(Constant.PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(file.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/apply/document/" + filename).toUriString();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to save CV");
        }
    };
}
