package com.company.aiinterview.voice.controller;

import com.company.aiinterview.voice.dto.request.TextToSpeechRequestDto;
import com.company.aiinterview.voice.dto.response.SpeechToTextResponseDto;
import com.company.aiinterview.voice.dto.response.TextToSpeechResponseDto;
import com.company.aiinterview.voice.dto.response.VoiceQuestionAudioResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/voice")
public class VoiceController {

    @GetMapping("/interview/{sessionId}/question/{questionId}/audio")
    public ResponseEntity<VoiceQuestionAudioResponseDto> questionAudio(@PathVariable Long sessionId, @PathVariable Long questionId) {
        // TODO: Return synthesized question audio URL.
        return ResponseEntity.ok(new VoiceQuestionAudioResponseDto());
    }

    @PostMapping("/interview/{sessionId}/answer/{questionId}/audio")
    public ResponseEntity<SpeechToTextResponseDto> uploadAnswerAudio(@PathVariable Long sessionId,
                                                                      @PathVariable Long questionId,
                                                                      @RequestPart("file") MultipartFile file) {
        // TODO: STT + evaluation handoff.
        return ResponseEntity.ok(new SpeechToTextResponseDto());
    }

    @PostMapping("/stt")
    public ResponseEntity<SpeechToTextResponseDto> stt(@RequestPart("file") MultipartFile file) {
        // TODO: Generic STT endpoint.
        return ResponseEntity.ok(new SpeechToTextResponseDto());
    }

    @PostMapping("/tts")
    public ResponseEntity<TextToSpeechResponseDto> tts(@RequestBody TextToSpeechRequestDto request) {
        // TODO: Generic TTS endpoint.
        return ResponseEntity.ok(new TextToSpeechResponseDto());
    }
}
