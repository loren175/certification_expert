package com.rocketseat.certification_expert.modules.students.useCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.certification_expert.modules.questions.repositories.QuestionRepository;
import com.rocketseat.certification_expert.modules.students.dto.StudentCertificationAnswerDTO;
import com.rocketseat.certification_expert.modules.students.dto.VerifyHasCertificationDTO;
import com.rocketseat.certification_expert.modules.students.entities.AnswersCertificationsEntity;
import com.rocketseat.certification_expert.modules.students.entities.CertificationStudentEntity;
import com.rocketseat.certification_expert.modules.students.entities.StudentEntity;
import com.rocketseat.certification_expert.modules.students.repositories.CertificationStudentRepository;
import com.rocketseat.certification_expert.modules.students.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.rocketseat.certification_expert.modules.questions.entities.QuestionEntity;

@Service
public class StudentCertificationAnswersUseCase {

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private CertificationStudentRepository certificationStudentRepository;

  @Autowired
  private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

  public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {
    var hasCertification = this.verifyIfHasCertificationUseCase.execute(new VerifyHasCertificationDTO(dto.getEmail(), dto.getTechnology()));

    if(hasCertification) {
      throw new Exception("You already got your certification");
    }

    List<QuestionEntity> questionEntity = questionRepository.findByTechnology(dto.getTechnology());
    List<AnswersCertificationsEntity> answersCertifications = new ArrayList<>();

    AtomicInteger correctAnswers = new AtomicInteger(0);

    dto.getQuestionsAnswers().stream().forEach(questionAnswer -> {
      var question = questionEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionId())).findFirst()
          .get();

      var findCorrectAlternative = question.getAlternatives().stream().filter(alternative -> alternative.isCorrect())
          .findFirst().get();

      if (findCorrectAlternative.getId().equals(questionAnswer.getAlternativeId())) {
        questionAnswer.setCorrect(true);
        correctAnswers.incrementAndGet();
      } else {
        questionAnswer.setCorrect(false);
      }

      var answersCertificationsEntity = AnswersCertificationsEntity.builder()
          .answerId(questionAnswer.getAlternativeId()).questionId(questionAnswer.getQuestionId())
          .isCorrect(questionAnswer.isCorrect()).build();

      answersCertifications.add(answersCertificationsEntity);
    });

    var student = studentRepository.findByEmail(dto.getEmail());
    UUID studentId;
    if (student.isEmpty()) {
      var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
      studentRepository.save(studentCreated);
      studentId = studentCreated.getId();
    } else {
      studentId = student.get().getId();
    }

    CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder()
        .technology(dto.getTechnology()).studentID(studentId).grade(correctAnswers.get()).build();

    var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

    answersCertifications.stream().forEach(answerCertification -> {
      answerCertification.setCertificationId(certificationStudentEntity.getId());
      answerCertification.setCertificationStudentEntity(certificationStudentEntity);
    });

    certificationStudentEntity.setAnswersCertificationsEntity(answersCertifications);

    certificationStudentRepository.save(certificationStudentEntity);

    return certificationStudentCreated;
  }
}