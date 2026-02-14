#!/bin/bash

echo "==================================="
echo "酒店管理系统 - 登录流程测试"
echo "==================================="
echo ""

# 测试用户登录
echo "1. 测试用户登录..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user001","password":"user123"}')

echo "登录响应："
echo "$LOGIN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$LOGIN_RESPONSE"
echo ""

# 检查登录是否成功
CODE=$(echo "$LOGIN_RESPONSE" | grep -o '"code":[0-9]*' | grep -o '[0-9]*')
if [ "$CODE" = "200" ]; then
    echo "✅ 用户登录成功"
else
    echo "❌ 用户登录失败"
    exit 1
fi

# 测试获取客房列表
echo ""
echo "2. 测试获取客房列表..."
ROOMS_RESPONSE=$(curl -s "http://localhost:8080/api/user/room/list?pageNum=1&pageSize=10")
echo "客房列表响应："
echo "$ROOMS_RESPONSE" | python3 -m json.tool 2>/dev/null | head -30 || echo "$ROOMS_RESPONSE"
echo ""

# 检查客房列表是否成功
CODE=$(echo "$ROOMS_RESPONSE" | grep -o '"code":[0-9]*' | grep -o '[0-9]*')
if [ "$CODE" = "200" ]; then
    echo "✅ 获取客房列表成功"
else
    echo "❌ 获取客房列表失败"
    exit 1
fi

# 测试管理员登录
echo ""
echo "3. 测试管理员登录..."
ADMIN_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

echo "管理员登录响应："
echo "$ADMIN_LOGIN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$ADMIN_LOGIN_RESPONSE"
echo ""

# 检查管理员登录是否成功
CODE=$(echo "$ADMIN_LOGIN_RESPONSE" | grep -o '"code":[0-9]*' | grep -o '[0-9]*')
if [ "$CODE" = "200" ]; then
    echo "✅ 管理员登录成功"
else
    echo "❌ 管理员登录失败"
    exit 1
fi

echo ""
echo "==================================="
echo "所有测试通过！✅"
echo "==================================="
echo ""
echo "访问地址："
echo "  用户端：http://localhost:3000"
echo "  管理端：http://localhost:3000/admin"
echo ""
echo "登录账号："
echo "  用户：user001 / user123"
echo "  管理员：admin / admin123"
