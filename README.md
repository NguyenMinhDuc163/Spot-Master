# Spot-Master

# Docker deployment

## Bước 1: Thêm cấu hình
Thêm file `.env` vào thư mục `src/main/resources/.env`. Đảm bảo rằng file `.env` đã được cấu hình đúng các biến môi trường cần thiết.

## Bước 2: Sử dụng Docker

Chạy các lệnh sau để triển khai:

```bash
docker pull nguyenduc1603/spotmaster:v1.0.0
docker run -p 2000:2000 nguyenduc1603/spotmaster:v1.0.0

# Sử dụng docker compose
```bash
docker-compose down
docker-compose up --build
