using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;
using Zalo_mini.Models;

namespace Zalo_mini.Data
{
    public partial class AppDbContext : DbContext
    {
        public AppDbContext()
        {
        }

        public AppDbContext(DbContextOptions<AppDbContext> options)
            : base(options)
        {
        }

        public virtual DbSet<call> calls { get; set; } = null!;
        public virtual DbSet<call_participant> call_participants { get; set; } = null!;
        public virtual DbSet<chat_group> chat_groups { get; set; } = null!;
        public virtual DbSet<conversation> conversations { get; set; } = null!;
        public virtual DbSet<conversation_participant> conversation_participants { get; set; } = null!;
        public virtual DbSet<conversation_tag> conversation_tags { get; set; } = null!;
        public virtual DbSet<conversation_tag_assignment> conversation_tag_assignments { get; set; } = null!;
        public virtual DbSet<friendship> friendships { get; set; } = null!;
        public virtual DbSet<group_invitation> group_invitations { get; set; } = null!;
        public virtual DbSet<group_member> group_members { get; set; } = null!;
        public virtual DbSet<message> messages { get; set; } = null!;
        public virtual DbSet<message_deletion> message_deletions { get; set; } = null!;
        public virtual DbSet<message_reaction> message_reactions { get; set; } = null!;
        public virtual DbSet<message_read_status> message_read_statuses { get; set; } = null!;
        public virtual DbSet<notification> notifications { get; set; } = null!;
        public virtual DbSet<search_history> search_histories { get; set; } = null!;
        public virtual DbSet<sticker> stickers { get; set; } = null!;
        public virtual DbSet<sticker_pack> sticker_packs { get; set; } = null!;
        public virtual DbSet<user> users { get; set; } = null!;
        public virtual DbSet<user_block> user_blocks { get; set; } = null!;
        public virtual DbSet<user_session> user_sessions { get; set; } = null!;
        public virtual DbSet<user_setting> user_settings { get; set; } = null!;
        public virtual DbSet<conversation_deleted> converation_deleteds { get; set; } = null!;

        /*  protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
          {
              if (!optionsBuilder.IsConfigured)
              {
  #warning To protect potentially sensitive information in your connection string, you should move it out of source code. You can avoid scaffolding the connection string by using the Name= syntax to read it from configuration - see https://go.microsoft.com/fwlink/?linkid=2131148. For more guidance on storing connection strings, see http://go.microsoft.com/fwlink/?LinkId=723263.
                  optionsBuilder.UseMySql("server=localhost;port=3306;user=root;password=231200231k231;database=zalo_mini", Microsoft.EntityFrameworkCore.ServerVersion.Parse("8.0.39-mysql"));
              }
          }*/

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.UseCollation("utf8mb4_0900_ai_ci")
                .HasCharSet("utf8mb4");

            modelBuilder.Entity<call>(entity =>
            {
                entity.HasIndex(e => e.caller_id, "idx_calls_caller");

                entity.HasIndex(e => e.conversation_id, "idx_calls_conversation");

                entity.HasIndex(e => e.started_at, "idx_calls_started");

                entity.Property(e => e.answered_at).HasColumnType("timestamp");

                entity.Property(e => e.call_type).HasColumnType("enum('voice','video')");

                entity.Property(e => e.duration).HasComment("Duration in seconds");

                entity.Property(e => e.ended_at).HasColumnType("timestamp");

                entity.Property(e => e.started_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.status)
                    .HasColumnType("enum('initiated','ringing','answered','ended','missed','declined')")
                    .HasDefaultValueSql("'initiated'");

                entity.HasOne(d => d.caller)
                    .WithMany(p => p.calls)
                    .HasForeignKey(d => d.caller_id)
                    .HasConstraintName("calls_ibfk_2");

                entity.HasOne(d => d.conversation)
                    .WithMany(p => p.calls)
                    .HasForeignKey(d => d.conversation_id)
                    .HasConstraintName("calls_ibfk_1");
            });

