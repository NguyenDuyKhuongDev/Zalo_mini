using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class conversation_tag_assignment
    {
        public long id { get; set; }
        public long conversation_id { get; set; }
        public long tag_id { get; set; }
        public long user_id { get; set; }
        public DateTime? created_at { get; set; }

        public virtual conversation conversation { get; set; } = null!;
        public virtual conversation_tag tag { get; set; } = null!;
        public virtual user user { get; set; } = null!;
    }
}
