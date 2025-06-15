using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class group_invitation
    {
        public long id { get; set; }
        public long group_id { get; set; }
        public long inviter_id { get; set; }
        public long invitee_id { get; set; }
        public string? status { get; set; }
        public string? message { get; set; }
        public DateTime? expires_at { get; set; }
        public DateTime? created_at { get; set; }
        public DateTime? responded_at { get; set; }

        public virtual chat_group group { get; set; } = null!;
        public virtual user invitee { get; set; } = null!;
        public virtual user inviter { get; set; } = null!;
    }
}
