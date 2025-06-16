package com.devmam.zalomini.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "ConversationParticipant")
@Table(name = "conversation_participants")
public class ConversationParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "joined_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt;

    @Column(name = "last_read_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastReadAt;

    @ColumnDefault("0")
    @Column(name = "is_muted")
    private Boolean isMuted;

    @ColumnDefault("0")
    @Column(name = "is_pinned")
    private Boolean isPinned;

    @ColumnDefault("0")
    @Column(name = "is_archived")
    private Boolean isArchived;

}