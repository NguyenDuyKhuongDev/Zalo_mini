using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class user_setting
    {
        public long id { get; set; }
        public long user_id { get; set; }
        public bool? notifications_enabled { get; set; }
        public bool? sound_enabled { get; set; }
        public bool? vibration_enabled { get; set; }
        public bool? show_read_receipts { get; set; }
        public string? last_seen_privacy { get; set; }
        public string? profile_photo_privacy { get; set; }
        public DateTime? created_at { get; set; }
        public DateTime? updated_at { get; set; }

        public virtual user user { get; set; } = null!;
    }
}
