
namespace Zalo_mini.Global_Exception
{
    public class GlobalExceptionMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<GlobalExceptionMiddleware> _logger;

        public GlobalExceptionMiddleware(RequestDelegate next, ILogger<GlobalExceptionMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }
        public async Task Invoke(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (AppException ex)
            {
                _logger.LogWarning(ex, "App Erorr:{Message}", ex.Message);
                context.Response.StatusCode = ex.StatusCode;
                context.Response.ContentType = "application/json";
                await context.Response.WriteAsJsonAsync(new
                {
                    error = ex.ErrorCode,
                    message = ex.Message,
                    detail = ex.Detail
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandle Error");
                context.Response.StatusCode = 500;
                context.Response.ContentType = "application/json";
                await context.Response.WriteAsJsonAsync(new
                {
                    error = "Unhandle Error",
                    message = $"Unhandle Error Occured :{ex.Message},Inner Exception:{ex.InnerException?.Message}",
                });
            }
        }
    }
}
