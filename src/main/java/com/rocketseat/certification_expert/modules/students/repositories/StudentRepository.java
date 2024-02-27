package com.rocketseat.certification_expert.modules.students.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rocketseat.certification_expert.modules.students.entities.StudentEntity;

import java.util.Optional;
import java.util.UUID;


public interface StudentRepository extends JpaRepository<StudentEntity, UUID> {
  
  public Optional<StudentEntity> findByEmail(String email);
}
