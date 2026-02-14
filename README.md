# 酒店管理系统 - 项目部署文档

## 项目概述

本项目是一个基于 Spring Boot + Vue3 的酒店管理系统，包含管理端和用户端两个子系统。

### 技术栈

**后端**：
- Spring Boot 2.7.18
- MyBatis 2.3.1
- MySQL 8.0.x
- Druid 1.2.20

**前端**：
- Vue 3.3.x
- Element Plus 2.4.x
- Pinia 2.1.x
- ECharts 5.x

---

## 环境要求

### 必需环境

| 软件 | 版本要求 | 说明 |
|-----|---------|------|
| JDK | 1.8+ | Java运行环境 |
| Maven | 3.6+ | 项目构建工具 |
| Node.js | 16+ | 前端运行环境 |
| npm | 8+ | 包管理器 |
| MySQL | 8.0+ | 数据库 |

### 开发工具（可选）

- IntelliJ IDEA（后端开发）
- VS Code（前端开发）
- Navicat / DBeaver（数据库管理）
- Postman（API测试）

---

## 快速开始

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source /Users/torres/Documents/trae_projects/hotel_reservation_system/database/init.sql
```

或者使用数据库管理工具导入 `database/init.sql` 文件。

### 2. 启动后端服务

```bash
# 进入后端项目目录
cd /Users/torres/Documents/trae_projects/hotel_reservation_system/hotel-backend

# 编译项目
mvn clean install

# 启动服务
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 3. 启动前端服务

```bash
# 进入前端项目目录
cd /Users/torres/Documents/trae_projects/hotel_reservation_system/hotel-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:3000` 启动。

### 4. 访问系统

打开浏览器访问：
- **用户端**：http://localhost:3000
- **管理端**：http://localhost:3000/admin

---

## 默认账号

### 管理员账号
- 用户名：`admin`
- 密码：`admin123`

### 测试用户账号
- 用户名：`user001`
- 密码：`user123`

---

## 项目结构

```
hotel_reservation_system/
├── 参考论文.docx              # 参考论文资料
├── database/
│   └── init.sql              # 数据库初始化脚本
├── hotel-backend/            # 后端项目
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/hotel/
│   │   │   │   ├── HotelApplication.java
│   │   │   │   ├── config/        # 配置类
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── service/       # 业务层
│   │   │   │   ├── mapper/        # 数据访问层
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   ├── vo/            # 视图对象
│   │   │   │   ├── common/        # 公共类
│   │   │   │   └── exception/     # 异常处理
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── mapper/        # MyBatis XML
│   │   └── test/
│   └── target/
└── hotel-frontend/           # 前端项目
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.js
        ├── App.vue
        ├── api/              # API接口
        ├── assets/           # 静态资源
        ├── components/       # 公共组件
        ├── layouts/          # 布局组件
        ├── router/           # 路由配置
        ├── stores/           # 状态管理
        ├── utils/            # 工具函数
        └── views/            # 页面组件
            ├── admin/        # 管理端页面
            └── user/         # 用户端页面
```

---

## 配置说明

### 后端配置

配置文件位置：`hotel-backend/src/main/resources/application.yml`

#### 数据库配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hotel_system?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的数据库密码
```

#### 服务端口配置

```yaml
server:
  port: 8080  # 后端服务端口
