package com.example.invitedate.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketResponse(UUID invitationId, String ticketCode, String message, OffsetDateTime confirmedAt) { }
