package com.example.art_lover.metmuseum.dto;

import java.util.List;

public record MetSearchResponse(
    int total,
    List<Integer> objectIDs
) {}
 