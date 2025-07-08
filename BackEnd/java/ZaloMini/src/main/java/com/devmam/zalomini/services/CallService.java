package com.devmam.zalomini.services;

import com.devmam.zalomini.dto.request.CallAccept;
import com.devmam.zalomini.dto.request.CallRequest;
import com.devmam.zalomini.dto.response.CallParticipantResponse;
import com.devmam.zalomini.dto.response.CallResponse;
import com.devmam.zalomini.entities.Call;
import com.devmam.zalomini.entities.CallParticipant;
import org.springframework.http.ResponseEntity;

public interface CallService {

    //Services
    ResponseEntity<?> initiateCall(CallRequest callRequest, String authHeader);
    ResponseEntity<?> answerCall(CallAccept callAccept, String authHeader);
    void declineCall(Long callId, Long userId);
    void endCall(Long callId, Long userId);
    void joinCall(Long callId, Long userId);
    void sendCallInvitation(Long callId, Long userId);
    void notifyParticipantJoined(Long callId, Long userId);
    void notifyParticipantLeft(Long callId, Long userId);
    void checkAndEndCallIfAllDeclined(Long callId);
    void checkAndEndCallIfEmpty(Long callId);

    //Mappers
    CallResponse mapToCallResponse(Call call);
    CallParticipantResponse mapToParticipantResponse(CallParticipant participant);

}
