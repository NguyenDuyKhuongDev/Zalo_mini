using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;
using System.Security.Claims;
using Zalo_mini.Data;
using Zalo_mini.Services;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthControllers : ControllerBase
    {
        private readonly IAuthServices _authServices;
        public AuthControllers(IAuthServices authServices)
        {
            _authServices = authServices;
        }

        [HttpPost("Login")]
        public async Task<IActionResult> Login([FromBody] LoginVM loginVM)
        {
            var loginResult = await _authServices.Login(loginVM) ;
            return loginResult.ToActionResult();

        }
        [HttpPost("Register")]
        public async Task<IActionResult> Register([FromBody] RegisterVM registerVM)
        {
            var registerResult = await _authServices.Register(registerVM);
            return registerResult.ToActionResult();

        }
        [HttpPost("RefreshToken")]
        [SwaggerOperation(Description = "Được gọi khi người dùng đăng nhập khi refresh token vẫn còn hạn và accesstoken đã hết hạn",Summary ="input : refeshToken")]
        public async Task<IActionResult> RefreshToken([FromBody] string refreshToken)
        {
            var refreshTokenResult = await _authServices.RefreshToken(refreshToken);
            return refreshTokenResult.ToActionResult();
        }

        [Authorize]
        [SwaggerOperation(Description = "INPUT : deviceId - logout khỏi thiết bị hiện tại")]    
        [HttpPost("Logout")]
        public async Task<IActionResult> Logout([FromBody] string deviceId)
        {
            var userId = long.TryParse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value, out long DefaultErrorId) ? DefaultErrorId : 0;
            var logoutResult = await _authServices.Logout(deviceId, userId);
            return logoutResult.ToActionResult();
        }

        [Authorize]
        [SwaggerOperation(Description = "INPUT : deviceId - logout khỏi thiết bị hiện tại")]
        [HttpPost("LogoutFromAllDevices")]
               public async Task<IActionResult> LogoutFromAllDevices()
        {
            var userId = long.TryParse(User.FindFirst(ClaimTypes.NameIdentifier)?.Value, out long DefaultErrorId) ? DefaultErrorId : 0;
            var logoutResult = await _authServices.LogoutFromAllDevices(userId);
            return logoutResult.ToActionResult();
        }





    }
}
