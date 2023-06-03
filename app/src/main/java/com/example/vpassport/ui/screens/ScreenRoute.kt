package com.example.vpassport.ui.screens

sealed class Screen(val route:String) {
    object Home: Screen(route = "home")
    object Detail: Screen(route = "register")

}