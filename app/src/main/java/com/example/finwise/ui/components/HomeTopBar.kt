package com.example.finwise.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finwise.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    title : String
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = TextStyle(
                    color = Color(0xff052224),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontSize = 24.sp
                )
            )
        },
        actions = {
            IconButton(
                onClick = { /* TODO: Handle settings click */ },
                colors = IconButtonColors(
                    containerColor = Color(0xffDFF7E2),
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xffDFF7E2),
                    disabledContentColor = Color.Black
                ),
                modifier = Modifier.size(60.dp).padding(16.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.img_5), contentDescription = null, modifier = Modifier.size(20.dp))
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF20c997), titleContentColor = Color.White)
    )

}