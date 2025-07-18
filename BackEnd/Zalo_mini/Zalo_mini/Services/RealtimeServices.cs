using Microsoft.AspNetCore.SignalR;
using Zalo_mini.Data;
using Zalo_mini.Hubs;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Services
{
    public interface IRealtimeServices
    {
        public Task SendMessage(MessageVM messageVM);
        public Task JoinConversation(string connectionId, string conversationId);
        public Task LeaveConversation(string connectionId, string conversationId);
        public Task Typing(string conversationId, string userId);
        public Task Seen(string conversationId, string userId, string lastSeenMessageId);
        public Task SendNotification(string conversationId, string userId);
        public Task Pin(string conversationId, string userId, string mesageId);
        public Task Reaction(string conversationId, string userId);
        public Task Recall(string conversationId, string userId);


    }
    public class RealtimeServices : IRealtimeServices
    {
        private readonly IHubContext<ChatHub> _hubContext;
        private readonly IMessageServices _messageServices;
        private readonly ConnectionMapping _connectionMapping;
        public RealtimeServices(IHubContext<ChatHub> hubContext, IMessageServices messageServices, ConnectionMapping connectionMapping)
        {
            _messageServices = messageServices;
            _hubContext = hubContext;
            _connectionMapping = connectionMapping;
        }
        public async Task JoinConversation(string connectionId, string conversationId)
        {
            await _hubContext.Groups.AddToGroupAsync(connectionId, $"conversation_{conversationId}");
        }


        public async Task LeaveConversation(string connectionId, string conversationId)
        {
            await _hubContext.Groups.RemoveFromGroupAsync(connectionId, $"conversation_{conversationId}");
        }

        public async Task Pin(string conversationId, string userId, string mesageId)
        {
            await _hubContext.Clients.Groups($"conversation_{conversationId}")
                .SendAsync("Pin", new MessageRealTimeVM
                {
                    UserId = userId,
                    MessageId = mesageId,
                    ConversationId = conversationId
                });
        }

        public Task Reaction(string conversationId, string userId)
        {
            throw new NotImplementedException();
        }

        public async Task Recall(string conversationId, string userId)
        {
            await _hubContext.Clients.Groups($"conversation_{conversationId}")
                .SendAsync("Recall", new MessageRealTimeVM
                {
                    ConversationId = conversationId,
                    UserId = userId
                });
        }

        public async Task Seen(string conversationId, string userId, string lastSeenMessageId)
        {
            await _hubContext.Clients.GroupExcept($"conversation_{conversationId}", userId)
                .SendAsync("Seen", new MessageRealTimeVM
                {
                    ConversationId = conversationId,
                    LastSeenMessageId = lastSeenMessageId
                });
        }

        public async Task SendMessage(MessageVM messageVM)
        {
            await _hubContext.Clients.Group($"conversation_{messageVM.conversation_id}").SendAsync("ReceiveMessage", messageVM);
        }

        public Task SendNotification(string conversationId, string userId)
        {
            throw new NotImplementedException();
        }

        public async Task Typing(string conversationId, string userId)
        {
            await _hubContext.Clients.Groups($"conversation_{conversationId}")
                .SendAsync("Typing", new MessageRealTimeVM
                {
                    UserId = userId,
                    ConversationId = conversationId
                });
        }
    }
}
