pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()


        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        // TradPlus
        mavenCentral()//maven仓库

        flatDir {
            dirs("libs")
        }



        // Pangle
        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle")
        }


        //Mintegral
//Launch GP market application， Android X Version
//If you fail to pull the code using gradle, add the maven warehouse configuration to the project root build.gradle file
        maven {
            url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        }

    }
}





rootProject.name = "demo2"
include(":app")
 