package com.carpBread.shareEatIt.domain.chat.repository;

import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 사용자가 참여한 채팅방 목록 조회
    // 사용자가 == giver 이면서 giverStatus가 true이거나, receiver이면서 receiverStatus= true인 참여 객체의 채팅방 조회
    @Query("SELECT c FROM ChatRoom c WHERE (c.participation.giver.id = :memberId AND c.participation.isGiverInChat = true) OR (c.participation.receiver.id = :memberId AND c.participation.isReceiverInChat = true)")
    List<ChatRoom> findByUserAndStatus(@Param("memberId") Long memberId);

    // 사용자가 해당 채팅방에 속한 사람인지 확인
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM ChatRoom c " +
            "WHERE c.id = :chatRoomId " +
            "AND (c.participation.giver.id = :memberId OR c.participation.receiver.id = :memberId)")
    boolean existsByMemberInChatRoom(@Param("memberId") Long memberId, @Param("chatRoomId") Long chatRoomId);

    // ptId로 조회
    @Query("SELECT c FROM ChatRoom c WHERE c.participation.id = :ptId")
    ChatRoom findByParticipationId(@Param("ptId") Long ptId);

}



