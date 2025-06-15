namespace Zalo_mini.Models
{
    public class conversation_deleted
    {
        public long Id { get; set; }
        public long ConversationId { get; set; }
        public long UserId { get; set; }
        public DateTime? DeletedAt { get; set; }

        public virtual conversation Conversation { get; set; } = null!;
        public virtual user User { get; set; } = null!;
    }
}
