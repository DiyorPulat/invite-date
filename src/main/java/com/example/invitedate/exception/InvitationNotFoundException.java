package com.example.invitedate.exception;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException() { super("Invitation was not found"); }
}
