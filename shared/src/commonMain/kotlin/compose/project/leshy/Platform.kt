package compose.project.leshy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform