using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class conversation
    {
        public conversation()
        {
            calls = new HashSet<call>();
            conversation_participants = new HashSet<conversation_participant>();
            conversation_tag_assignments = new HashSet<conversation_tag_assignment>();
            messages = new HashSet<message>();
        }

        public long id { get; set; }
        public string conversation_type { get; set; } = null!;
        public long? group_id { get; set; }
        public string? name { get; set; }
        public string? avatar_url { get; set; }
        public long? last_message_id { get; set; }
        public DateTime? last_message_at { get; set; }
        public bool? is_active { get; set; } = true;
        public DateTime? created_at { get; set; }
        public DateTime? updated_at { get; set; }

        public virtual chat_group? group { get; set; }
        public virtual message? last_message { get; set; }
        public virtual ICollection<call> calls { get; set; }
        public virtual ICollection<conversation_participant> conversation_participants { get; set; }
        public virtual ICollection<conversation_tag_assignment> conversation_tag_assignments { get; set; }
        public virtual ICollection<message> messages { get; set; }
        public virtual ICollection<conversation_deleted> conversation_deleted { get; set; } = new HashSet<conversation_deleted>();
    }
}
