// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    kotlin("plugin.serialization") version "1.9.23" apply true
    id("com.android.library") version "8.1.2" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}