using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class user_session
    {
        public long id { get; set; }
        public long user_id { get; set; }
        public string? device_id { get; set; }
        public string? device_type { get; set; }
        public string access_token { get; set; } = null!;
        public string? refresh_token { get; set; }
        public DateTime? expires_at { get; set; }
        public bool? is_active { get; set; }
        public DateTime? created_at { get; set; }

        public virtual user user { get; set; } = null!;
    }
}
