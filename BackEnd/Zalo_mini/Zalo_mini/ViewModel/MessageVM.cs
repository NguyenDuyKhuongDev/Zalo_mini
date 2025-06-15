using System.ComponentModel.DataAnnotations;

namespace Zalo_mini.ViewModel
{
    public class MessageVM
    {
        [Required]
        public long conversation_id { get; set; }
        [Required]
        public long sender_id { get; set; }
        public long? reply_to_message_id { get; set; }
        public string? message_type { get; set; }
        public string? content { get; set; }
        public string? media_url { get; set; }
        public string? file_name { get; set; }
        public long? file_size { get; set; }

    }
}
