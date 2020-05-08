package jumpaku.commons.json

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import jumpaku.commons.control.Result
import jumpaku.commons.control.result
import java.io.File

fun String.parseJson(): JsonElement = JsonParser().parse(this)

fun File.tryParseJson(): Result<JsonElement> = result { readText().parseJson() }
