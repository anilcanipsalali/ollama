package com.anilcanipsalali.ollama.service;

import com.anilcanipsalali.ollama.web.model.ChatRequest;
import com.anilcanipsalali.ollama.web.model.ChatResponse;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Service
public class ChatService {

    private final ChatClient chatClient;

    @Value("${dev.ollama.chat-memory-retrieve-size-key}")
    private Integer chatMemoryRetrieveSizeKey;

    public ChatService(ChatClient.Builder builder) {
        InMemoryChatMemory inMemoryChatMemory = new InMemoryChatMemory();
        this.chatClient = builder.defaultAdvisors(
                new PromptChatMemoryAdvisor(inMemoryChatMemory),
                new MessageChatMemoryAdvisor(inMemoryChatMemory)
        ).build();
    }

    public ChatResponse chat(ChatRequest chatRequest) {
        if (StringUtils.isEmpty(chatRequest.getConversationId())) {
            chatRequest.setConversationId(UUID.randomUUID().toString());
        }

        String content = chatClient.prompt()
                .user(chatRequest.getMessage())
                .advisors(advisorSpec -> advisorSpec
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatRequest.getConversationId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, chatMemoryRetrieveSizeKey)
                )
                .call()
                .content();

        return ChatResponse.builder().response(content).conversationId(chatRequest.getConversationId()).build();
    }

    public Flux<String> chatStream(ChatRequest chatRequest) {
        return chatClient.prompt()
                .user(chatRequest.getMessage())
                .advisors(advisorSpec -> advisorSpec
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatRequest.getConversationId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, chatMemoryRetrieveSizeKey)
                )
                .stream()
                .content();
    }
}
