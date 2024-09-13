# Sử dụng Maven image để build ứng dụng và tải các phụ thuộc
FROM maven:3.8.8-eclipse-temurin-21 AS build

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app

# Sao chép file cấu hình Maven (pom.xml) vào container trước
COPY pom.xml .

# Tải các phụ thuộc Maven trước để cache
RUN mvn dependency:go-offline

# Sao chép toàn bộ mã nguồn dự án vào container
COPY . .

# Chạy lệnh Maven để build ứng dụng và tạo file JAR
RUN mvn clean package

# Sử dụng OpenJDK image để chạy ứng dụng
FROM eclipse-temurin:21-jdk

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR đã build từ bước trước
COPY --from=build /app/target/server-jar.jar /app/SpotMaster.jar

# Sao chép file .env từ thư mục gốc vào thư mục /app trong container
COPY src/main/resources/.env /app/.env

# Expose port 2000 (cho server)
EXPOSE 2000

# Chạy ứng dụng với file .env
CMD ["java", "-jar", "/app/SpotMaster.jar"]
