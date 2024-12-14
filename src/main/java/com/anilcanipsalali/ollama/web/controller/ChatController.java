package com.anilcanipsalali.ollama.web.controller;

import com.anilcanipsalali.ollama.service.ChatService;
import com.anilcanipsalali.ollama.web.model.ChatRequest;
import com.anilcanipsalali.ollama.web.model.ChatResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
        return chatService.chat(chatRequest);
    }

    @PostMapping("/stream")
    public Flux<String> chatWithStream(@RequestBody ChatRequest chatRequest) {
        return chatService.chatStream(chatRequest);
    }
}
