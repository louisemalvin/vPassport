package com.example.vpassport.view.screens

sealed class Screen(val route:String) {
    object Home: Screen(route = "home")
    object Detail: Screen(route = "register")

}