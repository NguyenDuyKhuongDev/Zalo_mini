package com.devmam.zalomini.dto.response;

import com.devmam.zalomini.entities.Call;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallResponse implements Serializable {
    private Long id;
    private Long conversationId;
    private Long callerId;
    private String callType;
    private String status;
    private Date startedAt;
    private Date answeredAt;
    private Date endedAt;
    private Integer duration;
    private List<CallParticipantResponse> participants;

    public static  CallResponse mapToCallResponse(Call call) {
        return CallResponse.builder()
                .id(call.getId())
                .conversationId(call.getConversation().getId())
                .callerId(call.getCaller().getId())
                .callType(call.getCallType())
                .status(call.getStatus())
                .startedAt(call.getStartedAt())
                .answeredAt(call.getAnsweredAt())
                .endedAt(call.getEndedAt())
                .duration(call.getDuration())
                .participants(call.getParticipants().stream()
                        .map(CallParticipantResponse::mapToParticipantResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
