# 部署指南

## 环境要求

### 最低配置

- **CPU**: 1核
- **内存**: 512MB
- **磁盘**: 100MB
- **JDK**: 17+

### 推荐配置

- **CPU**: 2核+
- **内存**: 1GB+
- **磁盘**: 500MB+
- **JDK**: 17 LTS

---

## 本地部署

### 1. 准备环境

```bash
# 检查Java版本
java -version

# 输出示例: java version "17.0.1"
```

### 2. 编译项目

```bash
# 克隆项目
git clone https://github.com/yourusername/sqlgenerate.git
cd sqlgenerate

# 编译
mvn clean package

# 跳过测试编译
mvn clean package -DskipTests
```

### 3. 运行应用

```bash
# 方式1: 使用java命令
java -jar target/sqlgenerate-1.0.0.jar

# 方式2: 指定配置文件
java -jar target/sqlgenerate-1.0.0.jar --spring.profiles.active=prod

# 方式3: 指定端口
java -jar target/sqlgenerate-1.0.0.jar --server.port=9090

# 方式4: 后台运行
nohup java -jar target/sqlgenerate-1.0.0.jar > app.log 2>&1 &
```

### 4. 验证部署

```bash
# 检查健康状态
curl http://localhost:8080/actuator/health

# 访问前端界面
# 浏览器打开: http://localhost:8080/sql-generator.html
```

---

## Docker部署

### 1. 构建镜像

```bash
# 使用项目内置Dockerfile
docker build -t sql-generator:latest .

# 或指定Dockerfile
docker build -f Dockerfile -t sql-generator:latest .
```

### 2. 运行容器

```bash
# 基础运行
docker run -d -p 8080:8080 --name sql-generator sql-generator:latest

# 指定环境变量
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SERVER_PORT=8080 \
  --name sql-generator \
  sql-generator:latest

# 挂载日志目录
docker run -d \
  -p 8080:8080 \
  -v /opt/logs:/app/logs \
  --name sql-generator \
  sql-generator:latest

# 自动重启
docker run -d \
  -p 8080:8080 \
  --restart unless-stopped \
  --name sql-generator \
  sql-generator:latest
```

### 3. Docker Compose部署

创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  sql-generator:
    image: sql-generator:latest
    container_name: sql-generator
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SERVER_PORT=8080
      - LOGGING_LEVEL_ROOT=INFO
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

启动服务:

```bash
# 启动
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止
docker-compose down

# 重启
docker-compose restart
```

---

## 传统服务器部署

### 1. 上传文件

```bash
# 创建目录
ssh user@server "mkdir -p /opt/sqlgenerate"

# 上传jar包
scp target/sqlgenerate-1.0.0.jar user@server:/opt/sqlgenerate/

# 上传配置文件（可选）
scp src/main/resources/application-prod.yml user@server:/opt/sqlgenerate/
```

### 2. 创建启动脚本

创建 `/opt/sqlgenerate/start.sh`:

```bash
#!/bin/bash

APP_NAME="sql-generator"
JAR_NAME="sqlgenerate-1.0.0.jar"
LOG_DIR="/opt/logs"
PID_FILE="/opt/sqlgenerate/app.pid"

# 创建日志目录
mkdir -p $LOG_DIR

# 检查是否已运行
if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
    if ps -p $PID > /dev/null; then
        echo "$APP_NAME is already running (PID: $PID)"
        exit 1
    fi
fi

# 启动应用
nohup java -jar \
    -Xms512m \
    -Xmx1024m \
    -Dspring.profiles.active=prod \
    -Dserver.port=8080 \
    $JAR_NAME \
    > $LOG_DIR/app.log 2>&1 &

# 保存PID
echo $! > $PID_FILE

echo "$APP_NAME started successfully"
```

创建 `/opt/sqlgenerate/stop.sh`:

```bash
#!/bin/bash

PID_FILE="/opt/sqlgenerate/app.pid"

if [ ! -f $PID_FILE ]; then
    echo "PID file not found"
    exit 1
fi

PID=$(cat $PID_FILE)

if ps -p $PID > /dev/null; then
    kill $PID
    sleep 3

    # 强制杀死
    if ps -p $PID > /dev/null; then
        kill -9 $PID
    fi

    echo "SQL Generator stopped (PID: $PID)"
else
    echo "Process not found (PID: $PID)"
fi

rm -f $PID_FILE
```

### 3. 配置系统服务

创建 `/etc/systemd/system/sql-generator.service`:

```ini
[Unit]
Description=SQL Generator Application
After=network.target

[Service]
Type=simple
User=appuser
Group=appuser
WorkingDirectory=/opt/sqlgenerate
ExecStart=/usr/bin/java -jar sqlgenerate-1.0.0.jar
ExecStop=/bin/kill -15 $MAINPID
Restart=always
RestartSec=10

# 环境变量
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="SERVER_PORT=8080"

# 日志
StandardOutput=journal
StandardError=journal
SyslogIdentifier=sql-generator

[Install]
WantedBy=multi-user.target
```

启动服务:

```bash
# 重载systemd配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start sql-generator

# 设置开机自启
sudo systemctl enable sql-generator

# 查看状态
sudo systemctl status sql-generator

# 查看日志
sudo journalctl -u sql-generator -f
```

---

## Nginx反向代理

### 配置示例

