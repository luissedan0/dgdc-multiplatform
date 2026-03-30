package com.luissedan0.demetergarterdistanceclub

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform