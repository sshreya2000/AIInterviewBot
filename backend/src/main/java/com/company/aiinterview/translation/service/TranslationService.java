package com.company.aiinterview.translation.service;

public interface TranslationService {
    com.company.aiinterview.translation.dto.response.TranslationResponseDto translate(com.company.aiinterview.translation.dto.request.TranslationRequestDto request);
}
