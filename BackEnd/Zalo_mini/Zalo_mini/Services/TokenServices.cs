using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using Zalo_mini.Data;
using Zalo_mini.Models;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Services
{
    public interface ITokenServices
    {
        public string GenerateToken(user user);
        public string GenerateRefreshTokenAsync();
           }
    public class TokenServices : ITokenServices
    {
        private readonly IConfiguration _config;
        private readonly AppDbContext _context;
        public TokenServices(AppDbContext context, IConfiguration config)
        {
            _config = config;
            _context = context;
        }
        public string GenerateToken(user user)
        {
            var claims = new[] {
         new Claim(ClaimTypes.NameIdentifier,user.id.ToString()),
        new Claim(ClaimTypes.Email,user.email),
         new Claim(ClaimTypes.MobilePhone,user.phone_number)
};
            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["Jwt:SecretKey"]));
            var cred = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
            var expireTime = int.TryParse(_config["Jwt:AcessTokenExpireMinutes"], out int minutes) ? minutes : 23;
            var token = new JwtSecurityToken(
                  issuer: _config["Jwt:Issuer"],
                  audience: _config["Jwt:Audience"],
                  claims: claims,
                   expires: DateTime.UtcNow.AddMinutes(expireTime),
                  signingCredentials: cred);
            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        public string GenerateRefreshTokenAsync() => Convert.ToBase64String(RandomNumberGenerator.GetBytes(48));

    }
}
