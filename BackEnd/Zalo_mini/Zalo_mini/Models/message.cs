using System;
using System.Collections.Generic;
using Zalo_mini.Constants;

namespace Zalo_mini.Models
{
    public partial class message
    {
        public message()
        {
            Inversereply_to_message = new HashSet<message>();
            conversations = new HashSet<conversation>();
            message_deletions = new HashSet<message_deletion>();
            message_reactions = new HashSet<message_reaction>();
            message_read_statuses = new HashSet<message_read_status>();
        }

        public long id { get; set; }
        public long conversation_id { get; set; }
        public long sender_id { get; set; }
        public long? reply_to_message_id { get; set; }
        public string? message_type { get; set; } = MessageType.Text;
        public string? content { get; set; }
        public string? media_url { get; set; }
        public string? file_name { get; set; }
        public long? file_size { get; set; }
        /// <summary>
        /// Duration in seconds for audio/video
        /// </summary>
        public int? duration { get; set; }
        public bool? is_deleted { get; set; }
        public bool? is_recalled { get; set; }
        public bool? is_pinned { get; set; }
        public DateTime? created_at { get; set; }
        public DateTime? updated_at { get; set; }

        public virtual conversation conversation { get; set; } = null!;
        public virtual message? reply_to_message { get; set; }
        public virtual user sender { get; set; } = null!;
        public virtual ICollection<message> Inversereply_to_message { get; set; }
        public virtual ICollection<conversation> conversations { get; set; }
        public virtual ICollection<message_deletion> message_deletions { get; set; }
        public virtual ICollection<message_reaction> message_reactions { get; set; }
        public virtual ICollection<message_read_status> message_read_statuses { get; set; }
    }
}
