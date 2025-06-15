using System.ComponentModel.DataAnnotations;

namespace Zalo_mini.ViewModel
{
    public class ConversationCreateVM
    {
        [Required]
        public List<string> ParticipantIds { get; set; }
        }
}
