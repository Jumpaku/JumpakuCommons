package jumpaku.commons.option.json

import com.github.salomonbrys.kotson.jsonNull
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import jumpaku.commons.control.Option
import jumpaku.commons.control.none
import jumpaku.commons.control.orDefault
import jumpaku.commons.control.some

object OptionJson {

    fun fromJson(json: JsonElement): Option<JsonElement> = when {
        json is JsonObject && json["value"]?.isJsonNull == false -> some(json["value"])
        else -> none()
    }

    fun toJson(option: Option<JsonElement>): JsonElement =
        jsonObject("value" to option.orDefault(jsonNull))
}