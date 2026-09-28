package com.example.invitedate.controller;

import com.example.invitedate.dto.*;
import com.example.invitedate.exception.InvitationNotFoundException;
import com.example.invitedate.service.InvitationService;
import com.example.invitedate.service.InvitationPdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;
    private final InvitationPdfService invitationPdfService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponse create(@Valid @RequestBody CreateInvitationRequest request) { return invitationService.create(request); }

    @GetMapping("/{id}")
    public PublicInvitationResponse get(@PathVariable String id) { return invitationService.getPublic(uuid(id)); }

    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qr(@PathVariable String id) {
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(java.time.Duration.ofDays(30)))
                .body(invitationService.getQrCode(uuid(id)));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable String id) {
        return ResponseEntity.ok()
                .header("Content-Disposition", ContentDisposition.attachment().filename("birga-taklif.pdf").build().toString())
                .body(invitationPdfService.generate(uuid(id)));
    }
    @PostMapping("/{id}/respond")
    public TicketResponse respond(@PathVariable String id, @Valid @RequestBody RespondInvitationRequest request) {
        return invitationService.respond(uuid(id), request);
    }
    @GetMapping("/{id}/status")
    public InvitationStatusResponse status(@PathVariable String id, @RequestParam String token) {
        return invitationService.getStatus(uuid(id), uuid(token));
    }
    private UUID uuid(String value) {
        try { return UUID.fromString(value); } catch (IllegalArgumentException ignored) { throw new InvitationNotFoundException(); }
    }
}
