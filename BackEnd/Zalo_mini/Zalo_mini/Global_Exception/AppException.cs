namespace Zalo_mini.Global_Exception
{
    public class AppException : System.Exception
    {
        public int StatusCode { get; set; }
        public string ErrorCode { get; set; }
        public string? Detail { get; set; }
        public AppException(string message, int statusCode, string errorCode, string? detail) : base(message)
        {
            StatusCode = statusCode;
            ErrorCode = errorCode;
            Detail = detail;
        }
    }

    public class ValidationException : AppException
    {
        public ValidationException(string message, string? detail = null) : base(message, 400, "VALIDATION_ERROR", detail)
        {
        }
    }

    public class NotFoundException : AppException
    {
        public NotFoundException(string message, string? detail = null) : base(message, 404, "NOT_FOUND", detail) { }
    }
    public class UnauthorizedException : AppException
    {
        public UnauthorizedException(string message, string? detail = null) : base(message, 401, "UNAUTHORIZED", detail) { }
    }
    public class DatabaseException : AppException
    {
        public DatabaseException(string message, string? detail = null) : base(message, 500, "DATABASE_ERROR", detail) { }
    }
    public class ConflictException : AppException
    {
        public ConflictException(string message, string? detail = null) : base(message, 409, "CONFLICT_ERROR", detail) { }
    }
    public class ForbiddenException : AppException
    {
        public ForbiddenException(string message, string? detail = null) : base(message, 403, "FORBIDDEN_ERROR", detail) { }
    }
}
