package com.devmam.zalomini.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebRTCSignal {
    String type; // "offer", "answer", "ice-candidate"
    Object data; // SDP hoặc ICE candidate data
    Long targetUserId; // Optional: user cụ thể để gửi signal
}
