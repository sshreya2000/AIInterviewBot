package com.company.aiinterview.voice.client;

import org.springframework.stereotype.Component;
import java.nio.file.Path;

@Component
public class PiperClient {
    public Path synthesizeToWav(String text, String voiceModelPath) {
        // TODO: Invoke local piper command via ProcessBuilder and generate WAV file.
        return Path.of("./tmp/piper/output.wav");
    }
}
