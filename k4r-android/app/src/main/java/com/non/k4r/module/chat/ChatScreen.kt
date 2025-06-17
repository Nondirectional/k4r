package com.non.k4r.module.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.non.k4r.module.chat.component.ChatScreen
import com.non.k4r.module.chat.vm.ChatViewModel

/**
 * 聊天界面Screen
 */
@Composable
fun ChatRoute(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val error by viewModel.error.collectAsState()
    
    ChatScreen(
        messages = messages,
        isSending = isSending,
        error = error,
        onSendMessage = viewModel::sendMessage,
        onClearMessages = viewModel::clearMessages,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}