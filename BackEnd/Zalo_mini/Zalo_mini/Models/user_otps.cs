using System.CodeDom.Compiler;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Zalo_mini.Models
{
    public class user_otps
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public long id { get; set; }
        [Required]
        public string email { get; set; }
        [Required]
        public string otp_code { get; set; }
        public DateTime? expired_at { get; set; }
        public bool isUsed { get; set; } = false;
        public DateTime? created_at { get; set; }
      
    }
}
