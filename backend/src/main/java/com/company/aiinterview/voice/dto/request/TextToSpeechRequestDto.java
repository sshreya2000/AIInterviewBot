package com.company.aiinterview.voice.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TextToSpeechRequestDto { String text; String languageCode; String voice; }
