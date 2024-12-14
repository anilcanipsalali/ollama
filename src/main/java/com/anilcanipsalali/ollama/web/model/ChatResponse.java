package com.anilcanipsalali.ollama.web.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {

    private String response;
    private String conversationId;
}
