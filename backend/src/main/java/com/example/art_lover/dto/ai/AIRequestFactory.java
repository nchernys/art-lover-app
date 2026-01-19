package com.example.art_lover.dto.ai;

import java.util.List;

import com.example.art_lover.dto.ai.*;

public class AIRequestFactory {

    private AIRequestFactory() {
    }

    public static AIRequest fromPrompt(String prompt) {
        return new AIRequest(
                List.of(
                        new AIRequest.Content(
                                List.of(new AIRequest.Part(prompt)))));
    }
}
