package jumpaku.commons.json

import com.google.gson.JsonElement
import com.google.gson.JsonParser

abstract class JsonConverterBase<T> {

    abstract fun toJson(src: T): JsonElement

    abstract fun fromJson(json: JsonElement): T

    open fun toJsonStr(src: T): String = toJson(src).toString()

    open fun fromJsonStr(str: String): T = fromJson(parser.parse(str))

    companion object {

        val parser: JsonParser = JsonParser()
    }

}