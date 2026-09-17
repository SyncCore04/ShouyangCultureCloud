# 寿阳文旅云 - Docker 部署到 Linux 云服务器指南

> 生成时间：2026-09-17
> 适用环境：CentOS 7+ / Ubuntu 20.04+，Docker + Docker Compose

---

## 目录

- [一、整体架构](#一整体架构)
- [二、前置准备](#二前置准备)
- [三、项目打包](#三项目打包)
- [四、编写 Docker 配置文件](#四编写-docker-配置文件)
- [五、部署到云服务器](#五部署到云服务器)
- [六、常用运维命令](#六常用运维命令)
- [七、代码更新后重新部署](#七代码更新后重新部署)
- [八、注意事项](#八注意事项)
- [九、最简 Docker 部署（单容器，适合毕设）](#九最简-docker-部署单容器适合毕设)

---

## 一、整体架构

```
用户浏览器
    │
    ▼
云服务器:80/443
    │
    ├── Nginx 容器（80端口）
    │   ├── /          → 前台静态资源
    │   ├── /admin     → 后台静态资源
    │   ├── /api/      → 反向代理到后端容器:8080
    │   └── /uploads/  → 反向代理到后端容器:8080
    │
    ├── Spring Boot 容器（8080端口，内部）
    │   └── 连接 MySQL 容器:3306
    │
    └── MySQL 容器（3306端口，内部，不对外暴露）
        └── 数据卷持久化
```

三个容器通过 Docker 网络互联，只有 Nginx 的 80 端口对外暴露。

---

## 二、前置准备

### 1. 云服务器要求

| 配置 | 最低要求 | 推荐配置 |
|------|---------|---------|
| CPU | 1核 | 2核 |
| 内存 | 2GB | 4GB |
| 硬盘 | 20GB | 40GB |
| 系统 | CentOS 7+ / Ubuntu 20.04+ | Ubuntu 22.04 |
| 带宽 | 1Mbps | 3Mbps+ |

### 2. 安装 Docker 和 Docker Compose

**Ubuntu/Debian：**

```bash
# 安装 Docker
curl -fsSL https://get.docker.com | bash
systemctl start docker
systemctl enable docker

# 安装 Docker Compose（v2 插件方式）
apt-get install -y docker-compose-plugin

# 验证
docker --version
docker compose version
```

**CentOS：**

```bash
# 安装 Docker
yum install -y yum-utils
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum install -y docker-ce docker-ce-cli containerd.io
systemctl start docker
systemctl enable docker

# 安装 Docker Compose
yum install -y docker-compose-plugin
```

### 3. 云服务器安全组放行端口

在云服务商控制台（阿里云/腾讯云/华为云）的安全组中放行：

- **80 端口**（HTTP 访问）
- **22 端口**（SSH 远程连接，已放行）
- **443 端口**（HTTPS，可选）

> 3306 和 8080 端口不需要对外暴露，只在 Docker 内部网络通信。

---

## 三、项目打包（在本地电脑操作）

### 1. 后端打包

```bash
cd D:\Code\Shouyang\shouyang-server
mvn clean package -DskipTests
# 生成 target/shouyang-server-1.0.0.jar
```

### 2. 前台打包

```bash
cd D:\Code\Shouyang\shouyang-web
npm run build
# 生成 dist/ 目录
```

### 3. 后台打包

```bash
cd D:\Code\Shouyang\shouyang-admin
npm run build
# 生成 dist/ 目录
```

### 4. 导出数据库

```bash
mysqldump -u root -p shouyang_culture > shouyang_culture_init.sql
```

---

## 四、编写 Docker 配置文件

在项目根目录创建 `docker/` 目录，结构如下：

```
Shouyang/
├── docker/
│   ├── backend/
│   │   ├── Dockerfile
│   │   └── application-prod.yml
│   ├── frontend/
│   │   ├── Dockerfile
│   │   └── nginx.conf
│   └── mysql/
│       └── init/
│           └── shouyang_culture_init.sql   # 数据库初始化脚本
├── docker-compose.yml
└── .dockerignore
```

### 1. 后端 Dockerfile

文件路径：`docker/backend/Dockerfile`

```dockerfile
# 基础镜像：JDK 17 精简版
FROM openjdk:17-jdk-slim

# 设置时区为东八区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用目录
WORKDIR /app

# 复制 jar 包（从本地 target 目录复制）
COPY shouyang-server/target/shouyang-server-1.0.0.jar app.jar

# 复制生产环境配置
COPY docker/backend/application-prod.yml application-prod.yml

# 创建上传文件目录
RUN mkdir -p /app/uploads

# 暴露端口（容器内部，不对外映射）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

### 2. 后端生产环境配置

文件路径：`docker/backend/application-prod.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    # 注意：host 写 mysql（docker-compose 中的服务名），不是 localhost
    url: jdbc:mysql://mysql:3306/shouyang_culture?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: Shouyang@2024   # 改成你自己的密码，和 docker-compose 中一致
    driver-class-name: com.mysql.cj.jdbc.Driver
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

# 文件上传配置
file:
  upload-path: /app/uploads/   # 容器内路径，通过卷挂载到宿主机

# MyBatis-Plus
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

# 日志
logging:
  level:
    com.shouyang: info
```

### 3. 前端 Dockerfile

文件路径：`docker/frontend/Dockerfile`

```dockerfile
# 基础镜像：Nginx 稳定版
FROM nginx:stable-alpine

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 复制 Nginx 配置
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

# 复制前台静态资源到 /usr/share/nginx/html/
COPY shouyang-web/dist/ /usr/share/nginx/html/

# 复制后台静态资源到 /usr/share/nginx/html/admin/
COPY shouyang-admin/dist/ /usr/share/nginx/html/admin/

# 暴露 80 端口
EXPOSE 80

# 启动 Nginx（前台运行）
CMD ["nginx", "-g", "daemon off;"]
```

### 4. Nginx 配置

文件路径：`docker/frontend/nginx.conf`

```nginx
server {
    listen       80;
    server_name  localhost;
    client_max_body_size 50M;

    # 前台用户端
    location / {
        root   /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
        index  index.html;
    }

    # 后台管理端
    location /admin/ {
        alias   /usr/share/nginx/html/admin/;
        try_files $uri $uri/ /admin/index.html;
        index  index.html;
    }

    # 后端 API 反向代理
    # 注意：proxy_pass 写 backend（docker-compose 中的服务名）
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 60s;
    }

    # 上传文件
    location /uploads/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
    }

    # 接口文档（可选）
    location /doc.html {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
}
```

### 5. docker-compose.yml

文件路径：项目根目录 `docker-compose.yml`

```yaml
version: '3.8'

services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    container_name: shouyang-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: Shouyang@2024       # root 密码，和 application-prod.yml 一致
      MYSQL_DATABASE: shouyang_culture          # 自动创建数据库
      TZ: Asia/Shanghai
    command:
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_general_ci
      --default-authentication-plugin=mysql_native_password
    volumes:
      - mysql-data:/var/lib/mysql               # 数据持久化
      - ./docker/mysql/init:/docker-entrypoint-initdb.d  # 初始化脚本（首次启动自动执行）
    # 不对外暴露 3306 端口，只在内部网络通信（更安全）
    # 如果需要外部连接数据库，取消下面注释
    # ports:
    #   - "3306:3306"
    networks:
      - shouyang-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-pShouyang@2024"]
      interval: 10s
      timeout: 5s
      retries: 5

  # 后端服务
  backend:
    build:
      context: .
      dockerfile: docker/backend/Dockerfile
    container_name: shouyang-backend
    restart: always
    depends_on:
      mysql:
        condition: service_healthy    # 等 MySQL 健康检查通过后再启动
    volumes:
      - ./uploads:/app/uploads        # 上传文件挂载到宿主机
    networks:
      - shouyang-network
    # 不对外暴露 8080，通过 Nginx 反向代理访问
    # ports:
    #   - "8080:8080"

  # 前端 Nginx
  frontend:
    build:
      context: .
      dockerfile: docker/frontend/Dockerfile
    container_name: shouyang-frontend
    restart: always
    depends_on:
      - backend
    ports:
      - "80:80"                        # 对外暴露 80 端口
    volumes:
      - ./uploads:/usr/share/nginx/html/uploads  # 可选：让 Nginx 直接服务上传文件
    networks:
      - shouyang-network

# 数据卷
volumes:
  mysql-data:
    driver: local

# 自定义网络
networks:
  shouyang-network:
    driver: bridge
```

### 6. .dockerignore

文件路径：项目根目录 `.dockerignore`

```
node_modules
target
*.log
.git
.gitignore
.idea
.vscode
*.iml
.DS_Store
```

---

## 五、部署到云服务器

### 1. 上传项目文件到服务器

**方式一：使用 scp 命令（本地电脑执行）**

```bash
# 将整个项目目录上传到服务器
scp -r D:\Code\Shouyang root@你的服务器IP:/opt/shouyang/
```

**方式二：使用 FileZilla / WinSCP 等图形化工具上传**

**方式三：使用 Git（服务器上 git clone）**

> 上传前确保已经执行了 `mvn package` 和 `npm run build`，target 和 dist 目录都有内容。

### 2. 登录服务器并进入项目目录

```bash
ssh root@你的服务器IP
cd /opt/shouyang
```

### 3. 构建并启动容器

```bash
# 构建镜像并启动（首次会比较慢，需要下载基础镜像）
docker compose up -d --build

# 查看容器运行状态
docker compose ps

# 查看日志（排查问题）
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql
```

### 4. 验证数据库初始化

```bash
# 进入 MySQL 容器
docker exec -it shouyang-mysql mysql -u root -pShouyang@2024

# 查看数据库和表
SHOW DATABASES;
USE shouyang_culture;
SHOW TABLES;
SELECT username, nickname FROM sys_admin;
EXIT;
```

> 如果初始化脚本没有自动执行，手动导入：
> ```bash
> docker exec -i shouyang-mysql mysql -u root -pShouyang@2024 shouyang_culture < docker/mysql/init/shouyang_culture_init.sql
> ```

### 5. 访问验证

在浏览器中访问：

| 页面 | 地址 |
|------|------|
| 前台用户端 | `http://你的服务器IP` |
| 后台管理端 | `http://你的服务器IP/admin` |
| 接口文档 | `http://你的服务器IP/doc.html` |

**测试账号：**

- 管理员：admin / 123456
- 前台用户：test / 123456

---

## 六、常用运维命令

```bash
# 启动所有容器
docker compose up -d

# 停止所有容器
docker compose down

# 重启某个服务
docker compose restart backend
docker compose restart frontend

# 查看日志
docker compose logs -f           # 所有服务
docker compose logs -f backend   # 后端
docker compose logs -f mysql     # 数据库

# 重新构建并启动（代码更新后）
docker compose up -d --build backend

# 进入容器
docker exec -it shouyang-backend bash
docker exec -it shouyang-mysql mysql -u root -p

# 查看容器资源占用
docker stats

# 清理无用镜像（释放空间）
docker image prune -f
```

---

## 七、代码更新后重新部署

```bash
# 1. 本地重新打包
cd shouyang-server && mvn clean package -DskipTests
cd ../shouyang-web && npm run build
cd ../shouyang-admin && npm run build

# 2. 上传更新的文件到服务器（只上传 target 和 dist 即可）
scp -r shouyang-server/target root@IP:/opt/shouyang/shouyang-server/
scp -r shouyang-web/dist root@IP:/opt/shouyang/shouyang-web/
scp -r shouyang-admin/dist root@IP:/opt/shouyang/shouyang-admin/

# 3. 服务器上重新构建并启动
cd /opt/shouyang
docker compose up -d --build
```

---

## 八、注意事项

### 1. 数据库密码

三个地方的密码必须一致：

- `docker-compose.yml` 中的 `MYSQL_ROOT_PASSWORD`
- `application-prod.yml` 中的 `spring.datasource.password`
- 手动连接数据库时使用的密码

### 2. 容器间通信

Docker Compose 中服务名就是主机名：

- 后端连接数据库：`jdbc:mysql://mysql:3306/...`（不是 localhost）
- Nginx 反向代理后端：`proxy_pass http://backend:8080;`（不是 localhost）

### 3. 上传文件持久化

`docker-compose.yml` 中挂载了 `./uploads:/app/uploads`，上传的图片会保存在宿主机的 `/opt/shouyang/uploads/` 目录，容器删除后文件不会丢失。

### 4. 数据库数据持久化

使用了命名卷 `mysql-data`，数据库数据不会因容器删除而丢失。如需完全重置：

```bash
docker compose down -v    # -v 会删除数据卷，谨慎使用
```

### 5. 内存不足问题

如果云服务器内存只有 2GB，可能出现 MySQL 或 Java 进程被 OOM kill 的情况：

- 限制 JVM 内存：在后端 Dockerfile 的 ENTRYPOINT 中添加 `-Xms256m -Xmx512m`
- 限制 MySQL 内存：在 command 中添加 `--innodb-buffer-pool-size=128M`

### 6. 防火墙

云服务器内部防火墙（如 firewalld/ufw）需要放行 80 端口：

```bash
# CentOS
firewall-cmd --add-port=80/tcp --permanent
firewall-cmd --reload

# Ubuntu
ufw allow 80/tcp
```

### 7. 域名和 HTTPS（可选）

如果有域名，在 Nginx 配置中修改 `server_name 你的域名;`，然后用 Certbot 配置 HTTPS：

```bash
# 进入 Nginx 容器安装 certbot（或在宿主机安装后挂载证书）
# 推荐使用宿主机 Nginx 做 SSL 终止，再反向代理到容器
```

---

## 九、最简 Docker 部署（单容器，适合毕设）

如果觉得三容器太复杂，可以用**单容器方案**：把前端静态文件打进后端 jar 包，只需要一个 Spring Boot 容器 + 一个 MySQL 容器。

### 1. 打包前端到后端

```bash
# 将前台 dist 复制到后端 static 目录
cp -r shouyang-web/dist/* shouyang-server/src/main/resources/static/

# 将后台 dist 复制到后端 static/admin 目录
mkdir -p shouyang-server/src/main/resources/static/admin
cp -r shouyang-admin/dist/* shouyang-server/src/main/resources/static/admin/

# 重新打包后端
cd shouyang-server
mvn clean package -DskipTests
```

### 2. docker-compose.yml（简化版）

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: Shouyang@2024
      MYSQL_DATABASE: shouyang_culture
    volumes:
      - mysql-data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - shouyang-network

  app:
    image: openjdk:17-jdk-slim
    restart: always
    depends_on:
      - mysql
    ports:
      - "80:8080"          # 直接对外暴露 8080，映射到宿主机 80
    volumes:
      - ./shouyang-server/target/shouyang-server-1.0.0.jar:/app/app.jar
      - ./uploads:/app/uploads
    working_dir: /app
    command: java -jar app.jar --spring.datasource.url=jdbc:mysql://mysql:3306/shouyang_culture?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai --spring.datasource.password=Shouyang@2024 --file.upload-path=/app/uploads/
    networks:
      - shouyang-network

volumes:
  mysql-data:
networks:
  shouyang-network:
```

### 3. 访问地址

- 前台：`http://服务器IP`
- 后台：`http://服务器IP/admin`
- 接口文档：`http://服务器IP/doc.html`

---

## 总结

- **标准方案**：三容器（Nginx + Spring Boot + MySQL），前后端分离、职责清晰
- **最简方案**：两容器（Spring Boot 含前端 + MySQL），配置简单、适合毕设演示
- **部署核心**：容器间用服务名通信、数据卷持久化、密码三处一致、安全组放行 80 端口
