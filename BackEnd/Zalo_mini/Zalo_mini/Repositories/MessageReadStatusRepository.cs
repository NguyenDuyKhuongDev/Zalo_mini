using Microsoft.EntityFrameworkCore.Storage.ValueConversion.Internal;
using Zalo_mini.Data;
using Zalo_mini.Models;

namespace Zalo_mini.Repositories
{
    public interface IMessageReadStatusRepository
    {
        public Task<IServiceResult> CreateMessageReadStatus(string messageId, long userId);
        public Task<IServiceResult> UpdateMessageReadStatus(string messageId, long userId);

    }
    public class MessageReadStatusRepository : IMessageReadStatusRepository
    {
        private readonly AppDbContext _context;

        public MessageReadStatusRepository(AppDbContext context)
        {
            _context = context;
        }
        public async Task<IServiceResult> CreateMessageReadStatus(string messageId, long userId)
        {

            var checkValidResult =  IsValid(messageId, userId);
            if (checkValidResult.Success == false) return checkValidResult;
            _context.message_read_statuses.Add(new message_read_status()
            {
                message_id = long.TryParse(messageId, out long messageIdInt) ? messageIdInt : 0,
                read_at = DateTime.UtcNow,
                user_id = userId,
            });
            return new ServiceResult
            {
                Message = "Create message read status successfully",
                Status = 200,
                Success = true
            };
        }

        public async Task<IServiceResult> UpdateMessageReadStatus(string messageId, long userId)
        {

            var checkValidResult =  IsValid(messageId, userId);
            if (checkValidResult.Success == false) return checkValidResult;

            var messageReadStatus = _context.message_read_statuses.FirstOrDefault(x => x.message_id == long.Parse(messageId) && x.user_id == userId);
            if (messageReadStatus == null) return new ServiceResult { Message = "Message read status not found", Status = 404, Success = false };
            
            messageReadStatus.read_at = DateTime.UtcNow;
            messageReadStatus.message_id =long.Parse(messageId);

            await _context.SaveChangesAsync();
            return new ServiceResult
            {
                Message = "Update message read status successfully",
                Status = 200,
                Success = true
            };
        }
        public IServiceResult IsValid(string messageId, long userId)
        {
            if (string.IsNullOrEmpty(messageId) || userId <= 0)
                return new ServiceResult
                {
                    Message = "Invalid message id or user id",
                    Status = 400,
                    Success = false
                };

            var message = _context.messages.FirstOrDefault(x => x.id == long.Parse(messageId));
            if (message == null) return new ServiceResult
            {
                Message = "Message not found",
                Status = 404,
                Success = false
            };

            var user = _context.users.FirstOrDefault(x => x.id == userId);
            if (user == null) return new ServiceResult
            {
                Message = "User not found",
                Status = 404,
                Success = false
            };
            return new ServiceResult { Message = "Valid", Status = 200, Success = true };
        }
        public bool IsMessageHasBeenRead(long messageId)
        {
            var result = _context.message_read_statuses.FirstOrDefault(x => x.message_id == messageId);
            if (result != null) return true;
            return false;
        }
    }
}
