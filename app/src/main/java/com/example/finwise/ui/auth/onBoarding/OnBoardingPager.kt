package com.example.finwise.ui.auth.onBoarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(onFinish: () -> Unit,navController: NavController) {
    //Correct way to remember PagerState
    val pagerState = rememberPagerState(initialPage = 0) { 2 } // Two pages

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> OnboardingScreen()
                1 -> OnboardingScreen2(
                    onNavigate = {
                        navController.navigate(it.route)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Page Indicator (Replacement for accompanist's HorizontalPagerIndicator)
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until pagerState.pageCount) {
                val isSelected = i == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.Black else Color.Gray)
                        .clickable { /* Handle indicator click, if needed */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next/Finish Button
        Button(
            onClick = {
                if (pagerState.currentPage < pagerState.pageCount - 1) {
                    // Move to the next page
                    // pagerState.animateScrollToPage(pagerState.currentPage + 1) //PagerState v2 way of doing this
                } else {
                    onFinish() // Navigate when finished (on the last page)
                }
            }
        ) {
            Text(text = if (pagerState.currentPage < pagerState.pageCount - 1) "Next" else "Get Started",
                fontSize = 16.sp)
        }
    }
}



