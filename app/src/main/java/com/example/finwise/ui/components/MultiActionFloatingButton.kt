package com.example.finwise.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.finwise.R // Replace with your actual package name
import com.example.finwise.util.Routes

@Composable
fun MultiFloatingActionButton(
    navController : NavHostController
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Column {
                SmallFloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.AddIncomeScreen)
                    },
                    containerColor = Color(0xFF20c997),
                    modifier = Modifier.size(60.dp).padding(bottom = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.img_23),
                        contentDescription = "Add Income",
                        modifier = Modifier.size(30.dp)
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.AddExpenseScreen)
                    },
                    containerColor = Color(0xFF20c997),
                    modifier = Modifier.size(60.dp).padding(bottom = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.img_24),
                        contentDescription = "Add Expense",
                        modifier = Modifier.size(30.dp)
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.AddSavingsScreen)
                    },
                    containerColor = Color(0xFF20c997),
                    modifier = Modifier.size(60.dp).padding(bottom = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.img_24),
                        contentDescription = "Add Savings",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = Color(0xFF20c997),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                modifier = Modifier.rotate(if (expanded) 45f else 0f)
            )
        }
    }
}