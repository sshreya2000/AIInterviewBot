package com.company.aiinterview.voice.service;

public interface VoiceInterviewService {
    com.company.aiinterview.voice.dto.response.SpeechToTextResponseDto submitVoiceAnswer(Long sessionId, Long questionId, org.springframework.web.multipart.MultipartFile audioFile, String languageCode);
    com.company.aiinterview.voice.dto.response.VoiceQuestionAudioResponseDto getQuestionAudio(Long sessionId, Long questionId, String languageCode);
}
