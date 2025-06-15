using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class sticker
    {
        public long id { get; set; }
        public long pack_id { get; set; }
        public string? name { get; set; }
        public string image_url { get; set; } = null!;
        /// <summary>
        /// Comma-separated keywords for search
        /// </summary>
        public string? keywords { get; set; }
        public DateTime? created_at { get; set; }

        public virtual sticker_pack pack { get; set; } = null!;
    }
}
