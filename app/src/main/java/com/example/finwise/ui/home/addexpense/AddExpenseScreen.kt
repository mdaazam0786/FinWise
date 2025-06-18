package com.example.finwise.ui.home.addexpense

import BottomNavigationBar
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.ui.components.CentreTopBar
import com.example.finwise.ui.home.categories.category.CategoryType
import com.example.finwise.util.UiEvent
import com.example.finwise.util.Utils
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.finwise.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExpensesScreen(
    navController: NavHostController,
    addExpenseViewModel: AddExpenseViewModel = hiltViewModel()
) {
    var pickedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd-MM-yyyy")
                .format(pickedDate)
        }
    }
    val dateDialogState = rememberMaterialDialogState()
    val context = LocalContext.current

    val categories = listOf(
        CategoryType.FOOD,
        CategoryType.RENT,
        CategoryType.GROCERIES,
        CategoryType.ENTERTAINMENT,
        CategoryType.SAVINGS,
        CategoryType.TRANSPORT,
        CategoryType.MEDICINE,
        CategoryType.GIFTS,

        )
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Select the category") }

    val title by addExpenseViewModel.title.collectAsStateWithLifecycle()
    val amount by addExpenseViewModel.amount.collectAsStateWithLifecycle()
    val scaffoldState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = true) {
        addExpenseViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> navController.popBackStack()

                is UiEvent.ShowSnackbar -> {
                    scaffoldState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }

                is UiEvent.Navigate -> Unit
            }

        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = scaffoldState) },
        topBar = {
            CentreTopBar(title = "Add Expense", navController = navController)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFf0faf5))
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // White Box Section (Content of the screen)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(Color(0xFFf0faf5)) // Light Background
                        .padding(24.dp)

                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Date",
                                color = colorResource(id = R.color.letters_icons),
                                fontFamily = FontFamily(
                                    Font(R.font.poppins_regular)
                                ),
                                fontWeight = FontWeight.ExtraBold
                            )
                            OutlinedTextField(
                                value = formattedDate,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(25.dp),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        dateDialogState.show()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.DateRange,
                                            contentDescription = null
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFecf8f4),
                                    focusedBorderColor = Color(0xFFecf8f4),
                                    unfocusedContainerColor = Color(0xFFecf8f4),
                                    focusedContainerColor = Color(0xFFecf8f4),
                                    focusedTextColor = colorResource(id = R.color.letters_icons),
                                    unfocusedTextColor = colorResource(id = R.color.letters_icons)
                                )
                            )
                        }
                        MaterialDialog(
                            dialogState = dateDialogState,
                            buttons = {
                                positiveButton(text = "Ok") {
                                    Toast.makeText(
                                        context,
                                        "Clicked ok",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                negativeButton(text = "Cancel")
                            }
                        ) {
                            datepicker(
                                initialDate = LocalDate.now(),
                                title = "Pick a date",
                            ) {
                                pickedDate = it
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Category",
                                color = colorResource(id = R.color.letters_icons),
                                fontFamily = FontFamily(
                                    Font(R.font.poppins_regular)
                                ),fontWeight = FontWeight.ExtraBold
                            )
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Dropdown",
                                            tint = colorResource(id = R.color.main_green)
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(25.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFecf8f4),
                                    focusedBorderColor = Color(0xFFecf8f4),
                                    unfocusedContainerColor = Color(0xFFecf8f4),
                                    focusedContainerColor = Color(0xFFecf8f4),
                                    focusedTextColor = colorResource(id = R.color.letters_icons),
                                    unfocusedTextColor = colorResource(id = R.color.letters_icons)
                                )
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(text = category.name) },
                                        onClick = {
                                            selectedCategory = category.name
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Amount",
                                color = colorResource(id = R.color.letters_icons),
                                fontFamily = FontFamily(
                                    Font(R.font.poppins_regular)
                                ),
                                fontWeight = FontWeight.ExtraBold
                            )
                            OutlinedTextField(
                                value = amount,
                                onValueChange = {
                                    addExpenseViewModel.onAddExpenseEvent(
                                        AddExpenseEvent.OnAmountChange(
                                            it
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(25.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFecf8f4),
                                    focusedBorderColor = Color(0xFFecf8f4),
                                    unfocusedContainerColor = Color(0xFFecf8f4),
                                    focusedContainerColor = Color(0xFFecf8f4),
                                    focusedTextColor = colorResource(id = R.color.letters_icons),
                                    unfocusedTextColor = colorResource(id = R.color.letters_icons)
                                )
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Expense Title",
                                color = colorResource(id = R.color.letters_icons),
                                fontFamily = FontFamily(
                                    Font(R.font.poppins_regular)
                                ),
                                fontWeight = FontWeight.ExtraBold
                            )
                            OutlinedTextField(
                                value = title,
                                onValueChange = {
                                    addExpenseViewModel.onAddExpenseEvent(
                                        AddExpenseEvent.OnTitleChange(
                                            it
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(25.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFecf8f4),
                                    focusedBorderColor = Color(0xFFecf8f4),
                                    unfocusedContainerColor = Color(0xFFecf8f4),
                                    focusedContainerColor = Color(0xFFecf8f4),
                                    focusedTextColor = colorResource(id = R.color.letters_icons),
                                    unfocusedTextColor = colorResource(id = R.color.letters_icons)
                                )
                            )
                        }

                        Button(
                            onClick = {
                                val timeStamp = Utils.getMilliFromDate(formattedDate)

                                addExpenseViewModel.onAddExpenseEvent(
                                    AddExpenseEvent.OnSaveClick(
                                        expense = Expense(
                                            title = title,
                                            amount = amount.toDouble(),
                                            date = timeStamp,
                                            category = selectedCategory,
                                        )
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20c997)),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text(
                                text = "Save",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = colorResource(id = R.color.letters_icons),
                                    fontFamily = FontFamily(
                                        Font(R.font.poppins_regular)
                                    )
                                )
                            )
                        }

                    }
                }
            }
        }
    )
}

