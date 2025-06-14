package com.devmam.zalomini.services;


import com.devmam.zalomini.dto.request.WebRTCSignal;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface WebRTCService {
    Map<Long, Set<String>> callSessions = new ConcurrentHashMap<>();
    void handleWebRTCSignal(Long callId, Long userId, WebRTCSignal signal);
    void forwardSignalToParticipants(Long callId, Long fromUserId, WebRTCSignal signal);
    void cleanupCall(Long callId);
    void removeUserFromCall(Long callId, Long userId);
}
