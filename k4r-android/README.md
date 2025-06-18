# K4R Android 应用

K4R是一个集成了用户认证、支出记录、待办事项、AI聊天和语音功能的Android应用。

## 🚀 新功能：用户认证系统

### 功能特性
- ✅ **用户登录** - 支持用户名/密码登录
- ✅ **Token管理** - 自动管理JWT令牌
- ✅ **状态持久化** - 登录状态本地存储
- ✅ **用户信息展示** - 显示当前用户详细信息
- ✅ **Token验证** - 支持手动验证Token有效性
- ✅ **安全登出** - 清除本地Token和状态

### 技术实现
- **网络层**: Retrofit + OkHttp + Kotlinx Serialization
- **状态管理**: Jetpack Compose + ViewModel + StateFlow
- **数据存储**: DataStore Preferences
- **依赖注入**: Hilt
- **认证协议**: JWT Bearer Token

## 📱 登录流程

### 1. 启动应用
应用启动后会自动检查登录状态：
- 如果已登录 → 直接进入主界面
- 如果未登录 → 显示登录界面

### 2. 用户登录
- 输入用户名和密码
- 点击登录按钮
- 系统验证并获取Token
- 登录成功后跳转到主界面

### 3. 用户信息管理
- 在设置页面可以查看当前用户信息
- 支持退出登录功能
- 可以测试Token有效性

## 🛠️ 集成架构

### 网络层架构
```
┌─────────────────┐
│   LoginScreen   │ ← UI层
└─────────────────┘
         │
┌─────────────────┐
│  LoginViewModel │ ← 状态管理
└─────────────────┘
         │
┌─────────────────┐
│  AuthRepository │ ← 业务逻辑
└─────────────────┘
         │
┌─────────────────┐
│   TokenManager  │ ← 本地存储
└─────────────────┘
         │
┌─────────────────┐
│     AuthApi     │ ← 网络请求
└─────────────────┘
```

### 文件结构
```
app/src/main/java/com/non/k4r/
├── core/
│   ├── auth/
│   │   ├── AuthRepository.kt      # 认证仓库
│   │   └── TokenManager.kt        # Token管理
│   └── network/
│       ├── api/
│       │   └── AuthApi.kt          # 认证API接口
│       ├── dto/
│       │   └── AuthDto.kt          # 数据传输对象
│       └── NetworkModule.kt        # 网络模块配置
├── module/
│   ├── auth/
│   │   ├── LoginScreen.kt          # 登录界面
│   │   └── LoginViewModel.kt       # 登录状态管理
│   └── common/
│       ├── K4rRoutes.kt           # 路由定义
│       └── SettingsComponents.kt   # 设置界面（含登出）
└── MainActivity.kt                 # 主活动
```

## 🔧 配置说明

### 1. 网络配置
在 `NetworkModule.kt` 中配置后端API地址：
```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/"  // 模拟器
// 或者
private const val BASE_URL = "http://192.168.1.100:8000/"  // 真机
```

### 2. 依赖配置
已添加的网络相关依赖：
```kotlin
// 网络请求
implementation(libs.retrofit)
implementation(libs.retrofit.kotlinx.serialization)
implementation(libs.logging.interceptor)
implementation(libs.okhttp)

// 数据存储
implementation(libs.androidx.datastore.preferences)
```

## 🎯 使用演示

### 演示账户
后端提供了演示账户供测试使用：

**管理员账户:**
- 用户名: `admin`
- 密码: `admin123`

**普通用户账户:**
- 用户名: `demo`
- 密码: `demo123`

### 测试步骤
1. 启动后端服务（参考 k4r-backend/README.md）
2. 运行 `python create_demo_user.py` 创建演示用户
3. 启动Android应用
4. 使用演示账户登录测试

## 🔒 安全特性

### Token管理
- JWT令牌安全存储在DataStore中
- 自动添加Bearer认证头
- Token过期自动清除本地状态

### 错误处理
- 网络错误友好提示
- 401/403状态码自动登出
- 输入验证和错误显示

### 状态管理
- 登录状态实时同步
- 跨界面状态一致性
- 内存泄漏防护

## 🚧 开发指南

### 添加新的认证API
1. 在 `AuthApi.kt` 中添加接口定义
2. 在 `AuthDto.kt` 中添加数据模型
3. 在 `AuthRepository.kt` 中实现业务逻辑
4. 在 `LoginViewModel.kt` 中添加UI状态管理

### 自定义登录界面
- 修改 `LoginScreen.kt` 中的UI组件
- 更新 `LoginViewModel.kt` 中的状态逻辑
- 调整主题和样式

### 集成其他认证方式
- 扩展 `AuthApi.kt` 接口
- 添加相应的数据模型
- 实现对应的业务逻辑

## 📋 API文档

### 登录接口
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### 获取用户信息
```http
GET /api/v1/users/me
Authorization: Bearer <token>
```

### Token验证
```http
POST /api/v1/auth/test-token
Authorization: Bearer <token>
```

## 🐛 故障排除

### 常见问题

1. **登录失败：网络错误**
   - 检查后端服务是否启动
   - 确认网络地址配置正确
   - 查看日志中的网络请求详情

2. **Token过期**
   - 检查后端Token有效期配置
   - 确认系统时间正确
   - 重新登录获取新Token

3. **界面状态异常**
   - 检查ViewModel中的状态管理
   - 确认StateFlow更新逻辑
   - 重启应用重置状态

### 调试技巧
- 开启网络日志查看请求详情
- 使用 `LoginViewModel.testToken()` 验证Token
- 检查DataStore中的存储数据

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

在提交代码前，请确保：
- 代码符合项目规范
- 添加必要的注释
- 测试功能正常
- 更新相关文档

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。 