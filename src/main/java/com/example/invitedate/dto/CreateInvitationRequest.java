package com.example.invitedate.dto;

import com.example.invitedate.enums.DateType;
import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.Map;

public record CreateInvitationRequest(
    @NotBlank @Size(max = 100) String senderName,
    @NotBlank @Email @Size(max = 254) String senderEmail,
    @NotBlank @Size(max = 100) String receiverName,
    @NotNull DateType dateType,
    @Future OffsetDateTime proposedDate,
    @Size(max = 2_000) String customMessage,
    Map<String, Object> experience) { }
