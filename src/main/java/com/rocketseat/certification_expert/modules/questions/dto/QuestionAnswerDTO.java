package com.rocketseat.certification_expert.modules.questions.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDTO {
  
  private UUID questionId;
  private UUID alternativeId;
  private boolean isCorrect;
}
