using EFCore.BulkExtensions;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Data.Common;
using System.Linq.Expressions;
using Zalo_mini.Data;
using Zalo_mini.Global_Exception;
using Zalo_mini.Models;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Repositories
{
    public interface IConversationRepositories
    {
        //tao cuoc noi chuyen 1-1
        public IServiceResult CreateDirectConversation();
        public IServiceResult DeleteConversation(long conversationId, long userId);
        public Task<IServiceResult> GetConversationAsync(long userId);

    }
    public class ConversationRepository : IConversationRepositories
    {
        private readonly AppDbContext _context;
        private readonly string? avatarDefaul;

        public ConversationRepository(AppDbContext context, IConfiguration configuration)
        {
            _context = context;
            avatarDefaul = configuration["avatarDefaul"] ?? null;
        }
        public IServiceResult CreateDirectConversation()
        {
            try
            {
                var newConversation = new conversation()
                {
                    conversation_type = Constants.ConversationType.direct.ToString(),
                    avatar_url = avatarDefaul,
                    is_active = true,
                    created_at = DateTime.UtcNow,
                };
                _context.conversations.Add(newConversation);
                _context.SaveChanges();
                return new ServiceResult()
                {
                    Status = StatusCodes.Status200OK,
                    Success = true,
                };
            }
            catch (DbException ex)
            {
                throw new DatabaseException("lỗi db không thể tạo cuộc trò chuyện", ex.InnerException.Message ?? ex.Message);
            }

        }


        // khi người dùng xóa cuộc trò chuyện thì chỉ ẩn phía người dùng đó , người dùng khác vẫn còn cuộc trò chuyện
        public IServiceResult DeleteConversation(long conversationId, long userId)
        {
            try
            {
                var conversation = _context.conversations.Find(conversationId);
                var user = _context.users.Find(userId);
                if (conversation == null || user == null) throw new NotFoundException("conversationId Or userId NoT Found");
                _context.converation_deleteds.Add(new conversation_deleted()
                {
                    ConversationId = conversationId,
                    UserId = userId,
                    DeletedAt = DateTime.UtcNow
                });
                _context.SaveChanges();
                return new ServiceResult()
                {
                    Status = StatusCodes.Status200OK,
                    Success = true
                };
            }
            catch (DbException ex)
            {
                throw new DatabaseException("lỗi liên quan tới db khi người dùng xóa cuộc trò chuyện", ex.InnerException.Message ?? ex.Message);
            }

        }

        public async Task<IServiceResult> GetConversationAsync(long userId)
        {
            var user = _context.users.Find(userId);
            if (user == null) return new ServiceResult() { Success = false, Status = StatusCodes.Status404NotFound, Message = "user khong ton tai" };
            try
            {
                var conversationsOfUser = await _context.conversation_participants
                    .Where(participant => participant.user_id == userId)
                    .Select(participant => participant.conversation)
                    .ToListAsync();
                return ServiceResult<List<conversation>>.Ok(null, StatusCodes.Status200OK, conversationsOfUser ?? new List<conversation>());
            }
            catch (DbException ex)
            {
                throw new DatabaseException("lỗi liên quan tới db khi người dùng get cuộc trò chuyện", ex.InnerException.Message ?? ex.Message);
            }



        }

           }
}
