using System;
using System.Collections.Generic;

namespace Zalo_mini.Models
{
    public partial class call
    {
        public call()
        {
            call_participants = new HashSet<call_participant>();
        }

        public long id { get; set; }
        public long conversation_id { get; set; }
        public long caller_id { get; set; }
        public string call_type { get; set; } = null!;
        public string? status { get; set; }
        public DateTime? started_at { get; set; }
        public DateTime? answered_at { get; set; }
        public DateTime? ended_at { get; set; }
        /// <summary>
        /// Duration in seconds
        /// </summary>
        public int? duration { get; set; }

        public virtual user caller { get; set; } = null!;
        public virtual conversation conversation { get; set; } = null!;
        public virtual ICollection<call_participant> call_participants { get; set; }
    }
}
