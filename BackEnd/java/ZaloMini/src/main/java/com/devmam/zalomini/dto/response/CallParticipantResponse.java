package com.devmam.zalomini.dto.response;

import com.devmam.zalomini.entities.CallParticipant;
import com.devmam.zalomini.entities.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallParticipantResponse {
    Long userId;
    String username;
    String displayName;
    String avatarUrl;
    String status;
    Date joinedAt;
    Date leftAt;

    public static CallParticipantResponse mapToParticipantResponse(CallParticipant participant) {
        User user = participant.getUser(); // Assuming User entity is loaded
        return CallParticipantResponse.builder()
                .userId(participant.getUser().getId())
                .username(user != null ? user.getUsername() : null)
                .displayName(user != null ? user.getDisplayName() : null)
                .avatarUrl(user != null ? user.getAvatarUrl() : null)
                .status(participant.getStatus())
                .joinedAt(participant.getJoinedAt())
                .leftAt(participant.getLeftAt())
                .build();
    }
}
