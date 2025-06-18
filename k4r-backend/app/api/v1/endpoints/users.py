from typing import Any, List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.database import get_db
from app.crud import crud_user
from app.crud.crud_user import get_current_active_user, get_current_active_superuser
from app.schemas import user as user_schemas
from app.models.user import User

router = APIRouter()


@router.get("/", response_model=List[user_schemas.User])
async def read_users(
    db: AsyncSession = Depends(get_db),
    skip: int = 0,
    limit: int = 100,
    current_user: User = Depends(get_current_active_superuser),
) -> Any:
    """获取用户列表（仅超级用户）"""
    users = await crud_user.get_multi(db, skip=skip, limit=limit)
    return users


@router.post("/", response_model=user_schemas.User)
async def create_user(
    *,
    db: AsyncSession = Depends(get_db),
    user_in: user_schemas.UserCreate,
    current_user: User = Depends(get_current_active_superuser),
) -> Any:
    """创建新用户（仅超级用户）"""
    user = await crud_user.get_by_email(db, email=user_in.email)
    if user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="该邮箱已被注册"
        )
    user = await crud_user.create(db, obj_in=user_in)
    return user


@router.put("/me", response_model=user_schemas.User)
async def update_user_me(
    *,
    db: AsyncSession = Depends(get_db),
    password: Optional[str] = None,
    full_name: Optional[str] = None,
    email: Optional[str] = None,
    current_user: User = Depends(get_current_active_user),
) -> Any:
    """更新当前用户信息"""
    current_user_data = user_schemas.UserUpdate(**current_user.__dict__)
    if password is not None:
        current_user_data.password = password
    if full_name is not None:
        current_user_data.full_name = full_name
    if email is not None:
        current_user_data.email = email
    user = await crud_user.update(db, db_obj=current_user, obj_in=current_user_data)
    return user


@router.get("/me", response_model=user_schemas.User)
async def read_user_me(
    current_user: User = Depends(get_current_active_user),
) -> Any:
    """获取当前用户信息"""
    return current_user


@router.get("/{user_id}", response_model=user_schemas.User)
async def read_user_by_id(
    user_id: int,
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db),
) -> Any:
    """根据ID获取特定用户"""
    user = await crud_user.get(db, id=user_id)
    if user == current_user:
        return user
    if not crud_user.is_superuser(current_user):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="权限不足"
        )
    return user


@router.put("/{user_id}", response_model=user_schemas.User)
async def update_user(
    *,
    db: AsyncSession = Depends(get_db),
    user_id: int,
    user_in: user_schemas.UserUpdate,
    current_user: User = Depends(get_current_active_superuser),
) -> Any:
    """更新用户信息（仅超级用户）"""
    user = await crud_user.get(db, id=user_id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="用户不存在"
        )
    user = await crud_user.update(db, db_obj=user, obj_in=user_in)
    return user 