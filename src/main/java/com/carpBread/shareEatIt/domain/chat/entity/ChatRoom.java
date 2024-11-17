package com.carpBread.shareEatIt.domain.chat.entity;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "CHAT_ROOM")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "chatroom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "participation")
    private Participation participation;

    @Enumerated(value = EnumType.STRING)
    private ChatRoomStatus status;

    // 채팅방 상태 변경
    public void updateStatus(){
        this.status = ChatRoomStatus.INACTIVE;
    }
}
