package com.example.finwise.ui.auth.login2

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finwise.R  // Replace with your actual package name
import com.example.finwise.ui.components.LoadingIndicator
import com.example.finwise.util.Result
import com.example.finwise.util.UiEvent

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen2(
viewModel2: LoginViewModel2 = hiltViewModel(),
onNavigate: (UiEvent.Navigate) -> Unit,
) {

    val email by viewModel2.email.collectAsStateWithLifecycle()
    val password by viewModel2.password.collectAsStateWithLifecycle()
    val signInResponse by viewModel2.signInState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel2.uiEvent.collect{event->
            when(event){
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    when(signInResponse) {
        is Result.Idle -> {}
        is Result.Loading -> {
            LoadingIndicator()
        }
        is Result.Success<*> -> LaunchedEffect(Unit) {
            viewModel2.onLoginEvent2(LoginEvent2.onLoginButtonClick)
        }
        is Result.Error -> signInResponse.let {
            LaunchedEffect(key1 = it) {
                Log.e("", it.toString())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF20c997)) // Teal background
    ) {
        // Teal Section (Content)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f), // Teal takes 30% of the screen
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome",
                style = TextStyle(
                    color = Color(0xFF0e493d),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                )
            )
        }

        // White Box Section (70% of the screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f) // White takes 70%
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFf0faf5)), // light background

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                
            ) {
                Text(
                    text = "Username Or Email",
                    style = TextStyle(fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0e493d),
                        fontFamily = FontFamily(Font(R.font.poppins_regular))

                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                          viewModel2.onLoginEvent2(LoginEvent2.onEmailChange(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(25.dp)), // Rounded corners

                    placeholder = { Text("example@example.com", style = TextStyle(fontSize = 14.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.poppins_regular)))) },
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFecf8f4),
                        focusedBorderColor = Color(0xFFecf8f4),
                        unfocusedContainerColor = Color(0xFFecf8f4),
                        focusedContainerColor = Color(0xFFecf8f4),
                    ),
                    //isError = !isValidEmail(email)
                )
                Text(
                    text = "Password",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0e493d),
                        fontFamily = FontFamily(Font(R.font.poppins_regular)))
                )
                var passwordVisible by remember { mutableStateOf(false) }

                val eyeIcon = painterResource(id = R.drawable.img_4)
                val eyeIconOff = painterResource(id = R.drawable.img_3)
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                          viewModel2.onLoginEvent2(LoginEvent2.onPasswordChange(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(25.dp)), // Rounded corners

                    placeholder = { Text("••••••••", style = TextStyle(fontSize = 14.sp, color = Color.Gray), fontFamily = FontFamily(Font(R.font.poppins_regular)), letterSpacing = 4.sp) },
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFecf8f4),
                        focusedBorderColor = Color(0xFFecf8f4),
                        unfocusedContainerColor = Color(0xFFecf8f4),
                        focusedContainerColor = Color(0xFFecf8f4),
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {

                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(
                                painter = if (passwordVisible) eyeIcon else eyeIconOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF0E3E3E)
                            )
                        }
                    }
                )
                Button(
                    onClick = {
                        viewModel2.onLoginEvent2(LoginEvent2.onLoginButtonClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20c997)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Log In",
                        style = TextStyle(color = Color.White, fontSize = 16.sp)
                    )
                }

                Text(
                    text = "Forgot Password?",
                    style = TextStyle(fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                         viewModel2.onLoginEvent2(LoginEvent2.onSignupButtonClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFecf8f4)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        style = TextStyle(color = Color.Black, fontSize = 16.sp)
                    )
                }

                Text(
                    text = "Use Fingerprint To Access",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "or sign up with",
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background), // Replace
                        contentDescription = "Facebook",
                        modifier = Modifier.size(40.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background), // Replace
                        contentDescription = "Google",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}


