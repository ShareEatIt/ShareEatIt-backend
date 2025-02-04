package com.carpBread.shareEatIt.domain.chat.entity;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.context.annotation.Profile;

@Entity
@Table(name = "chat_room")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
//@Profile("!test") // 유진: test 시 application-test 프로퍼티에 불러오지 않는 빈으로 지정 (mongodb 사용)
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "chatroom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pt_id")
    private Participation participation;

    @Enumerated(value = EnumType.STRING)
    private ChatRoomStatus status;

    // 채팅방 상태 변경
    public void updateStatus(){
        this.status = ChatRoomStatus.INACTIVE;
    }
}
