package com.example.livechatapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechatapplication.LCViewModel
import com.example.livechatapplication.TitleText

@Composable
fun StatusScreen(
    navController: NavController,
    vm: LCViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TitleText(txt = "Task Management")
        TaskList(
            tasks = vm.tasks.value,
            onTaskChecked = { task ->
                vm.toggleTaskComplete(task)
            },
            onDeleteTask = { task ->
                vm.deleteTask(task)
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        TaskInputField(onTaskAdded = { task ->
            vm.addTask(task)
        })
        Spacer(modifier = Modifier.height(16.dp))
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.STATUSLIST,
            navController = navController
        )
    }
}

@Composable
fun TaskInputField(onTaskAdded: (String) -> Unit) {
    var taskText by remember { mutableStateOf(TextFieldValue()) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = taskText,
            onValueChange = { taskText = it },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                if (taskText.text.isNotBlank()) {
                    onTaskAdded(taskText.text)
                    taskText = TextFieldValue()
                }
            }
        ) {
            Text("Add Task")
        }
    }
}

@Composable
fun TaskList(
    tasks: List<String>,
    onTaskChecked: (String) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    LazyColumn {
        items(tasks) { task ->
            TaskItem(task = task, onTaskChecked = onTaskChecked, onDeleteTask = onDeleteTask)
        }
    }
}

@Composable
fun TaskItem(task: String, onTaskChecked: (String) -> Unit, onDeleteTask: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = { onTaskChecked(task) }
        )
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = "Checkbox",
            modifier = Modifier
                .clickable { onTaskChecked(task) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = task,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Rounded.Delete,
            contentDescription = "Delete",
            modifier = Modifier.clickable { onDeleteTask(task) }
        )
    }
}


