package com.company.aiinterview.voice.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VoiceAnswerUploadRequestDto { Long questionId; String languageCode; }
