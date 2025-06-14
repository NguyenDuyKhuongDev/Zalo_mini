package com.devmam.zalomini.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallRequest {
    @NonNull
    Long conversationId;

    @NotBlank
    @Pattern(regexp = "^(voice|video)$", message = "Hệ thống chỉ cung cấp dịch vụ gọi video và gọi voice")
    String callType;
}