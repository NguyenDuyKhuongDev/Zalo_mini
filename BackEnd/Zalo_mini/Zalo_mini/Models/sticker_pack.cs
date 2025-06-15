using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class sticker_pack
    {
        public sticker_pack()
        {
            stickers = new HashSet<sticker>();
        }

        public long id { get; set; }
        public string name { get; set; } = null!;
        public string? description { get; set; }
        public string? thumbnail_url { get; set; }
        public bool? is_free { get; set; }
        public bool? is_active { get; set; }
        public DateTime? created_at { get; set; }

        public virtual ICollection<sticker> stickers { get; set; }
    }
}
