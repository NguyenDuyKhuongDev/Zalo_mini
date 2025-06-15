using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class search_history
    {
        public long id { get; set; }
        public long user_id { get; set; }
        public string search_type { get; set; } = null!;
        public string search_query { get; set; } = null!;
        public DateTime? created_at { get; set; }

        public virtual user user { get; set; } = null!;
    }
}
