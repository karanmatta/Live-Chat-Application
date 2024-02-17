package com.example.livechatapplication

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.livechatapplication.data.CHATS
import com.example.livechatapplication.data.ChatData
import com.example.livechatapplication.data.ChatUser
import com.example.livechatapplication.data.Event
import com.example.livechatapplication.data.MESSAGES
import com.example.livechatapplication.data.Message
import com.example.livechatapplication.data.USER_NODE
import com.example.livechatapplication.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel@Inject constructor(
    val auth : FirebaseAuth,
    val db : FirebaseFirestore,
    val storage: FirebaseStorage
):ViewModel() {

    var inProgress = mutableStateOf(false)
    var inProcessChat = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessages = mutableStateOf(false)
    var currentChatMessageListener :ListenerRegistration? = null

    //Dagger Apne Aap handle krlega
    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }

    }


    fun populateMessages(chatId: String) {
        inProgressChatMessages.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGES)
            .addSnapshotListener() { value, error ->
                if (error != null) {
                    handleException(error)
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.timestamp }
                    inProgressChatMessages.value = false
                }

            }
    }

    fun depopulateMessages(){
        chatMessages.value = listOf()
        currentChatMessageListener =null
    }





    // Getting chats

    fun populateChats() {

        inProcessChat.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChat.value = false

            }

        }

    }
    fun onSendReply(chatId:String , message :String){

        val time = Calendar.getInstance().time.toString()
        val msg = Message(
            userData.value?.userId,
            message,
            time

        )
        db.collection(CHATS).document(chatId).collection(MESSAGES).document().set(msg)

    }






    fun signUp(name: String, number: String, email: String, password: String) {
        inProgress.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields")
            return
        }
        inProgress.value = true

        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {}

            } else {
                handleException(customMessage = "Number already exists")
            }
        }.addOnFailureListener {
            handleException(it, customMessage = "Cannot check number")
        }

        //sign up logic
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                signIn.value = true
                createOrUpdateProfile(name, number)


            } else {
                handleException(it.exception, customMessage = "Sign Up Failed")


            }
        }


    }

    fun Login(email: String, password: String) {
        inProgress.value = true
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields")
            return
        } else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signIn.value = true
                    inProgress.value = false
                    val uid = auth.currentUser?.uid
                    uid?.let {
                        getUserData(it)
                    }
                } else {
                    handleException(it.exception, customMessage = "Login Failed")
                }
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) { imageUrl ->
            createOrUpdateProfile(imageurl = imageUrl.toString())
        }
    }

    // Function to upload image to storage
    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        // Upload image logic
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/${uuid}")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            val result = taskSnapshot.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener { uri ->
                onSuccess(uri)
                inProgress.value = false
            }
        }.addOnFailureListener { exception ->
            handleException(exception, customMessage = "Image Upload Failed")
        }
    }




    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageurl: String? = null
    ) {

        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageurl ?: userData.value?.imageUrl


        )
        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {

                if (it.exists()) {
                    db.collection(USER_NODE).document(uid).update(userData.toMap())
                    inProgress.value = false
                    getUserData(uid)

                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserData(uid)
                }
            }.addOnFailureListener {
                handleException(it, customMessage = "Cannot Reatrieve User Data")

            }


        }


    }

    private fun getUserData(uid: String) {

        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, customMessage = "Cannot Retrieve User Data")
            }
            if (value != null) {
                val user = value.toObject(UserData::class.java)
                userData.value = user
                inProgress.value = false
                populateChats()
            }

        }


    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("LiveChatApp", "Live Chat Exception", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMutableState.value = Event(message)
        inProgress.value = false


        //handle exception
    }

    fun logout() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        eventMutableState.value = Event("Logged Out")
        depopulateMessages()
        currentChatMessageListener=null
    }

    fun onAddChat(number: String) {

        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Please enter a valid number")
            return
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number)
                        .get().addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "User not found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    user1 = ChatUser(
                                        userId = userData?.value?.userId,
                                        name = userData?.value?.name,
                                        imageUrl = userData?.value?.imageUrl,
                                        number = userData?.value?.number
                                    ),
                                    user2 = ChatUser(
                                        userId = chatPartner.userId,
                                        name = chatPartner.name,
                                        imageUrl = chatPartner.imageUrl,
                                        number = chatPartner.number
                                    )
                                )


                                db.collection(CHATS).document(id).set(chat)

                            }
                        }
                        .addOnFailureListener {
                            handleException(it)
                        }
                } else {
                    handleException(customMessage = "Chat already exists")
                }
            }
        }


    }
    var tasks = mutableStateOf<List<String>>(emptyList())

    fun addTask(task: String) {
        tasks.value += task
    }

    fun toggleTaskComplete(task: String) {
        val updatedTasks = tasks.value.toMutableList()
        val index = updatedTasks.indexOf(task)
        if (index != -1) {
            updatedTasks[index] = "✔️ $task" // Add a marker to indicate task completion
            tasks.value = updatedTasks
        }
    }

    fun deleteTask(task: String) {
        tasks.value -= task
    }
}






