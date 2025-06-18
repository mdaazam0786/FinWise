package com.example.finwise.ui.auth.signup

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finwise.R  // Replace with your actual package name
import com.example.finwise.ui.auth.login2.LoginEvent2
import com.example.finwise.ui.components.LoadingIndicator
import com.example.finwise.util.Result
import com.example.finwise.util.UiEvent

@Composable
fun SignUpScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    onNavigate : (UiEvent.Navigate) -> Unit
) {
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val confirmPassword by viewModel.confirmPassword.collectAsStateWithLifecycle()
    val mobileNumber by viewModel.mobileNumber.collectAsStateWithLifecycle()
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val dateOfBirth by viewModel.dateOfBirth.collectAsStateWithLifecycle()
    val signupState by viewModel.signUpState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect{event->
            when(event){
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    when(signupState) {
        is Result.Idle -> {}
        is Result.Loading -> {
            LoadingIndicator()
        }
        is Result.Success<*> -> LaunchedEffect(Unit) {
            viewModel.onSignupEvent(SignupEvent.SignUpClicked)
        }
        is Result.Error -> signupState.let {
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
        // Teal Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
                style = TextStyle(
                    color = Color(0xFF0e493d),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                )
            )
        }

        // White Box Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFf0faf5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            ) {
                // Form Fields
                Text(
                    text = "Full Name",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.poppins_regular)),color = Color(0xff363130))
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                         viewModel.onSignupEvent(SignupEvent.OnFullNameChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    placeholder = {
                        Text(
                            "example@example.com",
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray,fontFamily = FontFamily(Font(R.font.poppins_regular)))
                        )
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFecf8f4),
                        focusedBorderColor = Color(0xFFecf8f4),
                        unfocusedContainerColor = Color(0xFFecf8f4),
                        focusedContainerColor = Color(0xFFecf8f4)
                    )
                )

                Text(
                    text = "Email",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.poppins_regular)),color = Color(0xff363130))
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                         viewModel.onSignupEvent(SignupEvent.OnEmailChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    placeholder = {
                        Text(
                            "example@example.com",
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray,fontFamily = FontFamily(Font(R.font.poppins_regular)))
                        )
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFecf8f4),
                        focusedBorderColor = Color(0xFFecf8f4),
                        unfocusedContainerColor = Color(0xFFecf8f4),
                        focusedContainerColor = Color(0xFFecf8f4)
                    )
                )

                Text(
                    text = "Mobile Number",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.poppins_regular)),color = Color(0xff363130))
                )
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        viewModel.onSignupEvent(SignupEvent.OnMobileNumber(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    placeholder = {
                        Text(
                            "+ 123 456 789",
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray,fontFamily = FontFamily(Font(R.font.poppins_regular)))
                        )
                    },
                    shape = RoundedCornerShape(25.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFecf8f4),
                        focusedBorderColor = Color(0xFFecf8f4),
                        unfocusedContainerColor = Color(0xFFecf8f4),
                        focusedContainerColor = Color(0xFFecf8f4)
                    )
                )

                Text(
                    text = "Date Of Birth",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.poppins_regular)),color = Color(0xff363130))
                )
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = {
                           viewModel.onSignupEvent(SignupEvent.OnDateOfBirth(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    placeholder = {
                        Text(
                            "DD / MM / YYYY",
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray,fontFamily = FontFamily(Font(R.font.poppins_regular)))
                        )
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFecf8f4),
                        focusedBorderColor = Color(0xFFecf8f4),
                        unfocusedContainerColor = Color(0xFFecf8f4),
                        focusedContainerColor = Color(0xFFecf8f4)
                    )
                )

                Text(
                    text = "Password",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.poppins_regular)),color = Color(0xff363130))
                )
                var passwordVisible by remember { mutableStateOf(false) }
                val eyeIcon = painterResource(id = R.drawable.img_4)
                val eyeIconOff = painterResource(id = R.drawable.img_3)
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        viewModel.onSignupEvent(SignupEvent.OnPasswordChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    placeholder = {
                        Text(
                            "••••••••",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                letterSpacing = 4.sp
                            )
                        )
                    },
                    shape = RoundedCornerShape(25.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFecf8f4),
                        focusedBorderColor = Color(0xFFecf8f4),
                        unfocusedContainerColor = Color(0xFFecf8f4),
                        focusedContainerColor = Color(0xFFecf8f4)
                    ),
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

                Text(
                    text = "Confirm Password",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold,color = Color(0xff363130),fontFamily = FontFamily(Font(R.font.poppins_regular)))
                )
                var confirmPasswordVisible by remember { mutableStateOf(false) }
                var passwordMatchError by remember { mutableStateOf<String?>(null) }
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                          viewModel.onSignupEvent(SignupEvent.OnConfirmPasswordChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    placeholder = {
                        Text(
                            "••••••••",
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray,letterSpacing = 4.sp,fontFamily = FontFamily(Font(R.font.poppins_regular)))
                        )
                    },
                    shape = RoundedCornerShape(25.dp),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFecf8f4),
                        focusedBorderColor = Color(0xFFecf8f4),
                        unfocusedContainerColor = Color(0xFFecf8f4),
                        focusedContainerColor = Color(0xFFecf8f4)
                    ),
                    trailingIcon = {
                        IconButton(onClick = {confirmPasswordVisible = !confirmPasswordVisible}){
                            Icon(
                                painter = if (confirmPasswordVisible) eyeIcon else eyeIconOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF0E3E3E)
                            )
                        }
                    }
                )

                if (passwordMatchError != null) {
                    Text(
                        text = passwordMatchError!!,
                        style = TextStyle(fontSize = 12.sp, color = Color.Red)
                    )
                }

                // Sign Up Button
                Button(
                    onClick = {
                        if (password != confirmPassword) {
                            passwordMatchError = "Passwords do not match"
                        } else {
                            passwordMatchError = null
                            // TODO: Handle actual sign-up logic here
                            viewModel.onSignupEvent(SignupEvent.SignUpClicked)
                            Log.d("SignUpScreen", "Account created")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20c997)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        style = TextStyle(color = Color(0xFF0E3E3E), fontSize = 16.sp,fontFamily = FontFamily(Font(R.font.poppins_regular)), fontWeight = FontWeight.ExtraBold)
                    )
                }
            }
        }
    }
}