            modelBuilder.Entity<call_participant>(entity =>
            {
                entity.HasIndex(e => e.call_id, "idx_call_participants_call");

                entity.HasIndex(e => e.user_id, "idx_call_participants_user");

                entity.Property(e => e.joined_at).HasColumnType("timestamp");

                entity.Property(e => e.left_at).HasColumnType("timestamp");

                entity.Property(e => e.status)
                    .HasColumnType("enum('invited','joined','left','declined')")
                    .HasDefaultValueSql("'invited'");

                entity.HasOne(d => d.call)
                    .WithMany(p => p.call_participants)
                    .HasForeignKey(d => d.call_id)
                    .HasConstraintName("call_participants_ibfk_1");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.call_participants)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("call_participants_ibfk_2");
            });

            modelBuilder.Entity<chat_group>(entity =>
            {
                entity.HasIndex(e => e.created_by, "idx_groups_creator");

                entity.HasIndex(e => e.group_type, "idx_groups_type");

                entity.Property(e => e.avatar_url).HasColumnType("text");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.description).HasColumnType("text");

                entity.Property(e => e.group_type)
                    .HasColumnType("enum('private','group','channel')")
                    .HasDefaultValueSql("'group'");

                entity.Property(e => e.is_active).HasDefaultValueSql("'1'");

                entity.Property(e => e.max_members).HasDefaultValueSql("'200'");

                entity.Property(e => e.name).HasMaxLength(255);

                entity.Property(e => e.updated_at)
                    .HasColumnType("timestamp")
                    .ValueGeneratedOnAddOrUpdate()
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.created_byNavigation)
                    .WithMany(p => p.chat_groups)
                    .HasForeignKey(d => d.created_by)
                    .HasConstraintName("chat_groups_ibfk_1");
            });

            modelBuilder.Entity<conversation>(entity =>
            {
                entity.HasIndex(e => e.last_message_id, "fk_conversations_last_message");

                entity.HasIndex(e => e.group_id, "idx_conversations_group");

                entity.HasIndex(e => e.last_message_at, "idx_conversations_last_message");

                entity.HasIndex(e => e.conversation_type, "idx_conversations_type");

                entity.Property(e => e.avatar_url).HasColumnType("text");

                entity.Property(e => e.conversation_type).HasColumnType("enum('direct','group')");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.is_active).HasDefaultValueSql("'1'");

                entity.Property(e => e.last_message_at).HasColumnType("timestamp");

                entity.Property(e => e.name).HasMaxLength(255);

                entity.Property(e => e.updated_at)
                    .HasColumnType("timestamp")
                    .ValueGeneratedOnAddOrUpdate()
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.group)
                    .WithMany(p => p.conversations)
                    .HasForeignKey(d => d.group_id)
                    .OnDelete(DeleteBehavior.Cascade)
                    .HasConstraintName("conversations_ibfk_1");

                entity.HasOne(d => d.last_message)
                    .WithMany(p => p.conversations)
                    .HasForeignKey(d => d.last_message_id)
                    .HasConstraintName("fk_conversations_last_message");
            });

            modelBuilder.Entity<conversation_participant>(entity =>
            {
                entity.HasIndex(e => e.conversation_id, "idx_conversation_participants_conv");

                entity.HasIndex(e => new { e.user_id, e.is_pinned }, "idx_conversation_participants_pinned");

                entity.HasIndex(e => e.user_id, "idx_conversation_participants_user");

                entity.HasIndex(e => new { e.conversation_id, e.user_id }, "unique_conversation_participant")
                    .IsUnique();

                entity.Property(e => e.is_archived).HasDefaultValueSql("'0'");

                entity.Property(e => e.is_muted).HasDefaultValueSql("'0'");

                entity.Property(e => e.is_pinned).HasDefaultValueSql("'0'");

                entity.Property(e => e.joined_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.last_read_at).HasColumnType("timestamp");

                entity.HasOne(d => d.conversation)
                    .WithMany(p => p.conversation_participants)
                    .HasForeignKey(d => d.conversation_id)
                    .HasConstraintName("conversation_participants_ibfk_1");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.conversation_participants)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("conversation_participants_ibfk_2");
            });

            modelBuilder.Entity<conversation_deleted>(entity =>
            {
                entity.HasOne(d => d.Conversation)
                    .WithMany(p => p.conversation_deleted)
                    .HasForeignKey(d => d.ConversationId)
                    .HasConstraintName("conversation_deleteds_Conversation");

                entity.HasOne(d => d.User)
               .WithMany(p => p.conversation_deleted)
               .HasForeignKey(d => d.ConversationId)
               .HasConstraintName("conversation_deleteds_User");

            });




            modelBuilder.Entity<conversation_tag>(entity =>
            {
                entity.HasIndex(e => e.user_id, "idx_conversation_tags_user");

                entity.HasIndex(e => new { e.name, e.user_id }, "unique_user_tag")
                    .IsUnique();

                entity.Property(e => e.color)
                    .HasMaxLength(7)
                    .HasDefaultValueSql("'#007bff'")
                    .HasComment("Hex color code");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.name).HasMaxLength(50);

                entity.HasOne(d => d.user)
                    .WithMany(p => p.conversation_tags)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("conversation_tags_ibfk_1");
            });

            modelBuilder.Entity<conversation_tag_assignment>(entity =>
            {
                entity.HasIndex(e => e.conversation_id, "idx_conversation_tag_assignments_conv");

                entity.HasIndex(e => e.tag_id, "idx_conversation_tag_assignments_tag");

                entity.HasIndex(e => e.user_id, "idx_conversation_tag_assignments_user");

                entity.HasIndex(e => new { e.conversation_id, e.tag_id, e.user_id }, "unique_conversation_tag_assignment")
                    .IsUnique();

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.conversation)
                    .WithMany(p => p.conversation_tag_assignments)
                    .HasForeignKey(d => d.conversation_id)
                    .HasConstraintName("conversation_tag_assignments_ibfk_1");

                entity.HasOne(d => d.tag)
                    .WithMany(p => p.conversation_tag_assignments)
                    .HasForeignKey(d => d.tag_id)
                    .HasConstraintName("conversation_tag_assignments_ibfk_2");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.conversation_tag_assignments)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("conversation_tag_assignments_ibfk_3");
            });

            modelBuilder.Entity<friendship>(entity =>
            {
                entity.HasIndex(e => e.addressee_id, "idx_friendships_addressee");

                entity.HasIndex(e => e.requester_id, "idx_friendships_requester");

                entity.HasIndex(e => e.status, "idx_friendships_status");

                entity.HasIndex(e => new { e.requester_id, e.addressee_id }, "unique_friendship")
                    .IsUnique();

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.status)
                    .HasColumnType("enum('pending','accepted','declined','blocked')")
                    .HasDefaultValueSql("'pending'");

                entity.Property(e => e.updated_at)
                    .HasColumnType("timestamp")
                    .ValueGeneratedOnAddOrUpdate()
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.addressee)
                    .WithMany(p => p.friendshipaddressees)
                    .HasForeignKey(d => d.addressee_id)
                    .HasConstraintName("friendships_ibfk_2");

                entity.HasOne(d => d.requester)
                    .WithMany(p => p.friendshiprequesters)
                    .HasForeignKey(d => d.requester_id)
                    .HasConstraintName("friendships_ibfk_1");
            });

            modelBuilder.Entity<group_invitation>(entity =>
            {
                entity.HasIndex(e => e.group_id, "idx_group_invitations_group");

                entity.HasIndex(e => e.invitee_id, "idx_group_invitations_invitee");

                entity.HasIndex(e => e.status, "idx_group_invitations_status");

                entity.HasIndex(e => e.inviter_id, "inviter_id");

                entity.HasIndex(e => new { e.group_id, e.invitee_id, e.status }, "unique_group_invitation")
                    .IsUnique();

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.expires_at).HasColumnType("timestamp");

                entity.Property(e => e.message).HasColumnType("text");

                entity.Property(e => e.responded_at).HasColumnType("timestamp");

                entity.Property(e => e.status)
                    .HasColumnType("enum('pending','accepted','declined','expired')")
                    .HasDefaultValueSql("'pending'");

                entity.HasOne(d => d.group)
                    .WithMany(p => p.group_invitations)
                    .HasForeignKey(d => d.group_id)
                    .HasConstraintName("group_invitations_ibfk_1");

                entity.HasOne(d => d.invitee)
                    .WithMany(p => p.group_invitationinvitees)
                    .HasForeignKey(d => d.invitee_id)
                    .HasConstraintName("group_invitations_ibfk_3");

                entity.HasOne(d => d.inviter)
                    .WithMany(p => p.group_invitationinviters)
                    .HasForeignKey(d => d.inviter_id)
                    .HasConstraintName("group_invitations_ibfk_2");
            });

            modelBuilder.Entity<group_member>(entity =>
            {
                entity.HasIndex(e => e.group_id, "idx_group_members_group");

                entity.HasIndex(e => e.role, "idx_group_members_role");

                entity.HasIndex(e => e.user_id, "idx_group_members_user");

                entity.HasIndex(e => new { e.group_id, e.user_id }, "unique_group_member")
                    .IsUnique();

                entity.Property(e => e.is_active).HasDefaultValueSql("'1'");

                entity.Property(e => e.joined_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.role)
                    .HasColumnType("enum('owner','admin','member')")
                    .HasDefaultValueSql("'member'");

                entity.HasOne(d => d.group)
                    .WithMany(p => p.group_members)
                    .HasForeignKey(d => d.group_id)
                    .HasConstraintName("group_members_ibfk_1");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.group_members)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("group_members_ibfk_2");
            });

            modelBuilder.Entity<message>(entity =>
            {
                entity.HasIndex(e => e.content, "idx_messages_content")
                    .HasAnnotation("MySql:FullTextIndex", true);

                entity.HasIndex(e => e.conversation_id, "idx_messages_conversation");

                entity.HasIndex(e => e.created_at, "idx_messages_created");

                entity.HasIndex(e => e.reply_to_message_id, "idx_messages_reply");

                entity.HasIndex(e => e.sender_id, "idx_messages_sender");

                entity.HasIndex(e => e.message_type, "idx_messages_type");

                entity.Property(e => e.content).HasColumnType("text");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.duration).HasComment("Duration in seconds for audio/video");

                entity.Property(e => e.file_name).HasMaxLength(255);

                entity.Property(e => e.is_deleted).HasDefaultValueSql("'0'");

                entity.Property(e => e.is_pinned).HasDefaultValueSql("'0'");

                entity.Property(e => e.is_recalled).HasDefaultValueSql("'0'");

                entity.Property(e => e.media_url).HasColumnType("text");

                entity.Property(e => e.message_type)
                    .HasColumnType("enum('text','image','video','audio','file','sticker','location','contact')")
                    .HasDefaultValueSql("'text'");

                entity.Property(e => e.updated_at)
                    .HasColumnType("timestamp")
                    .ValueGeneratedOnAddOrUpdate()
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.conversation)
                    .WithMany(p => p.messages)
                    .HasForeignKey(d => d.conversation_id)
                    .HasConstraintName("messages_ibfk_1");

                entity.HasOne(d => d.reply_to_message)
                    .WithMany(p => p.Inversereply_to_message)
                    .HasForeignKey(d => d.reply_to_message_id)
                    .HasConstraintName("messages_ibfk_3");

                entity.HasOne(d => d.sender)
                    .WithMany(p => p.messages)
                    .HasForeignKey(d => d.sender_id)
                    .HasConstraintName("messages_ibfk_2");
            });

            modelBuilder.Entity<message_deletion>(entity =>
            {
                entity.HasIndex(e => e.message_id, "idx_message_deletions_message");

                entity.HasIndex(e => e.user_id, "idx_message_deletions_user");

                entity.HasIndex(e => new { e.message_id, e.user_id }, "unique_message_deletion")
                    .IsUnique();

                entity.Property(e => e.deleted_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.message)
                    .WithMany(p => p.message_deletions)
                    .HasForeignKey(d => d.message_id)
                    .HasConstraintName("message_deletions_ibfk_1");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.message_deletions)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("message_deletions_ibfk_2");
            });

            modelBuilder.Entity<message_reaction>(entity =>
            {
                entity.HasIndex(e => e.message_id, "idx_message_reactions_message");

                entity.HasIndex(e => e.user_id, "idx_message_reactions_user");

                entity.HasIndex(e => new { e.message_id, e.user_id, e.reaction_type }, "unique_message_reaction")
                    .IsUnique();

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.reaction_type).HasColumnType("enum('like','love','laugh','sad','angry','wow')");

                entity.HasOne(d => d.message)
                    .WithMany(p => p.message_reactions)
                    .HasForeignKey(d => d.message_id)
                    .HasConstraintName("message_reactions_ibfk_1");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.message_reactions)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("message_reactions_ibfk_2");
            });

            modelBuilder.Entity<message_read_status>(entity =>
            {
                entity.ToTable("message_read_status");

                entity.HasIndex(e => e.message_id, "idx_message_read_status_message");

                entity.HasIndex(e => e.user_id, "idx_message_read_status_user");

                entity.HasIndex(e => new { e.message_id, e.user_id }, "unique_message_read")
                    .IsUnique();

                entity.Property(e => e.read_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.message)
                    .WithMany(p => p.message_read_statuses)
                    .HasForeignKey(d => d.message_id)
                    .HasConstraintName("message_read_status_ibfk_1");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.message_read_statuses)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("message_read_status_ibfk_2");
            });

            modelBuilder.Entity<notification>(entity =>
            {
                entity.HasIndex(e => e.type, "idx_notifications_type");

                entity.HasIndex(e => new { e.user_id, e.is_read }, "idx_notifications_unread");

                entity.HasIndex(e => e.user_id, "idx_notifications_user");

                entity.Property(e => e.content).HasColumnType("text");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.data)
                    .HasColumnType("json")
                    .HasComment("Additional data as JSON");

                entity.Property(e => e.is_read).HasDefaultValueSql("'0'");

                entity.Property(e => e.title).HasMaxLength(255);

                entity.Property(e => e.type).HasMaxLength(50);

                entity.HasOne(d => d.user)
                    .WithMany(p => p.notifications)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("notifications_ibfk_1");
            });

            modelBuilder.Entity<search_history>(entity =>
            {
                entity.ToTable("search_history");

                entity.HasIndex(e => e.created_at, "idx_search_history_created");

                entity.HasIndex(e => e.search_type, "idx_search_history_type");

                entity.HasIndex(e => e.user_id, "idx_search_history_user");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.search_query).HasColumnType("text");

                entity.Property(e => e.search_type).HasColumnType("enum('user','message','conversation')");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.search_histories)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("search_history_ibfk_1");
            });

            modelBuilder.Entity<sticker>(entity =>
            {
                entity.HasIndex(e => e.keywords, "idx_stickers_keywords")
                    .HasAnnotation("MySql:FullTextIndex", true);

                entity.HasIndex(e => e.pack_id, "idx_stickers_pack");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.image_url).HasColumnType("text");

                entity.Property(e => e.keywords)
                    .HasColumnType("text")
                    .HasComment("Comma-separated keywords for search");

                entity.Property(e => e.name).HasMaxLength(100);

                entity.HasOne(d => d.pack)
                    .WithMany(p => p.stickers)
                    .HasForeignKey(d => d.pack_id)
                    .HasConstraintName("stickers_ibfk_1");
            });

            modelBuilder.Entity<sticker_pack>(entity =>
            {
                entity.HasIndex(e => e.is_active, "idx_sticker_packs_active");

                entity.HasIndex(e => e.is_free, "idx_sticker_packs_free");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.description).HasColumnType("text");

                entity.Property(e => e.is_active).HasDefaultValueSql("'1'");

                entity.Property(e => e.is_free).HasDefaultValueSql("'1'");

                entity.Property(e => e.name).HasMaxLength(100);

                entity.Property(e => e.thumbnail_url).HasColumnType("text");
            });

            modelBuilder.Entity<user>(entity =>
            {
                entity.HasIndex(e => e.is_active, "idx_users_active");

                entity.HasIndex(e => e.phone_number, "idx_users_phone")
                    .IsUnique();

                entity.HasIndex(e => e.username, "idx_users_username")
                    .IsUnique();

                entity.Property(e => e.avatar_url).HasColumnType("text");

                entity.Property(e => e.bio).HasColumnType("text");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.display_name).HasMaxLength(100);

                entity.Property(e => e.email).HasMaxLength(255);

                entity.Property(e => e.gender).HasColumnType("enum('male','female','other')");

                entity.Property(e => e.is_active).HasDefaultValueSql("'1'");

                entity.Property(e => e.is_blocked).HasDefaultValueSql("'0'");

                entity.Property(e => e.last_seen).HasColumnType("timestamp");

                entity.Property(e => e.phone_number).HasMaxLength(15);

                entity.Property(e => e.updated_at)
                    .HasColumnType("timestamp")
                    .ValueGeneratedOnAddOrUpdate()
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.username).HasMaxLength(50);
            });

            modelBuilder.Entity<user_block>(entity =>
            {
                entity.HasIndex(e => e.blocked_id, "idx_user_blocks_blocked");

                entity.HasIndex(e => e.blocker_id, "idx_user_blocks_blocker");

                entity.HasIndex(e => new { e.blocker_id, e.blocked_id }, "unique_user_block")
                    .IsUnique();

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasOne(d => d.blocked)
                    .WithMany(p => p.user_blockblockeds)
                    .HasForeignKey(d => d.blocked_id)
                    .HasConstraintName("user_blocks_ibfk_2");

                entity.HasOne(d => d.blocker)
                    .WithMany(p => p.user_blockblockers)
                    .HasForeignKey(d => d.blocker_id)
                    .HasConstraintName("user_blocks_ibfk_1");
            });

            modelBuilder.Entity<user_session>(entity =>
            {
                entity.HasIndex(e => e.access_token, "idx_sessions_token")
                    .HasAnnotation("MySql:IndexPrefixLength", new[] { 100 });

                entity.HasIndex(e => e.user_id, "idx_sessions_user");

                entity.Property(e => e.access_token).HasColumnType("text");

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.device_id).HasMaxLength(255);

                entity.Property(e => e.device_type).HasMaxLength(50);

                entity.Property(e => e.expires_at).HasColumnType("timestamp");

                entity.Property(e => e.is_active).HasDefaultValueSql("'1'");

                entity.Property(e => e.refresh_token).HasColumnType("text");

                entity.HasOne(d => d.user)
                    .WithMany(p => p.user_sessions)
                    .HasForeignKey(d => d.user_id)
                    .HasConstraintName("user_sessions_ibfk_1");
            });

            modelBuilder.Entity<user_setting>(entity =>
            {
                entity.HasIndex(e => e.user_id, "idx_user_settings_user")
                    .IsUnique();

                entity.Property(e => e.created_at)
                    .HasColumnType("timestamp")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.last_seen_privacy)
                    .HasColumnType("enum('everyone','friends','nobody')")
                    .HasDefaultValueSql("'everyone'");

                entity.Property(e => e.notifications_enabled).HasDefaultValueSql("'1'");

                entity.Property(e => e.profile_photo_privacy)
                    .HasColumnType("enum('everyone','friends','nobody')")
                    .HasDefaultValueSql("'everyone'");

                entity.Property(e => e.show_read_receipts).HasDefaultValueSql("'1'");

                entity.Property(e => e.sound_enabled).HasDefaultValueSql("'1'");

                entity.Property(e => e.updated_at)
                    .HasColumnType("timestamp")
                    .ValueGeneratedOnAddOrUpdate()
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.vibration_enabled).HasDefaultValueSql("'1'");

                entity.HasOne(d => d.user)
                    .WithOne(p => p.user_setting)
                    .HasForeignKey<user_setting>(d => d.user_id)
                    .HasConstraintName("user_settings_ibfk_1");
            });

            OnModelCreatingPartial(modelBuilder);
        }

        partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
    }
}
