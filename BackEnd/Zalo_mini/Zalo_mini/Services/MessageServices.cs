using Zalo_mini.Data;
using Zalo_mini.Models;
using Zalo_mini.Repositories;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Services
{
    public interface IMessageServices
    {
        public Task<message> PinMessage(message message);
        public Task<message> RecallMessage(message message);
        public Task<message> ReactMessage(message message);
              public Task TyppingMessage(message message);
        public Task MessageSeen(message message);
        public Task SendMessage(message message);
        public Task SeenMessage(message message);
        /*  public Task IsSeenLatestMessage(string userId, string conversationId);*/
     

    }
    public class MessageServices : IMessageServices
    {
        private readonly IMessageRepositories _messageRepositories;
        private readonly IRealtimeServices _realtimeServices;
        private readonly AppDbContext _context;

        public MessageServices(AppDbContext context, IMessageRepositories messageRepositories, IRealtimeServices realtimeServices)
        {
            _context = context;
            _messageRepositories = messageRepositories;
            _realtimeServices = realtimeServices;
        }
       

        public Task MessageSeen(string mesageLatestSeenId)
        {
            throw new NotImplementedException();
        }

        public Task MessageSeen(message message)
        {
            throw new NotImplementedException();
        }

        public Task<message> PinMessage(message message)
        {
            throw new NotImplementedException();
        }

        public Task<message> ReactMessage(message message)
        {
            throw new NotImplementedException();
        }

        public Task<message> RecallMessage(message message)
        {
            throw new NotImplementedException();
        }

        public Task SeenMessage(message message)
        {
            throw new NotImplementedException();
        }

        public async Task<IServiceResult> SendMessage(MessageVM messageVM)
        {
            var createMessageResult = await _messageRepositories.CreateMessageAsync(messageVM);
            if (!createMessageResult.Success) return createMessageResult;

            await _realtimeServices.SendMessage(messageVM);
            return new ServiceResult() { Success = true, Status = 200, Message = "Send message successfully" };
        }

        public Task SendMessage(message message)
        {
            throw new NotImplementedException();
        }

        public Task TyppingMessage(message message)
        {
            throw new NotImplementedException();
        }
    }
}
