package com.carpBread.shareEatIt.domain.chat.entity;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "chat_room")
@NoArgsConstructor
@SuperBuilder
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "chatroom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id")
    private Participation participation;

    @Enumerated(value = EnumType.STRING)
    private ChatRoomStatus status;

    public ChatRoom(BaseEntityBuilder<?, ?> b, Long id, Participation participation, ChatRoomStatus status) {
        super(b);
        this.id = id;
        this.participation = participation;
        this.status = status;
    }

    // 채팅방 상태 변경
    public void updateStatus(){
        this.status = ChatRoomStatus.INACTIVE;
    }
}
