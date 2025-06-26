package com.example.finwise.ui.home.categories.savings.car

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.repository.TransactionRepository
import com.example.finwise.ui.home.categories.savings.travel.TravelSavingsCombinedData
import com.example.finwise.ui.home.categories.savings.travel.TravelSavingsUiState
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CarViewModel @Inject constructor(
    private val repository : TransactionRepository
): ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _carSavingsState = MutableStateFlow(CarSavingsUiState())
    val carSavingsState: StateFlow<CarSavingsUiState> = _carSavingsState.asStateFlow()

    private val CAR_CATEGORY_NAME = "CAR" // Use the actual string name from your enum

    init {
        viewModelScope.launch {
            // Combine flows from Savings (deposits) and Goal (target)
            val savingsFlow = repository.getAllSavings().map { allSavings ->
                allSavings.filter { it.category == CAR_CATEGORY_NAME }
            }
            val goalFlow = repository.getGoalByCategory(CAR_CATEGORY_NAME)

            combine(savingsFlow, goalFlow) { travelDeposits, travelGoal ->
                CarSavingsCombinedData(deposits = travelDeposits, goal = travelGoal)
            }.collect { data ->
                val goalAmount = data.goal?.targetAmount ?: 0.0
                val totalAmountSaved = data.deposits.sumOf { it.amount }
                val progress = if (goalAmount > 0) {
                    (totalAmountSaved / goalAmount * 100).toFloat().coerceIn(0f, 100f)
                } else 0f

                _carSavingsState.value = CarSavingsUiState(
                    carGoal = data.goal,
                    totalAmountSaved = totalAmountSaved,
                    progressPercentage = progress,
                    carDeposits = data.deposits.sortedByDescending { it.date } // Sort deposits by date
                )
                Log.d("CarSavingsVM", "UI State updated: Goal=$goalAmount, Saved=$totalAmountSaved, Progress=$progress")
            }
        }
    }

    // Function to set or update the car goal
    fun setCarGoal(amount: Double) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val existingGoal = repository.getGoalByCategory(CAR_CATEGORY_NAME).first()


                    if (existingGoal != null) {
                        // Update existing goal
                        repository.updateGoal(existingGoal.copy(targetAmount = amount, dateSet = System.currentTimeMillis()))
                        Log.d("CarSavingsVM", "Updated existing travel goal: ${existingGoal.id} with amount $amount")
                    } else {
                        // Insert new goal
                        val newGoal = Goal(
                            category = CAR_CATEGORY_NAME,
                            targetAmount = amount,
                            dateSet = System.currentTimeMillis()
                        )
                        repository.insertGoal(newGoal)
                        Log.d("CarSavingsVM", "New car goal set: $newGoal")
                    }
                }
                sendUiEvent(UiEvent.ShowSnackbar("Car goal set successfully!"))
            } catch (e: Exception) {
                Log.e("CarSavingsVM", "Error setting car goal: ${e.message}", e)
                sendUiEvent(UiEvent.ShowSnackbar("Error setting goal: ${e.localizedMessage}"))
            }
        }
    }



    fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}

