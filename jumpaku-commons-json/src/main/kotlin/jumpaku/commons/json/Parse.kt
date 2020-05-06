package jumpaku.commons.json

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import jumpaku.commons.control.Result
import jumpaku.commons.control.result
import java.io.File
import java.net.URL
import java.nio.file.Path

fun String.parseJson(): JsonElement = JsonParser().parse(this)

fun File.tryParseJson(): Result<JsonElement> = result { readText().parseJson() }

fun Path.tryParseJson(): Result<JsonElement> = toFile().tryParseJson()

fun URL.tryParseJson(): Result<JsonElement> = result { readText().parseJson() }
