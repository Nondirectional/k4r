# K4R Backend API

K4R应用的后端API服务，基于FastAPI、SQLAlchemy、PostgreSQL和Redis构建。

## 技术栈

- **Web框架**: FastAPI
- **ORM框架**: SQLAlchemy 2.0 (异步)
- **数据库**: PostgreSQL
- **缓存**: Redis
- **Python版本**: 3.12
- **包管理**: Conda

## 项目结构

```
k4r-backend/
├── app/                    # 应用主目录
│   ├── api/               # API路由
│   │   └── v1/           # API v1版本
│   │       ├── api.py    # 路由汇总
│   │       └── endpoints/ # 各模块端点
│   ├── core/              # 核心配置
│   │   ├── config.py     # 应用配置
│   │   ├── database.py   # 数据库配置
│   │   ├── redis.py      # Redis配置
│   │   └── security.py   # 安全相关
│   ├── crud/              # 数据库操作
│   ├── models/            # 数据库模型
│   ├── schemas/           # Pydantic模式
│   └── main.py           # FastAPI应用
├── alembic/               # 数据库迁移
├── tests/                 # 测试文件
├── requirements.txt       # Python依赖
├── environment.yml        # Conda环境
├── docker-compose.yml     # Docker编排
├── Dockerfile            # Docker构建
└── run.py                # 启动脚本
```

## 快速开始

### 1. 使用Conda环境

```bash
# 创建并激活Conda环境
conda env create -f environment.yml
conda activate k4r-backend

# 或者使用pip安装依赖
pip install -r requirements.txt
```

### 2. 环境配置

```bash
# 复制环境变量示例文件
cp .env.example .env

# 编辑环境变量（修改数据库连接等配置）
vim .env
```

### 3. 数据库设置

```bash
# 初始化数据库迁移
alembic init alembic

# 创建迁移文件
alembic revision --autogenerate -m "初始化数据库"

# 执行迁移
alembic upgrade head
```

### 4. 启动服务

```bash
# 开发模式启动
python run.py

# 或使用uvicorn直接启动
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 5. 使用Docker

```bash
# 使用Docker Compose启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f api
```

## API文档

启动服务后，访问以下地址查看API文档：

- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## 主要功能

### 认证系统
- JWT令牌认证
- 用户注册/登录
- 权限管理

### 数据库
- 异步PostgreSQL连接
- SQLAlchemy ORM
- 数据库迁移管理

### 缓存
- Redis缓存支持
- 会话管理
- 数据缓存

### API特性
- RESTful API设计
- 自动API文档生成
- 数据验证和序列化
- 错误处理

## 开发

### 数据库迁移

```bash
# 创建新的迁移文件
alembic revision --autogenerate -m "描述变更"

# 应用迁移
alembic upgrade head

# 回滚迁移
alembic downgrade -1
```

### 运行测试

```bash
# 运行所有测试
pytest

# 运行特定测试文件
pytest tests/test_main.py

# 带覆盖率报告
pytest --cov=app tests/
```

### 代码格式化

```bash
# 安装开发依赖
pip install black isort flake8

# 格式化代码
black app/ tests/

# 排序导入
isort app/ tests/

# 代码检查
flake8 app/ tests/
```

## 部署

### 生产环境配置

1. 设置生产环境变量
2. 使用生产级数据库
3. 配置反向代理(Nginx)
4. 设置SSL证书
5. 配置日志记录

### Docker部署

```bash
# 构建生产镜像
docker build -t k4r-backend:latest .

# 运行容器
docker run -d \
  --name k4r-backend \
  -p 8000:8000 \
  -e DATABASE_URL=postgresql+asyncpg://user:pass@host:5432/db \
  -e REDIS_URL=redis://redis-host:6379/0 \
  k4r-backend:latest
```

## 许可证

此项目使用 MIT 许可证。 