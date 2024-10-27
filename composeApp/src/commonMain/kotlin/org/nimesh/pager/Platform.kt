package org.nimesh.pager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform