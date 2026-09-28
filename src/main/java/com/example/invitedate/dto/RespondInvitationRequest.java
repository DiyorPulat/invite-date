package com.example.invitedate.dto;

import com.example.invitedate.enums.InvitationStatus;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record RespondInvitationRequest(
    @NotNull InvitationStatus status,
    Map<String, Object> responseDetails) { }
