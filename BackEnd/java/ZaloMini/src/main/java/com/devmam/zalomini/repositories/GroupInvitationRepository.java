package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long>, JpaSpecificationExecutor<GroupInvitation> {
}