package io.crud.portal.repository;

import io.crud.portal.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicantRepo extends JpaRepository<Applicant, String> {
    Optional<Applicant> findById(String id);
}