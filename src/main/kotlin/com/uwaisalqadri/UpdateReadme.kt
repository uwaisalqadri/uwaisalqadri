package com.uwaisalqadri

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

class UpdateReadmeCommand: CliktCommand() {

    private val outputFile by option("-o", help = "The README.md file to write")
        .file()
        .required()

    override fun run() {
        val readMe = runBlocking { ReadMeUpdater().generateReadme() }
        outputFile.writeText(readMe)
        // TODO why do I need to do this
        exitProcess(0)
    }
}

fun main(argv: Array<String>) {
    UpdateReadmeCommand().main(argv)
}