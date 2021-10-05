/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("de.Ste3et_C0st.furniture.java-conventions")
}

repositories{
    maven(url = uri("https://maven.enginehub.org/repo/"))
    maven(url = uri( "https://maven.elmakers.com/repository/"))
}

dependencies {
    implementation(project(":Core"))
    compileOnly("com.sk89q:worldedit:5.7-SNAPSHOT")
    compileOnly("com.sk89q:worldguard:5.9.1-SNAPSHOT")
    compileOnly("com.github.TownyAdvanced:Towny:0.97.1.0")
    implementation(files("/lib/PlotSquared.jar"))
}

description = "PlotSquaredV3"
