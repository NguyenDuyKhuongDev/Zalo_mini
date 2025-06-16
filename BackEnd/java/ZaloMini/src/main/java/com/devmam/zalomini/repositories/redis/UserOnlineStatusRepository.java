package com.devmam.zalomini.repositories.redis;

import com.devmam.zalomini.entities.redis.UserOnlineStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOnlineStatusRepository extends CrudRepository<UserOnlineStatus, String> {

}
