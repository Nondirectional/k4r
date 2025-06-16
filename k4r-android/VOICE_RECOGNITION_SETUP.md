# 阿里云Dashscope语音识别服务配置指南

本项目已集成阿里云Dashscope提供的实时语音识别服务，用于替代Android内置的语音识别功能。

## 配置步骤

### 1. 获取阿里云Dashscope API Key

1. 访问 [阿里云控制台](https://ecs.console.aliyun.com/)
2. 登录您的阿里云账号
3. 进入 **模型服务灵积（DashScope）** 控制台
4. 在左侧导航栏选择 **API-KEY管理**
5. 创建新的API Key或使用现有的API Key
6. 复制您的API Key

### 2. 配置API Key

打开文件 `app/src/main/java/com/non/k4r/module/voice/DashscopeConfig.kt`

将以下行：
```kotlin
const val API_KEY = "YOUR_DASHSCOPE_API_KEY"
```

替换为您的实际API Key：
```kotlin
const val API_KEY = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

### 3. 权限配置

项目已自动配置了必要的权限，无需额外操作：
- `RECORD_AUDIO` - 录音权限
- `INTERNET` - 网络访问权限

### 4. 依赖配置

项目已自动添加了必要的依赖：
- `OkHttp` - WebSocket通信
- `org.json` - JSON数据解析

## 使用方法

### 在UI中使用语音输入

语音输入功能已集成到主界面的浮动按钮中：

1. 点击语音输入按钮
2. 授权录音权限（首次使用）
3. 开始说话
4. 系统会自动识别语音并转换为相应的操作

### 支持的语音命令

#### 添加开支记录
- "添加开支 50元 买咖啡"
- "记录支出 100块 午餐"
- "花了 30元 打车"

#### 添加待办事项
- "添加待办 明天开会"
- "记录任务 完成项目报告"
- "新建待办 买菜"

## 技术实现

### 核心组件

1. **DashscopeVoiceService** - 语音识别服务
   - 管理WebSocket连接
   - 处理音频数据传输
   - 解析识别结果

2. **VoiceCommandProcessor** - 语音命令处理器
   - 解析语音识别结果
   - 提取关键信息（金额、描述等）
   - 转换为应用操作

3. **VoiceInputFab** - 语音输入UI组件
   - 权限管理
   - 用户交互界面
   - 状态显示

### WebSocket通信流程

1. 建立WebSocket连接到阿里云Dashscope服务
2. 发送任务启动指令
3. 实时传输音频数据
4. 接收识别结果
5. 处理任务完成或错误

## 故障排除

### 常见问题

1. **"请先在DashscopeConfig中配置阿里云Dashscope API Key"**
   - 检查API Key是否正确配置
   - 确保API Key不是默认值

2. **"连接失败"**
   - 检查网络连接
   - 验证API Key是否有效
   - 确认阿里云账户余额充足

3. **"识别失败"**
   - 检查录音权限是否授权
   - 确保在安静环境中说话
   - 尝试说话更清晰

### 调试信息

在Android Studio的Logcat中查看标签为 `DashscopeVoiceService` 的日志信息，可以帮助诊断问题。

## 参考文档

- [阿里云Dashscope语音识别文档](https://help.aliyun.com/zh/model-studio/websocket-for-paraformer-real-time-service)
- [WebSocket API参考](https://help.aliyun.com/zh/model-studio/websocket-for-paraformer-real-time-service?spm=a2c4g.11186623.0.0.3d1054e0gzmKLY)

## 注意事项

1. **安全性**：请勿将API Key提交到版本控制系统中
2. **费用**：语音识别服务可能产生费用，请关注阿里云账单
3. **网络**：服务需要稳定的网络连接
4. **权限**：确保应用有录音权限