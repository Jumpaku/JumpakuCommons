package jumpaku.commons.test.json

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import jumpaku.commons.json.*
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class JsonMapKtTest {

    data class Data(val value: String): ToJson {

        override fun toJson(): JsonElement = jsonObject("value" to value.toJson())

        companion object {

            fun fromJson(json: JsonElement): Data = Data(json["value"].string)
        }
    }

    @Test
    fun testMapJson() {
        println("MapJson")
        val str2int = mapOf("A" to 1, "B" to 2, "C" to 3)
        val ssi = jsonMap(str2int.map { (k, v) -> k.toJson() to v.toJson() }.toMap()).toString()
        val dsi = ssi.parseJson().orThrow().map.map { (k, v) -> Pair(k.string, v.int) }.toMap()
        assertThat(dsi.size, `is`(3))
        assertThat(dsi["A"]!!, `is`(str2int["A"]!!))
        assertThat(dsi["B"]!!, `is`(str2int["B"]!!))
        assertThat(dsi["C"]!!, `is`(str2int["C"]!!))

        val str2data = mapOf("A" to Data("a"), "B" to Data("b"), "C" to Data("c"))
        val ssp = jsonMap(str2data.map { (k, v) -> k.toJson() to v.toJson() }.toMap()).toString()
        val dsp = ssp.parseJson().orThrow().map.map { (k, v) -> k.string to Data.fromJson(v) }.toMap()
        assertThat(dsp.size, `is`(3))
        assertThat(dsp["A"]!!, `is`(str2data["A"]!!))
        assertThat(dsp["B"]!!, `is`(str2data["B"]!!))
        assertThat(dsp["C"]!!, `is`(str2data["C"]!!))
    }
}