package com.example.wattify.activities

data class User(val name: String, val email: String, val password: String) {
    constructor() : this("", "", "")
}
