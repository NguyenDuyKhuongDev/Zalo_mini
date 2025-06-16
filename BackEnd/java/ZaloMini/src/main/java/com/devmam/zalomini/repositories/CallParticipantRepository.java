package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.CallParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CallParticipantRepository extends JpaRepository<CallParticipant, Long>, JpaSpecificationExecutor<CallParticipant> {
    List<CallParticipant> findByCallIdAndStatus(Long callId, String status);

    Optional<CallParticipant> findByCallIdAndUserId(Long callId, Long userId);

    @Query("SELECT cp FROM CallParticipant cp WHERE cp.call.id = :callId AND cp.status = 'JOINED'")
    List<CallParticipant> findActiveParticipants(@Param("callId") Long callId);
}