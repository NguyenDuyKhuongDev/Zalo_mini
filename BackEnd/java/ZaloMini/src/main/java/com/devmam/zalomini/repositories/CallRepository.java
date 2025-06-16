package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.Call;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface CallRepository extends JpaRepository<Call, Long>, JpaSpecificationExecutor<Call> {

    @Query("""
            SELECT c FROM Call c WHERE c.conversation.id = :conversationId
            AND c.status IN ('initiated', 'ringing', 'answered')
            ORDER BY c.startedAt DESC
            """)
    List<Call> findActiveCallsByConversationId(@Param("conversationId") Long conversationId);

    @Query("""
            SELECT c FROM Call c WHERE c.id = :userId OR 
            EXISTS (SELECT 1 FROM CallParticipant cp WHERE cp.call.id = c.id AND cp.user.id = :userId) 
            ORDER BY c.startedAt DESC
            """)
    Page<Call> findCallHistoryByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT c FROM Call c WHERE c.id = :callId AND 
            (c.caller.id = :userId OR EXISTS (SELECT 1 FROM CallParticipant cp WHERE cp.call.id = c.id AND cp.user.id = :userId))
            """)
    Optional<Call> findByIdAndUserId(@Param("callId") Long callId, @Param("userId") Long userId);
}