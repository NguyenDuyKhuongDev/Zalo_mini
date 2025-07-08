package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long>, JpaSpecificationExecutor<MessageReaction> {
}