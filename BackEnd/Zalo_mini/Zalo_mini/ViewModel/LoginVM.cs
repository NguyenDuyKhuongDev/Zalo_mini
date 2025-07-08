using System.ComponentModel.DataAnnotations;

namespace Zalo_mini.ViewModel
{
    public class LoginVM
    {
        [Required]
        public string Email { get; set; }
        [Required]
        public string PhoneNumber { get; set; }
        [Required]
        public string Otp { get; set; }
        [Required]
        public string Device_id { get; set; }
        public string? Device_type { get; set; }

    }
}
