package com.company.aiinterview.voice.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SpeechToTextResponseDto { String transcript; String detectedLanguage; Double confidence; }
