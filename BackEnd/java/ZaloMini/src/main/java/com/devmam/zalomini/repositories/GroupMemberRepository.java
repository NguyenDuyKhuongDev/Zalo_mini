package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long>, JpaSpecificationExecutor<GroupMember> {
}