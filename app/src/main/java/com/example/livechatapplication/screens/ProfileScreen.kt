package com.example.livechatapplication.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechatapplication.CommonDivider
import com.example.livechatapplication.CommonImage
import com.example.livechatapplication.CommonProgressBar
import com.example.livechatapplication.DestinationScreen
import com.example.livechatapplication.LCViewModel
import com.example.livechatapplication.navigateTo


@Composable
fun ProfileScreen(navController: NavController,vm : LCViewModel) {



    val inProgress = vm.inProgress.value

    if (inProgress) {
        CommonProgressBar()
    } else {

        val userData = vm.userData.value
        var name by rememberSaveable{
            mutableStateOf(userData?.name?:"")
        }
        var number by rememberSaveable{
            mutableStateOf(userData?.number?:"")
        }

        Column {

            ProfileContent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                vm = vm,
                onNameChange = {name = it},
                onNumberChange = {number = it   },
                onBack = {
                         navigateTo(navController=navController, route =DestinationScreen.ChatList.route)
                },
                onSave = {
                    vm.createOrUpdateProfile(name = name, number= number)
                },
                name = name,
                number = number,
                onLogout = {
                    vm.logout()
                    navigateTo(navController = navController, route = DestinationScreen.Login.route)

                }

            )




        // Bottom Navigation Menu
            Spacer(modifier =Modifier.height(300.dp))
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.PROFILE,
            navController = navController
        )
    }


    }

}








@Composable
fun ProfileContent(
    vm: LCViewModel,
    modifier: Modifier,
    onBack: () -> Unit = { },
    onSave: () -> Unit = { },
    name: String,
    number: String,
    onNameChange :(String) -> Unit,
    onNumberChange :(String) -> Unit,
    onLogout: () -> Unit = { }

){

    val imageUrl = vm.userData.value?.imageUrl

    // Profile content goes here
    Column{
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Back", Modifier.clickable {
                onBack.invoke()
            })
            Text(text = "Save", Modifier.clickable {
                onSave.invoke()
            })

        }
            // Divider
            CommonDivider()
            ProfileImage(imageUrl = imageUrl, vm = vm)

            CommonDivider()

            Row (modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
            , verticalAlignment = Alignment.CenterVertically){
                Text(text = "Name", Modifier.width(100.dp))
                TextField(value = name, onValueChange = onNameChange,
                    colors = TextFieldDefaults.colors(

                        focusedTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ))

            }
            Row (modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                , verticalAlignment = Alignment.CenterVertically){

                Text(text = "Number", Modifier.width(100.dp))
                TextField(value = number, onValueChange = onNumberChange,
                    colors = TextFieldDefaults.colors(

                        focusedTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                )

            }
            CommonDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                Text(text ="Logout", Modifier.clickable {
                    onLogout.invoke()
                })

            }





    }


}

@Composable
fun ProfileImage(imageUrl :String? ,  vm : LCViewModel ) {
    // Profile image goes here

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) {
        uri ->
        uri?.let {
            vm.uploadProfileImage(uri)
        }

    }

    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min))
    {
        Column(
            modifier = Modifier
                .padding(1.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally)
        {

            Card(shape  = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)){

                // Image goes here

            CommonImage(data =imageUrl)


            }
            Text(text = "Change Profile Picture",modifier = Modifier.padding(8.dp),color = Color.Gray)


        }

        val isLoading = vm.inProgress.value
        if(isLoading){
            CommonProgressBar()
        }


    }
}
