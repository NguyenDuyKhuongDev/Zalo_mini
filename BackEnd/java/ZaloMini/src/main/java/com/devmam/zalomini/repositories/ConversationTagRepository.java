package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.ConversationTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConversationTagRepository extends JpaRepository<ConversationTag, Long>, JpaSpecificationExecutor<ConversationTag> {
}