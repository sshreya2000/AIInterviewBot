package com.company.aiinterview.voice.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TextToSpeechResponseDto { String audioUrl; String format; Integer durationMs; }
