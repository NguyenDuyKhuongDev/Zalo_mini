using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class call_participant
    {
        public long id { get; set; }
        public long call_id { get; set; }
        public long user_id { get; set; }
        public DateTime? joined_at { get; set; }
        public DateTime? left_at { get; set; }
        public string? status { get; set; }

        public virtual call call { get; set; } = null!;
        public virtual user user { get; set; } = null!;
    }
}
