package com.non.k4r.module.chat

import android.content.Context
import com.non.k4r.core.data.database.dao.ExpenditureRecordDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.dao.TodoRecordDao
import com.non.k4r.module.chat.tool.builtin.AddExpenditureRecordTool
import com.non.k4r.module.chat.tool.builtin.AddTodoRecordTool
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 聊天服务管理器
 * 负责管理DashscopeChatService和注册需要依赖注入的工具
 */
@Singleton
class ChatServiceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordDao: RecordDao,
    private val expenditureRecordDao: ExpenditureRecordDao,
    private val todoRecordDao: TodoRecordDao
) {
    
    private var _chatService: DashscopeChatService? = null
    
    /**
     * 获取聊天服务实例
     */
    fun getChatService(): DashscopeChatService {
        if (_chatService == null) {
            _chatService = DashscopeChatService(context).apply {
                // 注册需要依赖注入的工具
                registerTool(AddExpenditureRecordTool(context, recordDao, expenditureRecordDao))
                registerTool(AddTodoRecordTool(context, recordDao, todoRecordDao))
            }
        }
        return _chatService!!
    }
    
    /**
     * 销毁聊天服务
     */
    fun destroyChatService() {
        _chatService?.destroy()
        _chatService = null
    }
} 