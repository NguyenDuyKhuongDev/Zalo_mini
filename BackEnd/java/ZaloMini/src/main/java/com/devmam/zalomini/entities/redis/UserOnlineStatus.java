package com.devmam.zalomini.entities.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("user_online_status")
public class UserOnlineStatus  {
    @Id
    private String userId;

    private boolean online;
    private LocalDateTime lastSeen;
    private String deviceId;
    private String deviceType;
    private String socketId; // WebSocket connection ID

    @TimeToLive
    private Long timeToLive = 300L; // 5 phút TTL
}
