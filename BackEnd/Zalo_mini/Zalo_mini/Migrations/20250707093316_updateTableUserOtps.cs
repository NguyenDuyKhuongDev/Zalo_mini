using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Zalo_mini.Migrations
{
    public partial class updateTableUserOtps : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_user_otps_users_user_id",
                table: "user_otps");

            migrationBuilder.DropIndex(
                name: "IX_user_otps_user_id",
                table: "user_otps");

            migrationBuilder.DropColumn(
                name: "user_id",
                table: "user_otps");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<long>(
                name: "user_id",
                table: "user_otps",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);

            migrationBuilder.CreateIndex(
                name: "IX_user_otps_user_id",
                table: "user_otps",
                column: "user_id");

            migrationBuilder.AddForeignKey(
                name: "FK_user_otps_users_user_id",
                table: "user_otps",
                column: "user_id",
                principalTable: "users",
                principalColumn: "id");
        }
    }
}
