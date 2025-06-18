package com.example.finwise.ui.home.categories.transport

sealed class TransportEvent {
    object OnAccountBalanceButton : TransportEvent()
    object OnExpenseButton : TransportEvent()
    object OnTransportDetailButton : TransportEvent()
    object OnAddExpenseButton : TransportEvent()
}