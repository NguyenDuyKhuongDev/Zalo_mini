using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage;
using System.Data.Common;
using System.Linq.Expressions;
using Zalo_mini.Data;
using Zalo_mini.Global_Exception;
using Zalo_mini.Models;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Repositories
{
    public interface IMessageRepositories
    {
        public Task<IServiceResult> CreateMessageAsync(MessageVM messageVm);
        public Task<IServiceResult> DeleteMessageForMeAsync(long messageId);
        public Task<IServiceResult> DeleteMessageForEveryoneAsync(long messageId);
        public Task<IServiceResult> GetMessagesAsync(long conversationId, long userId, int numberOfMessages);
    }
    public class MessageRepositories : IMessageRepositories
    {
        private readonly AppDbContext _context;
        public MessageRepositories(AppDbContext context)
        {
            _context = context;
        }
        public async Task<IServiceResult> CreateMessageAsync(MessageVM messageVm)
        {
            try
            {
                IServiceResult result = new ServiceResult();
                switch (messageVm.message_type)
                {
                    case Constants.MessageType.Text: result = await CreateTextmessageAsync(messageVm); break;
                    case Constants.MessageType.Sticker: result = await CreateStickerMessageAsync(messageVm); break;
                    default:
                        result = new ServiceResult()
                        {
                            Status = StatusCodes.Status400BadRequest,
                            Success = false,
                            Message = "Kiểu tin nhắn không hỗ trợ"
                        };
                        break;
                }
                return result;
            }
            catch (DbException ex)
            {
                throw new DatabaseException("lỗi liên quan tới db khi người dùng tạo tin nhắn", ex.InnerException.Message ?? ex.Message);
            }
        }

        private async Task<IServiceResult> CreateTextmessageAsync(MessageVM messageVm)
        {
            if (string.IsNullOrEmpty(messageVm.content)) return new ServiceResult()
            {
                Status = StatusCodes.Status400BadRequest,
                Success = false,
                Message = "Nội dung không được để trống"
            };
            await _context.messages.AddAsync(new message()
            {
                content = messageVm.content,
                conversation_id = messageVm.conversation_id,
                sender_id = messageVm.sender_id,
                created_at = DateTime.UtcNow,
                message_type = messageVm.message_type,
                reply_to_message_id = messageVm.reply_to_message_id
            });
            await _context.SaveChangesAsync();
            return new ServiceResult() { Status = StatusCodes.Status200OK, Success = true };
        }
        private async Task<IServiceResult> CreateStickerMessageAsync(MessageVM messageVm)
        {
            if (string.IsNullOrEmpty(messageVm.file_name)) return new ServiceResult()
            {
                Success = false,
                Status = StatusCodes.Status400BadRequest,
                Message = "sticker name khong dc de trong"
            };

            await _context.messages.AddAsync(new message()
            {
                file_name = messageVm.file_name,
                conversation_id = messageVm.conversation_id,
                sender_id = messageVm.sender_id,
                reply_to_message_id = messageVm.reply_to_message_id,
                message_type = messageVm.message_type,
                created_at = DateTime.UtcNow,
            });
            await _context.SaveChangesAsync();
            return new ServiceResult() { Status = StatusCodes.Status200OK, Success = true };
        }

        public async Task<IServiceResult> DeleteMessageForMeAsync(long messageId)
        {
            try
            {
                var messageReadyToDelete = _context.messages.Find(messageId);
                if (messageReadyToDelete == null) return new ServiceResult()
                {
                    Success = false,
                    Status = StatusCodes.Status400BadRequest,
                    Message = "messageId NoT Found"
                };
                await _context.message_deletions.AddAsync(new message_deletion()
                {
                    message_id = messageId,
                    user_id = messageReadyToDelete.sender_id,
                    deleted_at = DateTime.UtcNow
                });
                await _context.SaveChangesAsync();
                return new ServiceResult() { Success = true, Status = StatusCodes.Status200OK };
            }
            catch (DbException ex)
            {
                throw new DatabaseException("lỗi liên quan tới db khi người dùng lấy danh sách tin nhắn ", ex.InnerException.Message ?? ex.Message);
            }
         ;
        }



        public async Task<IServiceResult> DeleteMessageForEveryoneAsync(long messageId)
        {
            try
            {
                var messageReadyToDelete = await _context.messages.FindAsync(messageId);
                if (messageReadyToDelete == null) return new ServiceResult()
                {
                    Success = false,
                    Status = StatusCodes.Status404NotFound,
                    Message = "tin nhan khong ton tai"
                };
                _context.Remove(messageReadyToDelete);
                await _context.SaveChangesAsync();
                return new ServiceResult() { Success = true, Status = StatusCodes.Status200OK };
            }
            catch (DbException ex)
            {
                throw new DatabaseException("lỗi liên quan tới db khi người dùng xóa tin nhắn", ex.InnerException.Message ?? ex.Message);
            }
        }
        //hàm này lấy tin nhắn mới nhất trong cuộc trò chuyện
        public async Task<IServiceResult> GetMessagesAsync(long conversationId, long userId, int numberOfMessages)
        {
            try
            {
                var user = await _context.conversation_participants.FindAsync(userId);
                var conversation = await _context.conversations.FindAsync(conversationId);
                if (user == null || conversation == null) return new ServiceResult()
                {
                    Success = false,
                    Status = StatusCodes.Status404NotFound,
                    Message = "user hoac cuoc tro chuyen khong tim thay"
                };

                var newestMessage = await _context.messages
                    .Where(message => message.conversation_id == conversationId)
                    .OrderByDescending(message => message.created_at)
                    .Take(numberOfMessages)
                    .ToListAsync();

                return ServiceResult<List<message>>.Ok(null, StatusCodes.Status200OK, newestMessage ?? new List<message>());
            }
            catch (DbException ex)
            {
                throw new DatabaseException("lỗi liên quan tới db khi người dùng lấy danh sách tin nhắn ", ex.InnerException.Message ?? ex.Message);
            }
        }
    }

}

