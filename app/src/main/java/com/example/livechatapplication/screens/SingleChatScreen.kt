package com.example.livechatapplication.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechatapplication.CommonDivider
import com.example.livechatapplication.CommonImage
import com.example.livechatapplication.LCViewModel
import com.example.livechatapplication.data.Message

@Composable
fun SingleChatScreen(navController: NavController, chatId: String, vm: LCViewModel) {

    var reply by rememberSaveable { mutableStateOf("") }
    var chatMessage by remember { vm.chatMessages }

    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }

    val myUser = vm.userData.value
    val currentChat = vm.chats.value.firstOrNull { it.chatId == chatId }
    val chatUser = currentChat?.let {
        if (myUser?.userId == it.user1?.userId) it.user2 else it.user1
    }

    LaunchedEffect(key1 = Unit) {
        vm.populateMessages(chatId)
    }

    BackHandler {
        vm.depopulateMessages()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        ChatHeader(name = chatUser?.name ?: "", imageUrl = chatUser?.imageUrl ?: "") {
            navController.popBackStack()
            vm.depopulateMessages()
        }

        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessages = chatMessage,
            currentUserId = myUser?.userId ?: ""
        )

        Spacer(modifier = Modifier.weight(1f))
        ReplyBox(
            reply = reply,
            onReplyChange = { reply = it },
            onSendReply = onSendReply
        )
    }
}

@Composable
fun MessageBox(modifier: Modifier,chatMessages: List<Message>, currentUserId :String){

    LazyColumn{

        items(chatMessages){

            msg ->
            val alignments = if(msg.sendBy == currentUserId)Alignment.End else Alignment.Start
            val color = if(msg.sendBy == currentUserId) Color(0xFF68C400) else Color(0xFFE0C0C0)
            Column (modifier = Modifier.
            fillMaxSize()
                .padding(8.dp),
                horizontalAlignment = alignments){
                Text(text =msg.message?:"",modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(color = color).padding(12.dp),
                    color=Color.White,
                    fontWeight = FontWeight.Bold
                )


            }

        }

    }

}


@Composable
fun ChatHeader(
    name :String,
    imageUrl :String,
    onBackClicked:()->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ){
        
        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null,
            modifier = Modifier
                .clickable { onBackClicked.invoke() }
                .padding(8.dp))
        CommonImage(data = imageUrl,modifier = Modifier
            .padding(8.dp)
            .size(50.dp)
            .clip(CircleShape))
        Text(text = name, fontWeight = FontWeight.Bold,modifier = Modifier.padding(start =8.dp))
        
    }
}


@Composable
fun ReplyBox(
    reply :String,
    onReplyChange:(String)->Unit,
    onSendReply:()->Unit
){

    Column(
        modifier= Modifier.fillMaxWidth()
    ) {

        CommonDivider()
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween){

            TextField(value = reply, onValueChange = onReplyChange, maxLines = 3)
            Button(onClick = onSendReply) {
                Text(text = "Send")

            }

        }
    }

}