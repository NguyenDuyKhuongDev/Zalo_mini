using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class user
    {
        public user()
        {
            call_participants = new HashSet<call_participant>();
            calls = new HashSet<call>();
            chat_groups = new HashSet<chat_group>();
            conversation_participants = new HashSet<conversation_participant>();
            conversation_tag_assignments = new HashSet<conversation_tag_assignment>();
            conversation_tags = new HashSet<conversation_tag>();
            friendshipaddressees = new HashSet<friendship>();
            friendshiprequesters = new HashSet<friendship>();
            group_invitationinvitees = new HashSet<group_invitation>();
            group_invitationinviters = new HashSet<group_invitation>();
            group_members = new HashSet<group_member>();
            message_deletions = new HashSet<message_deletion>();
            message_reactions = new HashSet<message_reaction>();
            message_read_statuses = new HashSet<message_read_status>();
            messages = new HashSet<message>();
            notifications = new HashSet<notification>();
            search_histories = new HashSet<search_history>();
            user_blockblockeds = new HashSet<user_block>();
            user_blockblockers = new HashSet<user_block>();
            user_sessions = new HashSet<user_session>();
        }

        public long id { get; set; }
        public string phone_number { get; set; } = null!;
        public string? username { get; set; }
        public string display_name { get; set; } = null!;
        public string email { get; set; }
        public string? avatar_url { get; set; }
        public string? bio { get; set; }
        public DateOnly? date_of_birth { get; set; }
        public string? gender { get; set; }
        public bool? is_active { get; set; }
        public bool? is_blocked { get; set; }
        public DateTime? last_seen { get; set; }
        public DateTime? created_at { get; set; }
        public DateTime? updated_at { get; set; }

        public virtual user_setting user_setting { get; set; } = null!;
        public virtual ICollection<call_participant> call_participants { get; set; }
        public virtual ICollection<call> calls { get; set; }
        public virtual ICollection<chat_group> chat_groups { get; set; }
        public virtual ICollection<conversation_participant> conversation_participants { get; set; }
        public virtual ICollection<conversation_tag_assignment> conversation_tag_assignments { get; set; }
        public virtual ICollection<conversation_tag> conversation_tags { get; set; }
        public virtual ICollection<friendship> friendshipaddressees { get; set; }
        public virtual ICollection<friendship> friendshiprequesters { get; set; }
        public virtual ICollection<group_invitation> group_invitationinvitees { get; set; }
        public virtual ICollection<group_invitation> group_invitationinviters { get; set; }
        public virtual ICollection<group_member> group_members { get; set; }
        public virtual ICollection<message_deletion> message_deletions { get; set; }
        public virtual ICollection<message_reaction> message_reactions { get; set; }
        public virtual ICollection<message_read_status> message_read_statuses { get; set; }
        public virtual ICollection<message> messages { get; set; }
        public virtual ICollection<notification> notifications { get; set; }
        public virtual ICollection<search_history> search_histories { get; set; }
        public virtual ICollection<user_block> user_blockblockeds { get; set; }
        public virtual ICollection<user_block> user_blockblockers { get; set; }
        public virtual ICollection<user_session> user_sessions { get; set; }
        public virtual ICollection<conversation_deleted> conversation_deleted { get; set; } = new HashSet<conversation_deleted>();
         }
}
