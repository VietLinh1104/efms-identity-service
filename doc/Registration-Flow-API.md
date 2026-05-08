# Hướng dẫn tích hợp API Đăng ký User và Công ty mới (OTP Flow)

Tài liệu này hướng dẫn Frontend tích hợp luồng đăng ký tài khoản Admin mới kèm theo xác thực mã OTP qua Email và khởi tạo thông tin Công ty.

---

## Tổng quan quy trình (Flow)

1.  **Bước 1**: Người dùng nhập Email và nhấn "Gửi mã xác thực".
2.  **Bước 2**: Hệ thống gửi mã OTP 6 số qua Email.
3.  **Bước 3**: Người dùng nhập tiếp các thông tin: Tên, Mật khẩu, Tên công ty và Mã OTP đã nhận.
4.  **Bước 4**: Nhấn "Đăng ký" để hoàn tất.

---

## 1. API Gửi mã xác thực (OTP)

Dùng để gửi mã xác thực đến Email của người dùng trước khi đăng ký.

-   **Endpoint**: `/auth/register/send-code`
-   **Method**: `POST`
-   **Auth**: Không yêu cầu (Public)

### Request Body
```json
{
  "email": "user@example.com"
}
```

### Response
-   **Success (200 OK)**:
    ```json
    {
      "message": "Verification code sent successfully!"
    }
    ```
-   **Error (400 Bad Request)**: Nếu email trống hoặc đã tồn tại trong hệ thống.
-   **Error (500 Internal Server Error)**: Lỗi gửi mail (cấu hình SMTP sai).

---

## 2. API Đăng ký tài khoản và Công ty

Dùng để xác thực OTP và chính thức tạo User (Role Admin) cùng với một Công ty mới.

-   **Endpoint**: `/auth/register`
-   **Method**: `POST`
-   **Auth**: Không yêu cầu (Public)

### Request Body (RegisterRequest)
| Trường | Kiểu dữ liệu | Mô tả | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `name` | String | Tên đầy đủ của người dùng | 3-255 ký tự |
| `email` | String | Email dùng để đăng ký | Định dạng email, max 255 |
| `password` | String | Mật khẩu tài khoản | 6-255 ký tự |
| `otp` | String | Mã xác thực 6 số nhận từ email | Đúng 6 ký tự |
| `companyName` | String | Tên công ty mới muốn tạo | Max 255 ký tự |

**Ví dụ:**
```json
{
  "name": "Nguyễn Văn A",
  "email": "user@example.com",
  "password": "strongpassword123",
  "otp": "123456",
  "companyName": "Công ty Giải pháp EFMS"
}
```

### Response
-   **Success (200 OK)**:
    ```json
    {
      "message": "User registered successfully!"
    }
    ```
-   **Error (400 Bad Request)**: 
    - Mã OTP sai hoặc hết hạn (hết hạn sau 5 phút).
    - Email đã tồn tại.
    - Thiếu thông tin bắt buộc.

---

## 3. API Đăng nhập (Tham khảo)

Sau khi đăng ký thành công, người dùng có thể dùng API này để lấy JWT Token.

-   **Endpoint**: `/auth/login`
-   **Method**: `POST`

### Request Body
```json
{
  "email": "user@example.com",
  "password": "strongpassword123"
}
```

### Response Body (JwtResponse)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": "uuid-cua-user",
  "name": "Nguyễn Văn A",
  "email": "user@example.com",
  "companyId": "uuid-cua-cong-ty-vừa-tạo",
  "roles": ["ROLE_ADMIN"]
}
```

---

## Lưu ý cho Frontend
1.  **Hết hạn OTP**: Mã OTP chỉ có hiệu lực trong **5 phút**. Nếu quá thời gian, người dùng phải nhấn gửi lại mã.
2.  **Bảo mật**: Mật khẩu nên được kiểm tra độ mạnh ở phía Frontend trước khi gửi lên Backend.
3.  **Xử lý lỗi**: Hiển thị thông báo lỗi từ trường `message` trong JSON trả về khi nhận mã 400 hoặc 500.
