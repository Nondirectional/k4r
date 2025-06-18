import redis.asyncio as redis
from typing import Optional

from app.core.config import settings


class RedisClient:
    """Redis客户端封装"""
    
    def __init__(self):
        self.redis_client: Optional[redis.Redis] = None
    
    async def connect(self):
        """连接Redis"""
        self.redis_client = redis.from_url(
            settings.REDIS_URL,
            encoding="utf-8",
            decode_responses=True
        )
    
    async def disconnect(self):
        """断开Redis连接"""
        if self.redis_client:
            await self.redis_client.close()
    
    async def get(self, key: str) -> Optional[str]:
        """获取缓存值"""
        if not self.redis_client:
            return None
        return await self.redis_client.get(key)
    
    async def set(self, key: str, value: str, expire: Optional[int] = None) -> bool:
        """设置缓存值"""
        if not self.redis_client:
            return False
        
        expire_time = expire or settings.REDIS_EXPIRE_TIME
        return await self.redis_client.setex(key, expire_time, value)
    
    async def delete(self, key: str) -> bool:
        """删除缓存"""
        if not self.redis_client:
            return False
        return bool(await self.redis_client.delete(key))
    
    async def exists(self, key: str) -> bool:
        """检查key是否存在"""
        if not self.redis_client:
            return False
        return bool(await self.redis_client.exists(key))
    
    async def expire(self, key: str, seconds: int) -> bool:
        """设置key过期时间"""
        if not self.redis_client:
            return False
        return bool(await self.redis_client.expire(key, seconds))


# 全局Redis客户端实例
redis_client = RedisClient()


async def init_redis():
    """初始化Redis连接"""
    await redis_client.connect()


async def close_redis():
    """关闭Redis连接"""
    await redis_client.disconnect()


def get_redis() -> RedisClient:
    """获取Redis客户端"""
    return redis_client 