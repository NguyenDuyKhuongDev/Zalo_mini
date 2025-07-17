namespace Zalo_mini.ViewModel
{
    public class MessageRealTimeVM
    {
        public string? UserId { get; set; }
        public string? ConversationId { get; set; }
        public string? MessageId { get; set; }
        public string? LastSeenMessageId { get; set; } = null;
    }
}
