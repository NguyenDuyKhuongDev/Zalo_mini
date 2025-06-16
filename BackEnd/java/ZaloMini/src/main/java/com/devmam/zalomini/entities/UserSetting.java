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
@Entity(name = "UserSetting")
@Table(name = "user_settings")
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ColumnDefault("1")
    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled;

    @ColumnDefault("1")
    @Column(name = "sound_enabled")
    private Boolean soundEnabled;

    @ColumnDefault("1")
    @Column(name = "vibration_enabled")
    private Boolean vibrationEnabled;

    @ColumnDefault("1")
    @Column(name = "show_read_receipts")
    private Boolean showReadReceipts;

    @ColumnDefault("'everyone'")
    @Lob
    @Column(name = "last_seen_privacy")
    private String lastSeenPrivacy;

    @ColumnDefault("'everyone'")
    @Lob
    @Column(name = "profile_photo_privacy")
    private String profilePhotoPrivacy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

}