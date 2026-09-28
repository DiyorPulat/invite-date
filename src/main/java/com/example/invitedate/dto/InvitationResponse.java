package com.example.invitedate.dto;

import com.example.invitedate.enums.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record InvitationResponse(UUID id, String senderName, String senderEmail, String receiverName, DateType dateType,
                                 OffsetDateTime proposedDate, String customMessage, InvitationStatus status,
                                 Map<String, Object> responseDetails, int viewCount, OffsetDateTime createdAt,
                                 OffsetDateTime updatedAt, String invitationUrl, String qrCodeDataUrl,
                                 Map<String, Object> experience, String ownerUrl, OffsetDateTime expiresAt) { }
