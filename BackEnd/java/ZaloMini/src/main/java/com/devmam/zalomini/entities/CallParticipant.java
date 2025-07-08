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
@Entity(name = "CallParticipant")
@Table(name = "call_participants")
public class CallParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "call_id", nullable = false)
    private Call call;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt = new Date();

    @Column(name = "left_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date leftAt;

    @ColumnDefault("'invited'")
    @Lob
    @Column(name = "status")
    private String status;

}