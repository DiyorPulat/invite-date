package com.example.invitedate.entity;

import com.example.invitedate.enums.DateType;
import com.example.invitedate.enums.InvitationStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "invitations")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Invitation {
    @Id private UUID id;
    @Column(nullable = false, unique = true) private UUID ownerToken;
    @Column(nullable = false, length = 100) private String senderName;
    @Column(nullable = false, length = 254) private String senderEmail;
    @Column(nullable = false, length = 100) private String receiverName;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private DateType dateType;
    private OffsetDateTime proposedDate;
    @Column(columnDefinition = "TEXT") private String customMessage;
    /** Creator-configured text and selectable options for the interactive invitation. */
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private String experience;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private InvitationStatus status;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private String responseDetails;
    @Column(nullable = false) private int viewCount;
    @Column(nullable = false, updatable = false) private OffsetDateTime createdAt;
    @Column(nullable = false) private OffsetDateTime updatedAt;
    @Column(nullable = false) private OffsetDateTime expiresAt;

    @PrePersist void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
