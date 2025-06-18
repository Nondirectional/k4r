from pydantic import BaseModel, EmailStr
from typing import Optional
from datetime import datetime


class UserBase(BaseModel):
    """用户基础模式"""
    username: str
    email: EmailStr
    full_name: Optional[str] = None
    is_active: bool = True


class UserCreate(UserBase):
    """用户创建模式"""
    password: str


class UserUpdate(BaseModel):
    """用户更新模式"""
    username: Optional[str] = None
    email: Optional[EmailStr] = None
    full_name: Optional[str] = None
    password: Optional[str] = None
    is_active: Optional[bool] = None


class UserInDBBase(UserBase):
    """数据库用户基础模式"""
    id: int
    created_at: datetime
    updated_at: datetime
    
    class Config:
        from_attributes = True


class User(UserInDBBase):
    """用户响应模式"""
    pass


class UserInDB(UserInDBBase):
    """数据库中的用户模式"""
    hashed_password: str


# Token相关模式
class Token(BaseModel):
    """令牌模式"""
    access_token: str
    token_type: str


class TokenPayload(BaseModel):
    """令牌载荷模式"""
    sub: Optional[str] = None 