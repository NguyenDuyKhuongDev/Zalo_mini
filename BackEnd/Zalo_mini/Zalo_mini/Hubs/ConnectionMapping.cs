using Zalo_mini.Models;

namespace Zalo_mini.Hubs
{
    public class ConnectionMapping
    {
        private readonly Dictionary<string, HashSet<string>> _userConnections = new();
        private readonly Dictionary<string, HashSet<string>> _conversationConnnections = new();
        private readonly object _lock = new();

        public void Add(string userId, string connectionId, string conversationId)
        {
            lock (_lock)
            {
                if (!_userConnections.TryGetValue(userId, out var user_connections))
                {
                    user_connections = new HashSet<string>();
                    _userConnections[userId] = user_connections;
                }
                if (!_conversationConnnections.TryGetValue(conversationId, out var connections))
                {
                    connections = new HashSet<string>();
                    _conversationConnnections[conversationId] = connections;
                }
                user_connections.Add(connectionId);
                connections.Add(connectionId);
            }
        }

        public void Remove(string userId, string connectionId)
        {
            lock (_lock)
            {
                if (_userConnections.TryGetValue(userId, out var user_connections))
                {
                    user_connections.Remove(connectionId);
                    if (user_connections.Count == 0) _userConnections.Remove(userId);
                }
                var conversationId = _conversationConnnections.FirstOrDefault(x => x.Value.Contains(connectionId)).Key;
                if (_conversationConnnections.TryGetValue(conversationId, out var connections))
                {
                    connections.Remove(connectionId);
                    if (connections.Count == 0) _conversationConnnections.Remove(conversationId);
                }
            }
        }

        public IEnumerable<string> GetUserConnections(string userId)
        {
            lock (_lock)
            {
                return _userConnections.TryGetValue(userId, out var connections) ? connections.ToList() : Enumerable.Empty<string>();
            }

        }

        public IEnumerable<string> GetUserOnlinesInConversation(string conversationId)
        {
            lock (_lock)
            {
                return _conversationConnnections.TryGetValue(conversationId, out var userIds) ? userIds.ToList() : Enumerable.Empty<string>();
            }
        }
    }
}
