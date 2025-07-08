package com.devmam.zalomini.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Call")
@Table(name = "calls")
public class Call {
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
    @JoinColumn(name = "caller_id", nullable = false)
    private User caller;

    @Lob
    @Column(name = "call_type", nullable = false)
    private String callType;

    @ColumnDefault("'initiated'")
    @Lob
    @Column(name = "status")
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "started_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date startedAt;

    @Column(name = "answered_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date answeredAt;

    @Column(name = "ended_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date endedAt;

    @Column(name = "duration")
    private Integer duration;

    @OneToMany(mappedBy = "call", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CallParticipant> participants;

}