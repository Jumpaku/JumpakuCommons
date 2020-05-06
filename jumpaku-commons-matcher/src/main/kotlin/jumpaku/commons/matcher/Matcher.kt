package jumpaku.commons.test

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

fun <A> matcher(message: String, test: (A) -> Boolean): TypeSafeMatcher<A> = object : TypeSafeMatcher<A>() {

    override fun describeTo(description: Description) { description.appendText(message) }

    override fun matchesSafely(actual: A): Boolean = test(actual)
}