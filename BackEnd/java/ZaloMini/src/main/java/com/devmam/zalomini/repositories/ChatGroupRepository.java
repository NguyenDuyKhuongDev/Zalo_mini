package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long>, JpaSpecificationExecutor<ChatGroup> {
}