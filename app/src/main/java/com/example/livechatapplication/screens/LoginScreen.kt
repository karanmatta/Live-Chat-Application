package com.example.livechatapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.livechatapplication.CheckSignIn
import com.example.livechatapplication.CommonProgressBar
import com.example.livechatapplication.DestinationScreen
import com.example.livechatapplication.LCViewModel
import com.example.livechatapplication.R
import com.example.livechatapplication.navigateTo

@Composable
fun LoginScreen(vm: LCViewModel,navController: NavController){


        CheckSignIn(vm,navcontroller = navController)

        Box(modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ), horizontalAlignment = Alignment.CenterHorizontally) {


                val emailState = remember { mutableStateOf(TextFieldValue("")) }
                val passwordState = remember { mutableStateOf(TextFieldValue("")) }

                val focus = LocalFocusManager.current

                Image(
                    painter = painterResource(id = R.drawable.chat),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(200.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                        .padding(8.dp)
                )
                Text(text = "Sign In ",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(8.dp))


                OutlinedTextField(
                    value = emailState.value, onValueChange = {
                        emailState.value=it
                    },
                    label = { Text("Email") },
                    modifier = Modifier.padding(8.dp)
                )

                OutlinedTextField(
                    value = passwordState.value, onValueChange = {
                        passwordState.value=it
                    },
                    label = { Text("Password") },
                    modifier = Modifier.padding(8.dp)
                )

                Button(onClick = {  vm.Login(emailState.value.text,passwordState.value.text) },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(text ="SIGN IN")

                }

                Text(text = "New User? Go to Sign Up",
                    color = Color.Blue,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigateTo(navController, DestinationScreen.SignUp.route)
                        })

            }

        }
        if(vm.inProgress.value) {

            CommonProgressBar()
        }




    }