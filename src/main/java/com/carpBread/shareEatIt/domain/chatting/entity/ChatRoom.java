package com.carpBread.shareEatIt.domain.chatting.entity;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "CHAT_ROOM")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder @Getter
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ChatStatus status;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pt_id")
    private Participation participation;




}
