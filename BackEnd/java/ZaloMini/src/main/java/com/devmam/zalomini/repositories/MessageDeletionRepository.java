package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.MessageDeletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageDeletionRepository extends JpaRepository<MessageDeletion, Long>, JpaSpecificationExecutor<MessageDeletion> {
}