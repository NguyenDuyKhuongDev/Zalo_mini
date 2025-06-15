using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class group_member
    {
        public long id { get; set; }
        public long group_id { get; set; }
        public long user_id { get; set; }
        public string? role { get; set; }
        public DateTime? joined_at { get; set; }
        public bool? is_active { get; set; }

        public virtual chat_group group { get; set; } = null!;
        public virtual user user { get; set; } = null!;
    }
}
