package dev.bltucker.echojournal.common

enum class Mood {
    STRESSED,
    SAD,
    NEUTRAL,
    PEACEFUL,
    EXCITED;

    companion object {
        fun fromString(value: String): Mood = valueOf(value.uppercase())
    }
}