using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using NETCore.MailKit.Core;
using System.Security.Claims;
using Zalo_mini.Data;
using Zalo_mini.Services;

namespace Zalo_mini.Controllers
{
    public class User_OtpController : Controller
    {
        private readonly IEmailServices _emailService;
        public User_OtpController(IEmailServices emailService)
        {
            _emailService = emailService;
        }
        [HttpPost("ExcecOtp")]
        public async Task<IActionResult> ExecOtp([FromBody] string email)
        {
            var execOtpResult = await _emailService.ExecSendOtpEmailAsync(email);
            return execOtpResult.ToActionResult();
        }
    }
}
