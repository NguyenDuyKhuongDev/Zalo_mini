package com.devmam.zalomini.entities.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("group_online_info")
public class GroupOnlineInfo {

    @Id
    private String groupId;

    private Set<String> onlineMembers;
    private int totalMembers;
    private LocalDateTime lastActivity;
    private String lastMessageId;
}
