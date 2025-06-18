# K4R 登录功能快速启动指南

本指南将帮助你快速设置并测试K4R应用的登录功能。

## 🚀 快速启动步骤

### 1. 启动后端服务

```bash
# 进入后端目录
cd k4r-backend

# 创建并激活Conda环境
conda env create -f environment.yml
conda activate k4r-backend

# 启动服务
python run.py
```

后端服务将在 `http://localhost:8000` 启动。

### 2. 创建演示用户

```bash
# 在后端目录下运行
python create_demo_user.py
```

这将创建以下演示账户：
- **管理员**: `admin` / `admin123`
- **普通用户**: `demo` / `demo123`

### 3. 测试API（可选）

使用浏览器访问 `http://localhost:8000/docs` 查看API文档，或者使用curl测试：

```bash
# 测试登录API
curl -X POST "http://localhost:8000/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### 4. 启动Android应用

1. 在Android Studio中打开 `k4r-android` 项目
2. 确保网络配置正确：
   - 模拟器：`http://10.0.2.2:8000/`
   - 真机：`http://192.168.1.100:8000/`（替换为你的IP）
3. 运行应用
4. 使用演示账户登录

## 🔧 网络配置

### Android模拟器
默认配置已设置为模拟器地址：`http://10.0.2.2:8000/`

### Android真机
如果使用真机测试，需要修改网络配置：

1. 找到你的电脑IP地址：
   ```bash
   # Windows
   ipconfig
   
   # macOS/Linux
   ifconfig
   ```

2. 更新 `k4r-android/app/src/main/java/com/non/k4r/core/network/NetworkModule.kt`：
   ```kotlin
   private const val BASE_URL = "http://YOUR_IP:8000/"  // 替换YOUR_IP
   ```

3. 确保防火墙允许8000端口访问

## 📱 测试功能

### 登录测试
1. 启动应用后将显示登录界面
2. 输入演示账户信息
3. 点击登录按钮
4. 成功后跳转到主界面

### 用户信息查看
1. 登录后在设置页面可以查看用户信息
2. 点击"测试Token"验证Token有效性

### 登出测试
1. 在设置页面点击"退出登录"
2. 确认登出后返回登录界面
3. 验证需要重新登录才能访问

## 🐛 常见问题

### 1. 后端启动失败
```bash
# 检查Python版本
python --version  # 应该是 3.12.x

# 检查依赖安装
pip list | grep fastapi

# 重新安装依赖
conda env remove -n k4r-backend
conda env create -f environment.yml
```

### 2. 数据库连接失败
```bash
# 检查PostgreSQL是否启动
# 或者使用SQLite测试（修改配置）
```

### 3. Android网络请求失败
- 检查后端服务是否启动：`curl http://localhost:8000/health`
- 检查网络地址配置是否正确
- 查看Android日志中的网络错误信息

### 4. Token验证失败
- 检查系统时间是否正确
- 确认Token未过期（默认8天）
- 重新登录获取新Token

## 📊 API测试示例

### 完整登录流程测试

```bash
# 1. 登录获取Token
TOKEN=$(curl -s -X POST "http://localhost:8000/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' | \
  jq -r '.access_token')

echo "Token: $TOKEN"

# 2. 使用Token获取用户信息
curl -X GET "http://localhost:8000/api/v1/users/me" \
  -H "Authorization: Bearer $TOKEN"

# 3. 测试Token
curl -X POST "http://localhost:8000/api/v1/auth/test-token" \
  -H "Authorization: Bearer $TOKEN"
```

## 🎯 下一步

登录功能测试成功后，你可以：

1. **扩展用户管理功能**
   - 添加用户注册界面
   - 实现密码重置功能
   - 添加用户权限管理

2. **完善安全机制**
   - 实现Token自动刷新
   - 添加生物识别登录
   - 增强密码安全策略

3. **优化用户体验**
   - 添加记住密码功能
   - 实现自动登录
   - 美化登录界面

4. **集成更多功能**
   - 用户数据同步
   - 多设备登录管理
   - 社交登录集成

## 📞 获取帮助

如果遇到问题：
1. 查看详细错误日志
2. 参考完整README文档
3. 检查网络和配置
4. 提交Issue描述问题

祝你使用愉快！🎉 