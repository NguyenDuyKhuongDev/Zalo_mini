using EFCore.BulkExtensions;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Zalo_mini.Data;
using Zalo_mini.Global_Exception;
using Zalo_mini.Models;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Repositories
{
    public interface IConversationParticipantRepositories
    {
        //trả về list những người add vào nhóm bị lỗi
        public Task<IServiceResult> AddParticipant(List<long> userIds, long conversationId);
        public Task<IServiceResult> RemoveParticipant(List<long> userIds, long conversationId);

    }
    public class ConversationParticipantRepository : IConversationParticipantRepositories
    {
        private readonly AppDbContext _context;
        public ConversationParticipantRepository(AppDbContext context)
        {
            _context = context;
        }
        public async Task<IServiceResult> AddParticipant(List<long> userIds, long conversationId)
        {
            var conversation = await _context.conversations.FindAsync(conversationId);
            if (conversation == null) return new ServiceResult()
            {
                Message = "conversationId NoT Found",
                Success = false,
                Status = StatusCodes.Status400BadRequest
            };

            var userIdValidToAdds = GetUsersValid(userIds);
            if (userIdValidToAdds.Count == 0) return new ServiceResult()
            {
                Success = false,
                Message = "There are no User Valid to add to Conversation ",
                Status = StatusCodes.Status400BadRequest
            };

            var usersReadyToAdd = userIdValidToAdds.Select(userId => new conversation_participant()
            {
                conversation_id = conversationId,
                user_id = userId,
                joined_at = DateTime.UtcNow,
            }).ToList();
            await _context.BulkInsertAsync(usersReadyToAdd);
            return new ServiceResult<List<long>>()
            {
                Success = true,
                Status = StatusCodes.Status200OK,
            };
        }

        public async Task<IServiceResult> RemoveParticipant(List<long> userIds, long conversationId)
        {
            try
            {

                var conversation = _context.conversations.Find(conversationId);
                if (conversation == null) return new ServiceResult() { Message = "conversation NoT Found", Success = false, Status = StatusCodes.Status400BadRequest };
                var userIdValidToRemove = GetUsersValidToRemove(userIds, conversationId);
                var userReadyToRemove = _context.conversation_participants.Where(x => userIdValidToRemove.Contains(x.user_id)).ToList();
                /* _context.conversation_participants.RemoveRange(userReadyToRemove);*/
                //_context.SaveChanges();
                if (userReadyToRemove.Count == 0) return new ServiceResult()
                {
                    Success = false,
                    Message = "There is no user valid to Remove",
                    Status = StatusCodes.Status400BadRequest
                };
                await _context.BulkDeleteAsync(userReadyToRemove);
                return new ServiceResult()
                {
                    Success = true,
                    Status = StatusCodes.Status200OK
                };

            }
            catch (DbUpdateException ex)
            {
                throw new DatabaseException("lỗi liên quan tới db khi người dùng thêm người vào cuộc trò chuyện", ex.InnerException.Message ?? ex.Message);
            }
        }

        private List<long> GetUsersValid(List<long> userIds)
        {
            List<long> userValids = new List<long>();
            foreach (var userId in userIds)
            {
                var user = _context.users.Find(userId);
                if (user != null) userValids.Add(userId);
            }
            return userValids;
        }

        private List<long> GetUserExistInConversation(long conversationId)
        {
            var participantIds = _context.conversation_participants.Where(x => x.conversation_id == conversationId).Select(x => x.user_id);
            return (List<long>)participantIds;
        }
        private List<long> GetUsersValidToRemove(List<long> userIds, long conversationId)
        {
            var userValid = new List<long>();
            var usersExsitedInConversation = GetUserExistInConversation(conversationId);
            foreach (var userId in userIds)
            {
                if (usersExsitedInConversation.Contains(userId)) userValid.Add(userId);
            }
            return userValid;
        }



    }
}
