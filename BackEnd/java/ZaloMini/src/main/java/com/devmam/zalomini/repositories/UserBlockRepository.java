package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long>, JpaSpecificationExecutor<UserBlock> {
}