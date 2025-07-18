using Zalo_mini.Data;
using Zalo_mini.Repositories;

namespace Zalo_mini.Services
{
    public interface IMessageReadStatusServices
    {
        public Task<IServiceResult> CreateOrUpdateMessageReadStatus(string messageId, long userId);

    }
    public class MessageReadStatusServices : IMessageReadStatusServices
    {
        private readonly AppDbContext _context;
        private readonly IMessageReadStatusRepository _messageReadStatusRepository;
        public MessageReadStatusServices(AppDbContext context)
        {
            _context = context;
        }
        public Task<IServiceResult> CreateOrUpdateMessageReadStatus(string messageId, long userId)
        {
            var messageReadStatus = _context.message_read_statuses.FirstOrDefault(x => x.message_id == long.Parse(messageId) && x.user_id == userId);
            if (messageReadStatus == null) return _messageReadStatusRepository.CreateMessageReadStatus(messageId, userId);
            return _messageReadStatusRepository.UpdateMessageReadStatus(messageId, userId);
        }
    }
}
