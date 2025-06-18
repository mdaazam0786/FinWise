package com.example.finwise.ui.auth.onBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finwise.R // Replace with your actual package name

@Composable
fun OnboardingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF20c997)), // Teal background
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f), // Teal section takes 40%
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center  //Center vertically the Text
        ) {
            Text(
                text = "Welcome To\nExpense Manager",
                style = TextStyle(
                    color = Color(0xff0E3E3E),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontWeight = FontWeight(800)
            )
        }

        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFf0faf5)),// light background
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_1), // Replace with your image
                    contentDescription = "Hand with Coins",
                    modifier = Modifier
                        .size(300.dp),
                    contentScale = ContentScale.Fit
                )

                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)

                ) {
                    Text(
                        text = "Next",
                        style = TextStyle(
                            color = Color(0xFF0e493d),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        )
                    )
                }


                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF20c997), shape = RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}

