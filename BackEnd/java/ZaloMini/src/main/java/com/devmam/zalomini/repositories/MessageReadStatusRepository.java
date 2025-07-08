package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.MessageReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long>, JpaSpecificationExecutor<MessageReadStatus> {
}