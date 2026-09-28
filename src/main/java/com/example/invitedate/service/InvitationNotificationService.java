package com.example.invitedate.service;

import com.example.invitedate.entity.Invitation;
import com.example.invitedate.enums.InvitationStatus;
import java.util.Map;

public interface InvitationNotificationService {
    void sendResponse(Invitation invitation, InvitationStatus status, Map<String, Object> details, String ticketCode);
}
