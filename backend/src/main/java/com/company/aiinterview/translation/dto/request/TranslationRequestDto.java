package com.company.aiinterview.translation.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TranslationRequestDto { String text; String sourceLanguage; String targetLanguage; String format; }
