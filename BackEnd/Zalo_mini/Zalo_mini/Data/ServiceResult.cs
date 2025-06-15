using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace Zalo_mini.Data
{
    // Class này có tác dụng chuyển 1 chuẩn trả về chứa status của method và data(tùy hàm) lên controller
    public interface IServiceResult
    {
        public bool Success { get; }
        public string? Message { get; }
        public int Status { get; }
    }
    //dùng class này khi chỉ hàm ko yêu cầu trả về data
    public class ServiceResult : IServiceResult
    {
        public bool Success { get; set; }
        public string? Message { get; set; }
        public int Status { get; set; }

    }
    //dùng hàm này để trả về data và status cơ bản
    public class ServiceResult<T> : IServiceResult
    {
        public bool Success { get; set; }
        public string? Message { get; set; }
        public int Status { get; set; }
        public T? Errors { get; set; }
        public T? Datas { get; set; }

        //Factory helper

        public static ServiceResult<T> Ok(string? message, int status, T? data) => new() { Success = true, Status = status, Message = message, Datas = data };

        public static ServiceResult<T> Fail(string? message, int status, T? errors) => new() { Success = false, Status = status, Message = message, Errors = errors };

        //ví dụ cách dùng :  return ServiceResult<List<conversation>>.Ok(null, StatusCodes.Status200OK, conversationsOfUser ?? new List<conversation>());
    }

    public static class ServiceResultExtension
    {
        public static IActionResult ToActionResult(this IServiceResult serviceResult)
        {
            if (serviceResult == null) return new StatusCodeResult(StatusCodes.Status500InternalServerError);
            if (serviceResult is ServiceResult<object> serviceResultWithData)
            {
                //ObjectResult là 1 class implement IActionResult
                //nó serilizable 1 object nhận vào thành json rồi trả về 
                return new ObjectResult(new
                {
                    serviceResultWithData.Success,
                    serviceResultWithData.Message,
                    serviceResultWithData.Status,
                    serviceResultWithData.Errors,
                    serviceResultWithData.Datas
                }
                )
                {
                    StatusCode = serviceResult.Status
                };
            }
            return new ObjectResult(new
            {
                serviceResult.Success,
                serviceResult.Message,
                serviceResult.Status
            })
            {
                StatusCode = serviceResult.Status
            };
        }
    }


}
