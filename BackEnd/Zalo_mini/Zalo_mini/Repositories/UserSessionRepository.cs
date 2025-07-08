using Microsoft.EntityFrameworkCore;
using Zalo_mini.Data;
using Zalo_mini.Models;
using Zalo_mini.Services;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Repositories
{
    public interface IUserSessionRepository
    {
        public Task<IServiceResult> CreateSessionAsync(RegisterVM registerVM, user user);
        public Task<IServiceResult> UpdateSessionAsync(long userSessionId, string acessToken, string refreshToken);
    }
    public class UserSessionRepository : IUserSessionRepository
    {
        private readonly AppDbContext _context;
        private readonly ITokenServices _tokenServices;
        private readonly IConfiguration _configuration;

        public UserSessionRepository(AppDbContext context, ITokenServices tokenServices, IConfiguration configuration)
        {
            _context = context;
            _tokenServices = tokenServices;
            _configuration = configuration;
        }
        public async Task<IServiceResult> CreateSessionAsync(RegisterVM registerVM, user user)
        {
            if (registerVM == null || user == null) return new ServiceResult()
            {
                Success = false,
                Status = StatusCodes.Status404NotFound,
                Message = "user hoac registerVM khong ton tai"
            };
            if (string.IsNullOrEmpty(registerVM.Device_id) || string.IsNullOrEmpty(registerVM.Device_type)) return new ServiceResult()
            {
                Message = "device_id hoac device_type khong dc de trong",
                Success = false,
                Status = StatusCodes.Status400BadRequest
            };

            var expireTimeRefreskToken = int.TryParse(_configuration["RefreshTokenExpireDays"], out int minutes) ? minutes : 23;
            var userSession = new user_session()
            {
                access_token = _tokenServices.GenerateToken(user),
                refresh_token = _tokenServices.GenerateRefreshTokenAsync(),
                created_at = DateTime.UtcNow,
                device_id = registerVM.Device_id,
                device_type = registerVM.Device_type,
                expires_at = DateTime.UtcNow.AddDays(expireTimeRefreskToken),
                is_active = true,
                user_id = user.id,
            };
            _context.user_sessions.Add(userSession);
            await _context.SaveChangesAsync();
            return ServiceResult<TokenVM>.Ok("create session thanh cong", StatusCodes.Status200OK, new TokenVM() { AcessToken = userSession.access_token, RefreshToken = userSession.refresh_token });
        }

        public async Task<IServiceResult> UpdateSessionAsync(long userSessionId, string acessToken, string refreshToken)
        {
            var userSession = await _context.user_sessions.FirstOrDefaultAsync(x => x.id == userSessionId);
            if (string.IsNullOrEmpty(acessToken) || string.IsNullOrEmpty(refreshToken)) return new ServiceResult()
            {
                Message = "acessToken hoac refreshToken null",
                Success = false,
                Status = StatusCodes.Status400BadRequest
            };

            if (userSession == null) return new ServiceResult()
            {
                Message = "userSession is not exist",
                Success = false,
                Status = StatusCodes.Status400BadRequest
            };

            var expireTimeRefreskToken = int.TryParse(_configuration["RefreshTokenExpireDays"], out int day) ? day : 23;


            userSession.access_token = acessToken;
            userSession.refresh_token = refreshToken;
            userSession.expires_at = DateTime.UtcNow.AddDays(expireTimeRefreskToken);
            userSession.created_at = DateTime.UtcNow;
            userSession.is_active = true;
            await _context.SaveChangesAsync();

            return  ServiceResult<TokenVM>.Ok("update session thanh cong", StatusCodes.Status200OK, new TokenVM() { AcessToken = acessToken, RefreshToken = refreshToken });
        }


    }
}
