package com.example.invitedate.dto;

import com.example.invitedate.enums.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.Map;

public record PublicInvitationResponse(UUID id, String senderName, String receiverName, DateType dateType,
                                       OffsetDateTime proposedDate, String customMessage, InvitationStatus status,
                                       Map<String, Object> experience, OffsetDateTime expiresAt) { }
