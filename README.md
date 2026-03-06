# User Management & Authentication REST API

## 📋 Giới thiệu
Dự án này là một hệ thống **RESTful API** được xây dựng trên nền tảng **Java Spring Boot**, cung cấp các chức năng quản lý người dùng (User Management) và xác thực, phân quyền bảo mật (Authentication & Authorization) sử dụng JSON Web Token (JWT).

Hệ thống được thiết kế theo kiến trúc Layered chuẩn mực của Spring (Controller - Service - Repository), đảm bảo mã nguồn dễ đọc, tái sử dụng cao và dễ bảo trì.

## 🚀 Công nghệ sử dụng
- **Ngôn ngữ:** Java 21
- **Framework:** Spring Boot 3/4
- **Bảo mật:** Spring Security (OAuth2 Resource Server), JWT (JSON Web Token)
- **Cơ sở dữ liệu:** Microsoft SQL Server
- **ORM / Tương tác DB:** Spring Data JPA, Hibernate
- **Tiện ích:** Lombok, MapStruct (Data Mapping), Jakarta Validation
- **Quản lý dependencies:** Maven

## ✨ Tính năng chính
### 1. Quản lý hệ thống người dùng (User Management)
- **Tạo người dùng mới:** REST endpoint để đăng ký tài khoản có xác thực dữ liệu đầu vào.
- **Lấy thông tin người dùng:** API hỗ trợ lấy danh sách toàn bộ người dùng hoặc chi tiết 1 user theo định danh (ID).
- **Cập nhật thông tin:** Cho phép người dùng chỉnh sửa thuộc tính tài khoản cá nhân.
- **Xóa tài khoản:** Cung cấp cơ chế loại bỏ tài khoản người dùng theo cấu trúc REST chuẩn xác.

### 2. Xác thực và Bảo mật (Authentication)
- **Đăng nhập (Authenticate):** Cơ chế xác thực an toàn thông qua username và password, hệ thống trả về token **JWT** hợp lệ chứa thông tin xác thực.
- **Kiểm tra Token (Introspect):** Endpoint dành riêng cho việc xác minh tính hợp lệ và hiệu lực thực tế của JWT.
- **Security Context:** Trích xuất quyền hạn (`Authorities`) và thông tin người dùng từ token trực tiếp trong các requests bảo mật.

## 🏛️ Cấu trúc dự án khái quát
- `controller`: Các REST controller xử lý trực tiếp HTTP Request/Response (Ví dụ: `UserController`, `AuthenticationController`).
- `service`: Chứa logic nghiệp vụ ứng dụng cốt lõi.
- `repository`: Giao tiếp với Database bằng Spring Data JPA.
- `model`: Định nghĩa các Entities tương ứng với bảng Database.
- `dto`: Các đối tượng trao đổi dữ liệu (Request/Response, Wrapper cấu trúc `ApiResponse`).
- `mapper`: Mapping giữa Entities và DTO tự động bằng MapStruct.
- `configuration` / `constants` / `utils`: Các cấu hình dự án, hằng số, và công cụ hỗ trợ.

## 🛠️ Yêu cầu môi trường
- **Java:** JDK 21+
- **Database:** Microsoft SQL Server
- **Maven:** 3.8+ (Có thể dùng Maven Wrapper đính kèm)

*Dự án cung cấp ApiResponse wrapper cho toàn bộ các endpoint, giúp frontend thiết lập cơ chế xử lý dữ liệu và cấu trúc lỗi (Error Handling/Data structure) một cách đồng nhất, chặt chẽ (format thống nhất code/message/result).*