```

### 前端配置

配置文件位置：`hotel-frontend/vite.config.js`

#### API代理配置

```javascript
server: {
  port: 3000,  // 前端服务端口
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端API地址
      changeOrigin: true
    }
  }
}
```

---

## 功能模块

### 管理端功能

1. **用户管理**
   - 用户列表查询（分页、搜索、筛选）
   - 用户新增、编辑、删除
   - 用户状态管理（启用/禁用）
   - 密码重置

2. **客房管理**
   - 客房列表查询（分页、搜索、筛选）
   - 客房新增、编辑、删除
   - 客房状态管理（空闲/预订/入住/维护）
   - 客房类型管理

3. **订单管理**
   - 订单列表查询（分页、搜索、筛选）
   - 订单详情查看
   - 订单确认、拒绝
   - 办理入住、退房
   - 订单导出

4. **数据统计**
   - 客房预订率统计（图表展示）
   - 营收统计（图表展示）
   - 订单统计

### 用户端功能

1. **注册/登录**
   - 用户注册
   - 用户登录
   - 退出登录

2. **客房预订**
   - 客房列表浏览
   - 客房搜索筛选
   - 客房详情查看
   - 提交预订订单
   - 订单支付

3. **个人订单管理**
   - 我的订单列表
   - 订单详情查看
   - 订单取消

4. **个人中心**
   - 个人信息查看
   - 个人信息修改
   - 密码修改

---

## API接口文档

### 接口基础路径

- 管理端接口：`/api/admin`
- 用户端接口：`/api/user`
- 公共接口：`/api/common`

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1707811200000
}
```

### 主要接口列表

#### 用户端接口

| 接口 | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 用户注册 | POST | /api/user/register | 用户注册 |
| 用户登录 | POST | /api/user/login | 用户登录 |
| 退出登录 | POST | /api/user/logout | 退出登录 |
| 客房列表 | GET | /api/user/room/list | 查询客房列表 |
| 客房详情 | GET | /api/user/room/{id} | 查询客房详情 |
| 提交预订 | POST | /api/user/order | 提交预订订单 |
| 我的订单 | GET | /api/user/order/list | 查询我的订单 |

#### 管理端接口

| 接口 | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 用户列表 | GET | /api/admin/user/list | 查询用户列表 |
| 用户新增 | POST | /api/admin/user | 新增用户 |
| 客房列表 | GET | /api/admin/room/list | 查询客房列表 |
| 客房新增 | POST | /api/admin/room | 新增客房 |
| 订单列表 | GET | /api/admin/order/list | 查询订单列表 |
| 订单确认 | PUT | /api/admin/order/confirm/{id} | 确认订单 |
| 预订率统计 | GET | /api/admin/stats/booking-rate | 预订率统计 |
| 营收统计 | GET | /api/admin/stats/revenue | 营收统计 |

---

## 常见问题

### 1. 后端启动失败

**问题**：数据库连接失败

**解决方案**：
- 检查MySQL服务是否启动
- 检查数据库配置是否正确（用户名、密码、数据库名）
- 确认数据库已创建并执行了初始化脚本

### 2. 前端启动失败

**问题**：依赖安装失败

**解决方案**：
```bash
# 清除npm缓存
npm cache clean --force

# 删除node_modules目录
rm -rf node_modules

# 重新安装依赖
npm install
```

### 3. 跨域问题

**问题**：前端请求后端接口出现跨域错误

**解决方案**：
- 后端已配置CORS，允许跨域请求
- 前端已配置代理，开发环境会自动代理到后端

### 4. 登录失败

**问题**：使用默认账号无法登录

**解决方案**：
- 确认数据库已初始化
- 确认用户表中存在默认账号数据
- 检查密码是否正确（admin123 / user123）

---

## 生产环境部署

### 后端部署

1. **打包项目**
```bash
cd hotel-backend
mvn clean package -Dmaven.test.skip=true
```

2. **上传jar包**
将 `target/hotel-backend-1.0.0.jar` 上传到服务器

3. **启动服务**
```bash
nohup java -jar hotel-backend-1.0.0.jar > hotel.log 2>&1 &
```

### 前端部署

1. **构建项目**
```bash
cd hotel-frontend
npm run build
```

2. **上传dist目录**
将 `dist` 目录上传到服务器

3. **Nginx配置**
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 技术支持

如有问题，请查看：
- 产品说明书：`产品说明书.md`
- 后端代码：`hotel-backend/`
- 前端代码：`hotel-frontend/`

---

**文档版本**：V1.0  
**更新日期**：2026-02-13
