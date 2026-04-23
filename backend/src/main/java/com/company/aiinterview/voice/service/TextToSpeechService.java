package com.company.aiinterview.voice.service;

public interface TextToSpeechService {
    com.company.aiinterview.voice.dto.response.TextToSpeechResponseDto synthesize(com.company.aiinterview.voice.dto.request.TextToSpeechRequestDto request);
}
