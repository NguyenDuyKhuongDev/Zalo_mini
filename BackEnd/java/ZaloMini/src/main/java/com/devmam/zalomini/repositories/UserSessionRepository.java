package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserSessionRepository extends JpaRepository<UserSession, Long>, JpaSpecificationExecutor<UserSession> {
}