using Microsoft.AspNetCore.Authorization;
using Microsoft.EntityFrameworkCore;
using System.Diagnostics;
using System.Security.Claims;
using Zalo_mini.Data;
using Zalo_mini.Models;
using Zalo_mini.Repositories;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Services
{
    public interface IAuthServices
    {
        public Task<IServiceResult> Login(LoginVM loginVM);
        public Task<IServiceResult> Register(RegisterVM registerVM);
        public Task<IServiceResult> Logout(string deviceId, long userId);
        public Task<IServiceResult> LogoutFromAllDevices(long userId);

        public Task<IServiceResult> RefreshToken(string refreshToken);

    }
    public class AuthServices : IAuthServices
    {
        private readonly AppDbContext _context;
        private readonly IEmailServices _emailServices;
        private readonly ITokenServices _tokenServices;
        private readonly IConfiguration _configuration;
        private readonly IUserRepository _userRepository;
        private readonly IUserSessionRepository _userSessionRepository;
        public AuthServices(AppDbContext context, IEmailServices emailServices, ITokenServices tokenServices, IConfiguration configuration, IUserRepository userRepository, IUserSessionRepository userSessionRepository)
        {
            _context = context;
            _emailServices = emailServices;
            _tokenServices = tokenServices;
            _configuration = configuration;
            _userRepository = userRepository;
            _userSessionRepository = userSessionRepository;
        }
        //có 3 trường hợp login : + accesstoken còn => login luôn
        //                        + acesstoken hết hạn refresh token còn  -> gọi api Rereshtoken ở bên token controller
        //                        + acesstoken hết hạn refresh token hết hạn -> gọi api login bên dưới
        //hay nói cạc khác thì hàm này sẽ cần otp từ gmail để refresh token
        public async Task<IServiceResult> Login(LoginVM loginVM)
        {
            //chưa có tài khoản , trên api thì chuyển hướng sang đăng kí
            var user = await _context.users.FirstOrDefaultAsync(x => x.phone_number == loginVM.PhoneNumber);
            if (user == null) return new ServiceResult() { Message = "khong ton tai tai khoan nay", Status = 404, Success = false };

            var newAcessToken = _tokenServices.GenerateToken(user);
            var refreshToken = _tokenServices.GenerateRefreshTokenAsync();
            //nếu chưa có session -> có thể do đăng nhập 1 thiết bị khác -> tạo session mới trên thiết bị mới
            var userSession = await _context.user_sessions.FirstOrDefaultAsync(x => x.device_id == loginVM.Device_id && x.user_id == user.id);
            if (userSession == null)
            {
                var infomationSession = new RegisterVM
                {
                    Device_id = loginVM.Device_id,
                    Device_type = loginVM.Device_type,
                    DisplayName = loginVM.PhoneNumber,
                    Email = loginVM.Email,
                    PhoneNumber = loginVM.PhoneNumber
                };

                var resultCreateUserSession = await _userSessionRepository.CreateSessionAsync(infomationSession, user);
                if (!resultCreateUserSession.Success) return resultCreateUserSession;
                //gửi trả token lên cho app mobile lưu trên app mobile
                return ServiceResult<TokenVM>.Ok("Login successfully", 200, new TokenVM { AcessToken = newAcessToken, RefreshToken = refreshToken });
            }
            //check otp trên gmail
            var checkOtpResult = await _emailServices.VerifyOtpEmailAsync(loginVM.Email, loginVM.Otp);
            if (!checkOtpResult.Success) return new ServiceResult() { Message = checkOtpResult.Message, Status = 400, Success = false };
            //nếu session đã tồn tại update token mới cho session
            var resultUpdateUserSession = await _userSessionRepository.UpdateSessionAsync(userSession.id, newAcessToken, refreshToken);
            if (resultUpdateUserSession.Success == false) return resultUpdateUserSession;

            return ServiceResult<TokenVM>.Ok("Login successfully", 200, new TokenVM { AcessToken = newAcessToken, RefreshToken = refreshToken });

        }


        //nếu như acesstoken chưa hết thì hiện button logout trên mobile để gọi service này 
        //truyền userid thôgn qua claim của token hiện tại trên controller thông qua [Authorize] 
        public async Task<IServiceResult> Logout(string deviceId, long userId)
        {
            //thông thường  claims trong token =0 là invalid , có thể do lỗi decode , thiếu claims ..vv
            if (string.IsNullOrEmpty(deviceId) || userId == 0) return new ServiceResult() { Message = "DeviceId is null or userId get from token is =0 ", Status = 400, Success = false };
            var userSession = await _context.user_sessions.FirstOrDefaultAsync(x => x.user_id == userId && x.device_id == deviceId);
            if (userSession == null) return new ServiceResult() { Message = "This user session is not exist", Status = 404, Success = false };
            _context.Remove(userSession);
            _context.SaveChanges();
            return new ServiceResult() { Message = "Logout successfully", Status = 200, Success = true };
        }

        public async Task<IServiceResult> LogoutFromAllDevices(long userId)
        {
            if (userId == 0) return new ServiceResult() { Message = "there are some problem from get user id fromtoken - userId should not equal to  0 ", Status = 400, Success = false };
            var userSessions = _context.user_sessions.Where(x => x.user_id == userId).ToList();
            if (userSessions.Count != 0 && userSessions != null)
            {
                _context.RemoveRange(userSessions);
                await _context.SaveChangesAsync();
            }
            return new ServiceResult() { Message = "Logout from all devices successfully", Status = 200, Success = true };

        }
        //trên mobile app sẽ check token nếu acesstoken hết hạn -> tự động refresh nếu refreshtoken còn hạn
        public async Task<IServiceResult> RefreshToken(string refreshToken)
        {
            if (string.IsNullOrEmpty(refreshToken)) return new ServiceResult() { Message = "Invalid refresh token", Status = 400, Success = false };
            var userSession = await _context.user_sessions.FirstOrDefaultAsync(x => x.refresh_token == refreshToken);
            var user = await _context.users.FirstOrDefaultAsync(x=>x.id==userSession.user_id);

            var newAcessToken = _tokenServices.GenerateToken(user);
            var newRefreshToken = _tokenServices.GenerateRefreshTokenAsync();

            var resultUpdateUserSession = await _userSessionRepository.UpdateSessionAsync(userSession.id, newAcessToken, newRefreshToken);
            if (!resultUpdateUserSession.Success) return resultUpdateUserSession;

            return ServiceResult<TokenVM>.Ok("Refresh token successfully", 200, new TokenVM { AcessToken = newAcessToken, RefreshToken = newRefreshToken });


        }

        public async Task<IServiceResult> Register(RegisterVM registerVM)
        {
            var resultCheckOtp = await _emailServices.VerifyOtpEmailAsync(registerVM.Email, registerVM.Otp);
            if (!resultCheckOtp.Success) return resultCheckOtp;

            var createUserResult = await _userRepository.CreateUserAsync(registerVM);
            if (!createUserResult.Success) return createUserResult;
            var createUserResultSucess = createUserResult as ServiceResult<user>;


            var createUserSession = await _userSessionRepository.CreateSessionAsync(registerVM, createUserResultSucess.Datas);
            if (!createUserSession.Success) return createUserSession;
            var createUserSessionSucess = createUserSession as ServiceResult<TokenVM>;
           
            return ServiceResult<TokenVM>.Ok("Register Successfully", 200, new TokenVM { AcessToken = createUserSessionSucess.Datas.AcessToken, RefreshToken = createUserSessionSucess.Datas.RefreshToken });
        }
    }
}
