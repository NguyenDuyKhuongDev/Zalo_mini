using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class chat_group
    {
        public chat_group()
        {
            conversations = new HashSet<conversation>();
            group_invitations = new HashSet<group_invitation>();
            group_members = new HashSet<group_member>();
        }

        public long id { get; set; }
        public string name { get; set; } = null!;
        public string? description { get; set; }
        public string? avatar_url { get; set; }
        public string? group_type { get; set; }
        public int? max_members { get; set; }
        public bool? is_active { get; set; }
        public long? created_by { get; set; }
        public DateTime? created_at { get; set; }
        public DateTime? updated_at { get; set; }

        public virtual user? created_byNavigation { get; set; }
        public virtual ICollection<conversation> conversations { get; set; }
        public virtual ICollection<group_invitation> group_invitations { get; set; }
        public virtual ICollection<group_member> group_members { get; set; }
    }
}
