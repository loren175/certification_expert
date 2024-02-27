package com.rocketseat.certification_expert.modules.students.useCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.certification_expert.modules.students.dto.VerifyHasCertificationDTO;
import com.rocketseat.certification_expert.modules.students.repositories.CertificationStudentRepository;

@Service
public class VerifyIfHasCertificationUseCase {
  
  @Autowired
  private CertificationStudentRepository certificationStudentRepository;

  public boolean execute( VerifyHasCertificationDTO dto ) {
    var result = this.certificationStudentRepository.findByStudentEmailAndTechnology(dto.getEmail(), dto.getTechnology());
    if(!result.isEmpty()) {
      return true;
    }
    return false;
  }
}
