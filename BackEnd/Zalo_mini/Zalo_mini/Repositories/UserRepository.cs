using Microsoft.EntityFrameworkCore;
using Zalo_mini.Data;
using Zalo_mini.Models;
using Zalo_mini.ViewModel;

namespace Zalo_mini.Repositories
{
    public interface IUserRepository
    {
        public Task <IServiceResult> CreateUserAsync(RegisterVM registerVM);

    }
    public class UserRepository : IUserRepository
    {
        private readonly AppDbContext _context;
        public UserRepository(AppDbContext context)
        {
            _context = context;
        }
        public async Task<IServiceResult> CreateUserAsync(RegisterVM registerVM)
        {
            if (string.IsNullOrEmpty(registerVM.Email) || string.IsNullOrEmpty(registerVM.PhoneNumber))
                return new ServiceResult()
                {
                    Success = false,
                    Status = 400,
                    Message = " Email or Phone number MUST NOT BE NULL"
                };
            var checkUserExist = _context.users.FirstOrDefault(x => x.phone_number == registerVM.PhoneNumber);
            if (checkUserExist != null) return new ServiceResult()
            {
                Message = "Phone number already used",
                Success = false,
                Status = 400
            };
            var registerUser = new user()
            {
                email = registerVM.Email,
                phone_number = registerVM.PhoneNumber,
                created_at = DateTime.UtcNow,
                display_name = (registerVM.DisplayName != null) ? registerVM.DisplayName : registerVM.PhoneNumber,
                is_active = true,
                is_blocked = false,
            };
            _context.users.Add(registerUser);
            await _context.SaveChangesAsync();

            return ServiceResult<user>.Ok("Create user successfully", 200, registerUser);
        }
    }
}
