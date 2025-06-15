using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class friendship
    {
        public long id { get; set; }
        public long requester_id { get; set; }
        public long addressee_id { get; set; }
        public string? status { get; set; }
        public DateTime? created_at { get; set; }
        public DateTime? updated_at { get; set; }

        public virtual user addressee { get; set; } = null!;
        public virtual user requester { get; set; } = null!;
    }
}
