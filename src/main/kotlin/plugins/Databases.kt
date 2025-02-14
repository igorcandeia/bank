package com.challenge.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases(dbUrl: String, dbUser: String, dbPassword: String) {
    Database.connect(
        url = dbUrl,
        user = dbUser,
        password = dbPassword
    )
}