```nginx
# /etc/nginx/conf.d/sql-generator.conf
server {
    listen 80;
    server_name sql.example.com;

    # 日志
    access_log /var/log/nginx/sql-generator-access.log;
    error_log /var/log/nginx/sql-generator-error.log;

    # 请求大小限制（文件上传）
    client_max_body_size 50M;

    # 超时设置
    proxy_connect_timeout 300s;
    proxy_send_timeout 300s;
    proxy_read_timeout 300s;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态资源缓存
    location ~* \.(html|css|js|png|jpg|jpeg|gif|ico)$ {
        proxy_pass http://localhost:8080;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

### HTTPS配置（Let's Encrypt）

```bash
# 安装certbot
sudo apt-get install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d sql.example.com

# 自动续期
sudo certbot renew --dry-run
```

Nginx配置更新:

```nginx
server {
    listen 80;
    server_name sql.example.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name sql.example.com;

    ssl_certificate /etc/letsencrypt/live/sql.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/sql.example.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # ... 其他配置同上
}
```

---

## Kubernetes部署

### Deployment配置

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sql-generator
  labels:
    app: sql-generator
spec:
  replicas: 2
  selector:
    matchLabels:
      app: sql-generator
  template:
    metadata:
      labels:
        app: sql-generator
    spec:
      containers:
      - name: sql-generator
        image: sql-generator:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SERVER_PORT
          value: "8080"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
```

### Service配置

```yaml
apiVersion: v1
kind: Service
metadata:
  name: sql-generator-service
spec:
  selector:
    app: sql-generator
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

### Ingress配置

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: sql-generator-ingress
  annotations:
    kubernetes.io/ingress.class: "nginx"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
  - hosts:
    - sql.example.com
    secretName: sql-generator-tls
  rules:
  - host: sql.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: sql-generator-service
            port:
              number: 80
```

部署命令:

```bash
# 应用配置
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml

# 查看状态
kubectl get pods
kubectl get svc
kubectl get ingress

# 查看日志
kubectl logs -f deployment/sql-generator
```

---

## 监控和日志

### 应用日志配置

`application-prod.yml`:

```yaml
logging:
  level:
    root: INFO
    org.example.sqlgenerate: INFO
  file:
    name: /opt/logs/sql-generator/app.log
    max-size: 100MB
    max-history: 30
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### Logback配置

`logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/opt/logs/sql-generator/app.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/opt/logs/sql-generator/app.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <root level="INFO">
            <appender-ref ref="FILE" />
        </root>
    </springProfile>
</configuration>
```

### 健康检查

```bash
# Spring Boot Actuator端点
curl http://localhost:8080/actuator/health

# 输出示例
{
  "status": "UP"
}
```

---

## 性能优化

### JVM参数调优

```bash
java -jar \
    -Xms512m \
    -Xmx1024m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/opt/logs/heapdump.hprof \
    sqlgenerate-1.0.0.jar
```

### Spring Boot优化

`application.yml`:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
  mvc:
    async:
      request-timeout: 30000

server:
  tomcat:
    threads:
      max: 200
      min-spare: 10
    max-connections: 8192
    accept-count: 100
```

---

## 故障排查

### 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 端口被占用 | 8080端口已使用 | 修改端口或关闭占用进程 |
| 内存溢出 | 堆内存不足 | 增加Xmx参数 |
| 文件上传失败 | 文件过大 | 增加max-file-size配置 |
| SQL生成慢 | Excel文件过大 | 优化Excel结构或增加资源 |

### 日志查看

```bash
# 查看应用日志
tail -f /opt/logs/sql-generator/app.log

# 查看系统服务日志
journalctl -u sql-generator -f

# 查看Docker日志
docker logs -f sql-generator

# 查看Kubernetes日志
kubectl logs -f deployment/sql-generator
```

---

## 备份和恢复

### 配置备份

```bash
# 备份配置文件
tar -czf sql-generator-config-$(date +%Y%m%d).tar.gz \
    /opt/sqlgenerate/application-prod.yml \
    /etc/nginx/conf.d/sql-generator.conf \
    /etc/systemd/system/sql-generator.service
```

### 数据备份

该应用为无状态服务，无需数据备份。如需持久化存储：
- 上传的Excel文件可配置存储到OSS
- 生成的SQL可保存到数据库或文件系统

---

## 升级指南

### 滚动升级

```bash
# 1. 备份当前版本
cp sqlgenerate-1.0.0.jar sqlgenerate-1.0.0.jar.bak

# 2. 下载新版本
wget https://github.com/yourusername/sqlgenerate/releases/download/v2.0.0/sqlgenerate-2.0.0.jar

# 3. 停止服务
sudo systemctl stop sql-generator

# 4. 替换jar包
mv sqlgenerate-2.0.0.jar sqlgenerate.jar

# 5. 启动服务
sudo systemctl start sql-generator

# 6. 验证
curl http://localhost:8080/actuator/health
```

### Docker升级

```bash
# 1. 拉取新镜像
docker pull sql-generator:latest

# 2. 停止并删除旧容器
docker stop sql-generator
docker rm sql-generator

# 3. 运行新容器
docker run -d -p 8080:8080 --name sql-generator sql-generator:latest
```

### Kubernetes升级

```bash
# 1. 更新镜像
kubectl set image deployment/sql-generator sql-generator=sql-generator:2.0.0

# 2. 查看滚动更新状态
kubectl rollout status deployment/sql-generator

# 3. 如需回滚
kubectl rollout undo deployment/sql-generator
```
