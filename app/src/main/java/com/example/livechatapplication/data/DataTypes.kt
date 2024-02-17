package com.example.livechatapplication.data

data class UserData(
    var name: String? = "",
    var number: String? = "",
   var userId: String? = "",
    var imageUrl: String? = ""



){

    fun toMap() = mapOf(
        "name" to name,
        "number" to number,
        "userId" to userId,
        "imageUrl" to imageUrl
    )
}

data class ChatData(
    val chatId: String? = "",
    val user1 :ChatUser? = ChatUser(),
    val user2 :ChatUser? =ChatUser(),
)

data class ChatUser(
    val name: String? = "",
    val number: String? = "",
    val imageUrl: String? = "",
    val userId: String? = ""
)


data class Message(
    val sendBy: String? = "",
    val message: String? = "",
    val timestamp: String? = ""
)

