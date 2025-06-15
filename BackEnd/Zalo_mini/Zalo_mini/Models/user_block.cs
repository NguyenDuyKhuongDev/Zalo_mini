using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class user_block
    {
        public long id { get; set; }
        public long blocker_id { get; set; }
        public long blocked_id { get; set; }
        public DateTime? created_at { get; set; }

        public virtual user blocked { get; set; } = null!;
        public virtual user blocker { get; set; } = null!;
    }
}
