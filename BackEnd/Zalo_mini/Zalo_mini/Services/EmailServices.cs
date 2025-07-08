using MailKit.Net.Smtp;
using MailKit.Security;
using MimeKit;
using Zalo_mini.Data;
using Zalo_mini.Models;

namespace Zalo_mini.Services
{
    public interface IEmailServices
    {
        public Task<IServiceResult> SendOtpEmailAsync(string email, string otp);
        public Task<IServiceResult> ExecSendOtpEmailAsync(string email);
        public Task<IServiceResult> VerifyOtpEmailAsync(string email, string otp);

    }
    public class EmailServices : IEmailServices
    {
        private readonly IConfiguration _configuration;
        private readonly AppDbContext _context;
        public EmailServices(IConfiguration configuration, AppDbContext context)
        {
            _configuration = configuration;
            _context = context;
        }

        public async Task<IServiceResult> ExecSendOtpEmailAsync(string email)
        {
            var otp = GenerateOpt();
            var otpExpireTime = int.TryParse(_configuration["EmailOTP:ExpireMinutes"], out int minute) ? minute : 5;
            var otpUser = new user_otps()
            {
                otp_code = otp,
                created_at = DateTime.UtcNow,
                email = email,
                isUsed = false,
                expired_at = DateTime.UtcNow.AddMinutes(otpExpireTime),
            };
            _context.user_otps.Add(otpUser);
            await _context.SaveChangesAsync();

            var result = await SendOtpEmailAsync(email, otp);
            return result;
        }

        public async Task<IServiceResult> SendOtpEmailAsync(string email, string otp)
        {
            if (string.IsNullOrEmpty(email) || string.IsNullOrEmpty(otp)) return new ServiceResult()
            {
                Success = false,
                Message = "Invalid email or otp",
                Status = 400
            };
            var message = new MimeMessage();
            message.From.Add(MailboxAddress.Parse(_configuration["Smtp:EmailSender"]));
            message.To.Add(MailboxAddress.Parse(email));
            message.Subject = "Your OTP Code";
            message.Body = new TextPart("plain") { Text = "Your OTP is " + otp };

            using var client = new SmtpClient();
            await client.ConnectAsync(_configuration["Smtp:Host"], int.Parse(_configuration["Smtp:Port"]), SecureSocketOptions.StartTls);
            await client.AuthenticateAsync(_configuration["Smtp:EmailSender"], _configuration["Smtp:AppPassword"]);
            await client.SendAsync(message);
            await client.DisconnectAsync(true);
            return new ServiceResult() { Success = true, Status = 200, Message = "Email sent opt successfully" };

        }

        public async Task<IServiceResult> VerifyOtpEmailAsync(string email, string otp)
        {
            if (string.IsNullOrEmpty(email) || string.IsNullOrEmpty(otp)) return new ServiceResult
            {
                Message = "Invalid email or otp",
                Status = 400,
                Success = false
            };
            var userOtp = _context.user_otps.FirstOrDefault(x => x.email == email && x.otp_code == otp);

            if (userOtp == null) return new ServiceResult { Message = "otp is not exist", Status = 404, Success = false };
            if (userOtp.isUsed) return new ServiceResult { Message = "otp is used", Status = 400, Success = false };
            if (userOtp.expired_at < DateTime.UtcNow) return new ServiceResult { Message = "otp is expired", Status = 400, Success = false };

            userOtp.isUsed = true;
            await _context.SaveChangesAsync();
            return new ServiceResult { Message = "otp is valid", Status = 200, Success = true };

        }

        private string GenerateOpt()
        {
            var random = new Random();
            var otp = "";
            for (int i = 0; i < 6; i++)
            {
                otp += random.Next(0, 9).ToString();
            }
            return otp;
        }
    }

}
