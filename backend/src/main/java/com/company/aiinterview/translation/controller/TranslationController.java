package com.company.aiinterview.translation.controller;

import com.company.aiinterview.translation.dto.request.TranslationRequestDto;
import com.company.aiinterview.translation.dto.response.TranslationResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/translation")
public class TranslationController {

    @PostMapping("/text")
    public ResponseEntity<TranslationResponseDto> translate(@RequestBody TranslationRequestDto request) {
        // TODO: Delegate translation to TranslationService.
        return ResponseEntity.ok(new TranslationResponseDto());
    }
}
