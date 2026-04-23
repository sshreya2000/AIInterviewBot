package com.company.aiinterview.voice.service;

public interface SpeechToTextService {
    com.company.aiinterview.voice.dto.response.SpeechToTextResponseDto transcribe(org.springframework.web.multipart.MultipartFile audioFile, String languageCode);
}
