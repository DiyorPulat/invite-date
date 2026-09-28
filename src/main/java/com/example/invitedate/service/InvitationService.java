package com.example.invitedate.service;

import com.example.invitedate.dto.*;
import java.util.UUID;

public interface InvitationService {
    InvitationResponse create(CreateInvitationRequest request);
    PublicInvitationResponse getPublic(UUID id);
    byte[] getQrCode(UUID id);
    TicketResponse respond(UUID id, RespondInvitationRequest request);
    InvitationStatusResponse getStatus(UUID id, UUID ownerToken);
}
