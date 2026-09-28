package com.example.invitedate.service.impl;

import com.example.invitedate.entity.Invitation;
import com.example.invitedate.enums.InvitationStatus;
import com.example.invitedate.service.InvitationNotificationService;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Keeps invitation delivery available locally when SMTP is not configured. */
@Service
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoopInvitationNotificationService implements InvitationNotificationService {
    @Override public void sendResponse(Invitation invitation, InvitationStatus status, Map<String, Object> details, String ticketCode) { }
}
