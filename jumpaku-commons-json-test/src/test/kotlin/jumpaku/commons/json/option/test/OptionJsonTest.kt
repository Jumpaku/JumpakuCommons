package jumpaku.commons.json.option.test

import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.toJson
import jumpaku.commons.control.*
import jumpaku.commons.option.json.OptionJson
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Test


class OptionJsonTest {

    val some = some(4).map { it.toJson() }
    val none = none<Int>().map { it.toJson() }

    @Test
    fun testOptionJson() {
        println("OptionJson")
        assertThat(OptionJson.fromJson(OptionJson.toJson(some)).map { it.int }.orNull(), `is`(4))
        assertThat(OptionJson.fromJson(OptionJson.toJson(none)).map { it.int }.orNull(), `is`(nullValue()))
    }
}