# **Spot-Master**

## Giới thiệu

Spot-Master là ứng dụng sử dụng JDK 21, Maven, và MySQL 8.0. Dự án có thể được triển khai bằng Docker hoặc Docker Compose.

---

## **Chạy Dự Án**

1. **Cấu hình biến môi trường**:  
   Sao chép file `env.example` thành `.env` và cấu hình các thông số cần thiết của MySQL.

---

## **Cấu Hình**

- **JDK**: 21
- **Maven**: 3.8.8
- **MySQL**: 8.0

---

## **Docker Deployment**

### Bước 1: Thêm cấu hình

Thêm file `.env` vào thư mục `src/main/resources/.env`. Đảm bảo rằng file `.env` được cấu hình chính xác với các biến môi trường cần thiết như thông tin MySQL.

### 1. Sử dụng Docker

Chạy các lệnh sau để tải và chạy ứng dụng:

```bash
docker pull nguyenduc1603/spotmaster:v1.0.0
docker run -p 2000:2000 nguyenduc1603/spotmaster:v1.0.0
```

# Sử dụng Docker Compose
```bash
docker-compose down        # Tắt các container đang chạy
docker-compose up --build  # Xây dựng lại image và khởi chạy các container
```

...
