using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class notification
    {
        public long id { get; set; }
        public long user_id { get; set; }
        public string type { get; set; } = null!;
        public string title { get; set; } = null!;
        public string? content { get; set; }
        /// <summary>
        /// Additional data as JSON
        /// </summary>
        public string? data { get; set; }
        public bool? is_read { get; set; }
        public DateTime? created_at { get; set; }

        public virtual user user { get; set; } = null!;
    }
}
