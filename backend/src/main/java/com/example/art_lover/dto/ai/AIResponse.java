package com.example.art_lover.dto.ai;

import java.util.List;

public record AIResponse(
    List<Candidate> candidates
) {
    public record Candidate(
        Content content
    ) {}

    public record Content(
        List<Part> parts
    ) {}

    public record Part(
            String text) {
    }

    public String firstText() {
    return candidates.get(0).content().parts().get(0).text();
}
}
