package com.company.aiinterview.translation.client;

import com.company.aiinterview.translation.dto.request.TranslationRequestDto;
import com.company.aiinterview.translation.dto.response.TranslationResponseDto;
import org.springframework.stereotype.Component;

@Component
public class LibreTranslateClient {
    public TranslationResponseDto translate(TranslationRequestDto request) {
        // TODO: Call LibreTranslate REST API via RestTemplate/WebClient.
        return new TranslationResponseDto();
    }
}
