package com.company.aiinterview.voice.client;

import org.springframework.stereotype.Component;
import java.nio.file.Path;

@Component
public class WhisperClient {
    public String transcribeAudio(Path audioPath, String languageCode) {
        // TODO: Invoke local whisper command via ProcessBuilder and parse transcript output.
        return "";
    }
}
