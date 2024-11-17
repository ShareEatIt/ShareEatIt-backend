package com.carpBread.shareEatIt.domain.chat.repository;

import com.carpBread.shareEatIt.domain.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatRoomId(Long chatRoomId);

}
