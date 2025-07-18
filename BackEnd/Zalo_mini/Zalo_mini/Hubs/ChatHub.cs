using Microsoft.AspNetCore.SignalR;
using System.Security.Cryptography.X509Certificates;
using Zalo_mini.Models;
using Zalo_mini.Services;
using Zalo_mini.ViewModel;
namespace Zalo_mini.Hubs
{
    public class ChatHub : Hub
    {
        private readonly IMessageServices _messageServices;
        private readonly IRealtimeServices _realtimeServices;
        private readonly ConnectionMapping _connectionMapping;
        public ChatHub(IMessageServices messageServices, ConnectionMapping connectionMapping, IRealtimeServices realtimeServices)
        {
            _messageServices = messageServices;
            _connectionMapping = connectionMapping;
            _realtimeServices = realtimeServices;
        }
        public override async Task OnConnectedAsync()
        {
            await base.OnConnectedAsync();
        }
        public async Task InitConnectionAsync(string conversationId)
        {
            //UserIdentifier là atrubute của signalr đại diện cho id người dùng hiện tại
            //mặc định nó lấy claim nameidentifyer trên token (có thể tùy chỉnh)
            var userId = Context.UserIdentifier;
            if (string.IsNullOrEmpty(userId)) _connectionMapping.Add(userId, Context.ConnectionId, conversationId);
        }
        public override async Task OnDisconnectedAsync(Exception? exception)
        {
            var userId = Context.UserIdentifier;
            if (string.IsNullOrEmpty(userId)) _connectionMapping.Remove(userId, Context.ConnectionId);
            await base.OnDisconnectedAsync(exception);

        }

        public Task JoinConversation(string conversationId)
            => _realtimeServices.JoinConversation(conversationId, Context.ConnectionId);

        public Task LeaveConversation(string conversationId)
            => _realtimeServices.LeaveConversation(conversationId, Context.ConnectionId);
    }
}

