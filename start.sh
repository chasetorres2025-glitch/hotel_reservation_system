#!/bin/bash

echo "==================================="
echo "酒店管理系统 - 快速启动脚本"
echo "==================================="
echo ""

# 检查MySQL服务
echo "1. 检查MySQL服务..."
if mysqladmin ping -h localhost --silent 2>/dev/null; then
    echo "   ✓ MySQL服务运行正常"
else
    echo "   ✗ MySQL服务未启动，请先启动MySQL服务"
    exit 1
fi

# 检查数据库是否存在
echo ""
echo "2. 检查数据库..."
DB_EXISTS=$(mysql -u root -e "SHOW DATABASES LIKE 'hotel_system'" | grep hotel_system)
if [ -z "$DB_EXISTS" ]; then
    echo "   数据库不存在，正在初始化..."
    mysql -u root < "$(dirname "$0")/database/init.sql"
    echo "   ✓ 数据库初始化完成"
else
    echo "   ✓ 数据库已存在"
fi

# 启动后端
echo ""
echo "3. 启动后端服务..."
cd "$(dirname "$0")/hotel-backend"
osascript -e 'tell application "Terminal" to do script "cd \"'$(pwd)'\" && mvn spring-boot:run"'
echo "   ✓ 后端服务启动中... (http://localhost:8080)"

# 等待后端启动
echo ""
echo "   等待后端服务启动..."
sleep 10

# 启动前端
echo ""
echo "4. 启动前端服务..."
cd "$(dirname "$0")/hotel-frontend"
osascript -e 'tell application "Terminal" to do script "cd \"'$(pwd)'\" && npm run dev"'
echo "   ✓ 前端服务启动中... (http://localhost:3000)"

echo ""
echo "==================================="
echo "启动完成！"
echo "==================================="
echo ""
echo "访问地址："
echo "  用户端：http://localhost:3000"
echo "  管理端：http://localhost:3000/admin"
echo ""
echo "默认账号："
echo "  管理员 - 用户名: admin  密码: admin123"
echo "  测试用户 - 用户名: user001  密码: user123"
echo ""
