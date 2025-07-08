package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.ConversationTagAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConversationTagAssignmentRepository extends JpaRepository<ConversationTagAssignment, Long>, JpaSpecificationExecutor<ConversationTagAssignment> {
}