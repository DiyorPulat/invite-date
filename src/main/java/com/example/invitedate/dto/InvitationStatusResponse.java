package com.example.invitedate.dto;

import com.example.invitedate.enums.InvitationStatus;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record InvitationStatusResponse(UUID id, InvitationStatus status, Map<String, Object> responseDetails,
                                       int viewCount, OffsetDateTime updatedAt) { }
