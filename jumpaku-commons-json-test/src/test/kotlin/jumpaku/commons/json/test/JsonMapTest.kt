package jumpaku.commons.json.test

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import jumpaku.commons.json.*
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class JsonMapKtTest {

    data class Data(val value: String)

    object DataJson {
        fun toJson(data: Data): JsonElement = jsonObject("value" to data.value.toJson())
        fun fromJson(json: JsonElement): Data = Data(json["value"].string)
    }

    @Test
    fun testJsonMap() {
        println("JsonMap")
        val str2int = mapOf("A" to 1, "B" to 2, "C" to 3)
        val ssi = jsonMap(str2int.map { (k, v) -> k.toJson() to v.toJson() }.toMap()).toString()
        val dsi = ssi.parseJson().map.map { (k, v) -> Pair(k.string, v.int) }.toMap()
        assertThat(dsi.size, `is`(3))
        assertThat(dsi["A"]!!, `is`(str2int["A"]!!))
        assertThat(dsi["B"]!!, `is`(str2int["B"]!!))
        assertThat(dsi["C"]!!, `is`(str2int["C"]!!))

        val str2data = mapOf("A" to Data("a"), "B" to Data("b"), "C" to Data("c"))
        val ssp = jsonMap(str2data.map { (k, v) -> k.toJson() to DataJson.toJson(v) }.toMap()).toString()
        val dsp = ssp.parseJson().map.map { (k, v) -> k.string to DataJson.fromJson(v) }.toMap()
        assertThat(dsp.size, `is`(3))
        assertThat(dsp["A"]!!, `is`(str2data["A"]!!))
        assertThat(dsp["B"]!!, `is`(str2data["B"]!!))
        assertThat(dsp["C"]!!, `is`(str2data["C"]!!))
    }
}