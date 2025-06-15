using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class conversation_participant
    {
        public long id { get; set; }
        public long conversation_id { get; set; }
        public long user_id { get; set; }
        public DateTime? joined_at { get; set; }
        public DateTime? last_read_at { get; set; }
        public bool? is_muted { get; set; }
        public bool? is_pinned { get; set; }
        public bool? is_archived { get; set; }

        public virtual conversation conversation { get; set; } = null!;
        public virtual user user { get; set; } = null!;
    }
}
