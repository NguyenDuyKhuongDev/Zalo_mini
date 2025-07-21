using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using NETCore.MailKit.Core;
using System.Text;
using Zalo_mini.Data;
using Zalo_mini.Global_Exception;
using Zalo_mini.Hubs;
using Zalo_mini.Repositories;
using Zalo_mini.Services;

var builder = WebApplication.CreateBuilder(args);
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
builder.Services.AddDbContext<AppDbContext>(options =>
{
    options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString));
});

// Add services to the container.
builder.Services.AddScoped<IEmailServices, EmailServices>();
builder.Services.AddScoped<ITokenServices, TokenServices>();
builder.Services.AddScoped<IAuthServices, AuthServices>();
builder.Services.AddScoped<IConversationRepositories, ConversationRepository>();
builder.Services.AddScoped<IConversationParticipantRepositories, ConversationParticipantRepository>();
builder.Services.AddScoped<IMessageRepositories, MessageRepositories>();
builder.Services.AddScoped<IUserRepository, UserRepository>();
builder.Services.AddScoped<IUserSessionRepository, UserSessionRepository>();
builder.Services.AddSingleton<IUserIdProvider, CustomUserIdProvider>();
builder.Services.AddSignalR();
builder.Services.AddSingleton<ConnectionMapping>();


builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        In = ParameterLocation.Header,
        Description = "Enter 'Bearer' [space] and then your token",
        Name = "Authorization",
        Type = SecuritySchemeType.ApiKey
    });

    options.AddSecurityRequirement(new OpenApiSecurityRequirement {
        {
        new OpenApiSecurityScheme{ Reference = new OpenApiReference{Type=ReferenceType.SecurityScheme,Id="Bearer"}},
        new string[]{}
        }
    });
    options.EnableAnnotations();
});
//cau hinh tu dong check model state
builder.Services.Configure<ApiBehaviorOptions>(options => { options.SuppressModelStateInvalidFilter = false; }
);
//cau hinh jwt
var key = Encoding.ASCII.GetBytes(builder.Configuration["Jwt:SecretKey"]);
builder.Services.AddAuthentication(options =>
{
    options.DefaultScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
}).AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters()
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,

        ValidIssuer = builder.Configuration["Jwt:Issuer"],
        ValidAudiences = builder.Configuration["Jwt:Audience"].Split(","),
        IssuerSigningKey = new SymmetricSecurityKey(key),

        ClockSkew = TimeSpan.FromMinutes(2),
        RequireExpirationTime = true,
        RequireSignedTokens = true
    };
});

//trong LaunchSettings.json tôi cần cấu hình để cho port là http, nếu
//để https thì nó tự nhảy sang gây lỗi 304, nhưng nếu để là 0.0.0.0 thì báo lỗi 
// nếu để là localhost thì nó lại không chạy được máy ảo 0.0.2.2 của mobile
//nên cứ để local host bên trong lanchSetting 
//xong cấu hình kestrel tiếp nhận port 5285 - là port http hiện tại
//https://localhost:7127; - đonạ port ở zalomini https, bỏ ra để nó đỡ tự nhảy vào https
builder.WebHost.ConfigureKestrel(serverOptions =>
{
    serverOptions.ListenAnyIP(5285); // mở port cho tất cả IP
});

builder.Services.AddAuthorization();

builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    policy.AllowAnyOrigin()
    .AllowAnyHeader()
    .AllowAnyMethod()

    );
});
var app = builder.Build();
app.UseStaticFiles();
//dang ki middle ware de xu ly exception 
app.UseMiddleware<GlobalExceptionMiddleware>();
// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}
app.UseCors("AllowAll");
app.UseRouting();
app.UseAuthentication();
app.UseAuthorization();
app.UseEndpoints(endpoints =>
{
    endpoints.MapHub<ChatHub>("/chatHub");
    endpoints.MapControllers();
});
//tắt tạm thời để nó không  tự nhảy sang https làm lỗi bên mobile
/*
app.UseHttpsRedirection();*/

app.Run();
