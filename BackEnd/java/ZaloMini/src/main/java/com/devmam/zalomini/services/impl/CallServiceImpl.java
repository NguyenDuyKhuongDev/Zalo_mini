package com.devmam.zalomini.services.impl;

import com.devmam.zalomini.dto.request.CallAccept;
import com.devmam.zalomini.dto.request.CallRequest;
import com.devmam.zalomini.dto.response.CallParticipantResponse;
import com.devmam.zalomini.dto.response.CallResponse;
import com.devmam.zalomini.dto.response.ResponseData;
import com.devmam.zalomini.entities.Call;
import com.devmam.zalomini.entities.CallParticipant;
import com.devmam.zalomini.entities.Conversation;
import com.devmam.zalomini.entities.User;
import com.devmam.zalomini.exception.customizeException.CallException;
import com.devmam.zalomini.exception.customizeException.UnAuthenticationException;
import com.devmam.zalomini.repositories.*;
import com.devmam.zalomini.services.CallService;
import com.devmam.zalomini.utils.JwtUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CallServiceImpl implements CallService {
    @Autowired
    private CallRepository callRepository;

    @Autowired
    private CallParticipantRepository participantRepository;

    @Autowired
    private ConversationParticipantRepository conversationParticipantRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    //    @Autowired
//    private WebRTCService webRTCService;
//
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Override
    public ResponseEntity<?> initiateCall(CallRequest callRequest, String authHeader) {
        //Kiểm tra token còn hợp lệ không
        String token = jwtUtil.getTokenFromAuthHeader(authHeader);
        if (token == null) throw new UnAuthenticationException("Không có quyền truy cập");

        //Kiểm tra cuộc gọi đang diễn ra không
        List<Call> activeCalls = callRepository.findActiveCallsByConversationId(callRequest.getConversationId());
        if (!activeCalls.isEmpty()) {
            throw new CallException("Cuộc gọi đang diễn ra trong cuộc trò chuyện này", callRequest);
        }

        //Kiểm tra cuộc trò chuyện có tồn tại không
        Conversation conversation = conversationRepository.findById(callRequest.getConversationId())
                .orElseThrow(() -> new CallException("Không tìm thấy cuộc trò chuyện", callRequest));

        //Kiểm tra người dùng có tồn tại không
        JWTClaimsSet jwtClaimsSet = jwtUtil.getClaimsFromToken(token);
        Long userId = Long.parseLong((String) jwtClaimsSet.getClaims().get("id"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CallException("Người dùng không hợp lệ"));

        //Tạo cuộc gọi
        Call call = Call.builder()
                .conversation(conversation)
                .caller(user)
                .callType(callRequest.getCallType())
                .status("initiated")
                .build();
        callRepository.save(call);
        return ResponseEntity.ok(
                ResponseData.builder()
                        .status(200)
                        .message("Tạo cuộc gọi thành công")
                        .data(CallResponse.mapToCallResponse(call))
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> answerCall(CallAccept callAccept, String authHeader) {
        String token = jwtUtil.getTokenFromAuthHeader(authHeader);
        if (token == null) throw new UnAuthenticationException("Không có quyền truy cập");

        Call call = callRepository.findById(callAccept.getCallId())
                .orElseThrow(() -> new CallException("Không tìm thấy cuộc gọi", callAccept));
        if (!call.getStatus().equals("ringing")) {
            throw new CallException("Cuộc gọi không ở trạng thái có thể trả lời");
        }
        // Cập nhật trạng thái call participant
        JWTClaimsSet jwtClaimsSet = jwtUtil.getClaimsFromToken(token);
        Long userId = Long.parseLong((String) jwtClaimsSet.getClaims().get("id"));
        CallParticipant participant = participantRepository.findByCallIdAndUserId(callAccept.getCallId(), userId)
                .orElseThrow(() -> new CallException("Bạn không được mời tham gia cuộc gọi này"));
        participant.setStatus("joined");
        participant.setJoinedAt(new Date());
        participantRepository.save(participant);

        // Cập nhật trạng thái cuộc gọi
        call.setStatus("answered");
        call.setAnsweredAt(new Date());
        callRepository.save(call);
        return ResponseEntity.ok(
                ResponseData.builder()
                        .status(200)
                        .message("Trả lời cuộc gọi thành công")
                        .data(CallResponse.mapToCallResponse(call))
                        .build()
        );
    }

    @Override
    public void declineCall(Long callId, Long userId) {
        CallParticipant participant = participantRepository.findByCallIdAndUserId(callId, userId)
                .orElseThrow(() -> new CallException("Không tìm thấy thông tin tham gia cuộc gọi"));

    }

    @Override
    public void endCall(Long callId, Long userId) {

    }

    @Override
    public void joinCall(Long callId, Long userId) {

    }

    @Override
    public void sendCallInvitation(Long callId, Long userId) {

    }

    @Override
    public void notifyParticipantJoined(Long callId, Long userId) {

    }

    @Override
    public void notifyParticipantLeft(Long callId, Long userId) {

    }

    @Override
    public void checkAndEndCallIfAllDeclined(Long callId) {

    }

    @Override
    public void checkAndEndCallIfEmpty(Long callId) {

    }

    @Override
    public CallResponse mapToCallResponse(Call call) {
        return null;
    }

    @Override
    public CallParticipantResponse mapToParticipantResponse(CallParticipant participant) {
        return null;
    }


}
