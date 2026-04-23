package com.company.aiinterview.translation.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TranslationResponseDto { String translatedText; String detectedLanguage; }
