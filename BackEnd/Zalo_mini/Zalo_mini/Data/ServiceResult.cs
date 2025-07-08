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
            if (serviceResult == null)
                return new StatusCodeResult(StatusCodes.Status500InternalServerError);

            var type = serviceResult.GetType();

            if (type.IsGenericType && type.GetGenericTypeDefinition() == typeof(ServiceResult<>))
            {
                var success = type.GetProperty("Success")?.GetValue(serviceResult);
                var message = type.GetProperty("Message")?.GetValue(serviceResult);
                var status = type.GetProperty("Status")?.GetValue(serviceResult);
                var errors = type.GetProperty("Errors")?.GetValue(serviceResult);
                var datas = type.GetProperty("Datas")?.GetValue(serviceResult);

                var responseBody = new
                {
                    Success = success,
                    Message = message,
                    Status = status,
                    Errors = errors,
                    Datas = datas
                };

                return new ObjectResult(responseBody)
                {
                    StatusCode = (int?)(status ?? StatusCodes.Status200OK)
                };
            }

            // Trường hợp không có generic data
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
