using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class conversation_tag
    {
        public conversation_tag()
        {
            conversation_tag_assignments = new HashSet<conversation_tag_assignment>();
        }

        public long id { get; set; }
        public string name { get; set; } = null!;
        /// <summary>
        /// Hex color code
        /// </summary>
        public string? color { get; set; }
        public long user_id { get; set; }
        public DateTime? created_at { get; set; }

        public virtual user user { get; set; } = null!;
        public virtual ICollection<conversation_tag_assignment> conversation_tag_assignments { get; set; }
    }
}
