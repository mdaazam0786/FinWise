package com.example.finwise.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.finwise.R  // Replace with your actual package name
import com.example.finwise.util.UiEvent

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect{event->
            when(event){
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FAF5)), // Light background color
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img), // Replace with your graph icon
            contentDescription = "FinWise Icon",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "FinWise",
            style = TextStyle(
                color = Color(0xFF20c997), // FinWise text color
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poppins_regular))
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome to FinWise – take control of your money, one wise decision at a time!",
            style = TextStyle(
                color = Color.Gray, // Grayish text color
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                 viewModel.onLoginEvent(LoginEvent.onLoginButtonClick)
            },
            modifier = Modifier
                .width(280.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20c997)), //teal color
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(text = "Log In", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                  viewModel.onLoginEvent(LoginEvent.onSignupButtonClick)
            },
            modifier = Modifier
                .width(280.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFecf8f4)), //light teal
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(text = "Sign Up", color = Color.Black, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Forgot Password?",
            style = TextStyle(
                color = Color.Gray,
                fontSize = 14.sp
            ),
            modifier = Modifier.clickable { viewModel.onLoginEvent(LoginEvent.onForgetPasswordButtonClick) }
        )
    }
}
