using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using Zalo_mini.Constants;
using Zalo_mini.Data;
using Zalo_mini.Repositories;

namespace Zalo_mini.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ConversationController : ControllerBase
    {
        private readonly IConversationRepositories _conversationRepositories;
        public ConversationController(IConversationRepositories conversationRepositories)
        {
            _conversationRepositories = conversationRepositories;
        }
        //ERRRORR : userid sẽ được lấy từ token nhưng chưa có phần authen nên để tạm cho đỡ lỗi compile
        [HttpGet("GetConversations")]
        public async Task<IActionResult> GetConversationAsync([FromBody] long userId)
        {
          //  var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var result = await _conversationRepositories.GetConversationAsync(userId);
            return result.ToActionResult();
        }

       

    }
}
