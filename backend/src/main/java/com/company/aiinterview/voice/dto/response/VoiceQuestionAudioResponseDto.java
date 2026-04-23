package com.company.aiinterview.voice.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VoiceQuestionAudioResponseDto { Long questionId; String audioUrl; String mimeType; }
