package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long>, JpaSpecificationExecutor<ConversationParticipant> {
}