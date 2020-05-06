package jumpaku.commons.option.json

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import jumpaku.commons.control.Option
import jumpaku.commons.control.none
import jumpaku.commons.control.orDefault
import jumpaku.commons.control.some

object OptionJson {
    val serializer = jsonSerializer<Option<JsonElement>> {
        jsonObject("value" to it.src.orDefault(jsonNull))
    }

    val deserializer = jsonDeserializer<Option<JsonElement>> {
        val j = it.json
        when {
            j is JsonObject && j["value"]?.isJsonNull == false -> some(j["value"])
            else -> none()
        }
    }

    val gson = GsonBuilder()
        .registerTypeAdapter(serializer)
        .registerTypeAdapter(deserializer)
        .create()

    fun fromJson(json: JsonElement): Option<JsonElement> = gson.fromJson(json)

    fun toJson(option: Option<JsonElement>): JsonElement = gson.typedToJsonTree(option)
}