using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class message_reaction
    {
        public long id { get; set; }
        public long message_id { get; set; }
        public long user_id { get; set; }
        public string reaction_type { get; set; } = null!;
        public DateTime? created_at { get; set; }

        public virtual message message { get; set; } = null!;
        public virtual user user { get; set; } = null!;
    }
}
