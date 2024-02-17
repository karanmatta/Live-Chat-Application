package com.example.livechatapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.livechatapplication.screens.ChatScreenList
import com.example.livechatapplication.screens.SignUpScreen
import com.example.livechatapplication.screens.LoginScreen
import com.example.livechatapplication.screens.ProfileScreen
import com.example.livechatapplication.screens.SingleChatScreen
import com.example.livechatapplication.screens.StatusScreen

import com.example.livechatapplication.ui.theme.LiveChatApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
sealed class DestinationScreen(var route: String) {
    object SignUp : DestinationScreen("signUp")
    object Login : DestinationScreen("login")
    object ChatList : DestinationScreen("chat")
    object Profile : DestinationScreen("profile")
    object StatusList : DestinationScreen("status")
    object SingleChat : DestinationScreen("singleChat/{chatId}") { // <- Add the closing curly brace here
        fun createRoute(id: String): String {
            return "singleChat/$id"
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveChatApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
ChatAppNavigation()
                }
            }
        }
    }
    @Composable
    fun ChatAppNavigation(){

val navController = rememberNavController()
        val vm = hiltViewModel<LCViewModel>()

        NavHost(navController = navController , startDestination = DestinationScreen.SignUp.route){
            composable(DestinationScreen.SignUp.route){
                SignUpScreen(navController = navController,vm = vm)
            }
            composable(DestinationScreen.Login.route){
                LoginScreen(navController = navController,vm = vm)
            }
            composable(DestinationScreen.ChatList.route){
                ChatScreenList(navController = navController,vm = vm)
            }

            composable(DestinationScreen.SingleChat.route) {
                val chatId = it.arguments?.getString("chatId")
                chatId?.let{
                    SingleChatScreen(navController = navController,chatId = chatId,vm = vm)
                }

            }


            composable(DestinationScreen.StatusList.route){
                StatusScreen(navController = navController,vm = vm)
            }
            composable(DestinationScreen.Profile.route){
                ProfileScreen(navController = navController,vm = vm)
            }

        }


    }
}



