using Zalo_mini.Models;

namespace Zalo_mini.Services
{
    public interface IMessageServices { 
    public Task<message> PinMessage(message message);
        public Task<message> RecallMessage(message message);
        public Task<message> ReactMessage(message message);
        public Task CopyMessage(message message);

    }
    public class MessageServices : IMessageServices
    {
        public Task CopyMessage(message message)
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
    }
}
