package jp.seesaa.hacocms.client

import jp.seesaa.hacocms.client.SortQuery.Companion.sq
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class SortQueryTest {
    @ParameterizedTest
    @MethodSource("provideQueryBuilderTestCases")
    fun testSortQueryBuilder(
        input: List<SortQuery>,
        expect: String
    ) {
        assertEquals(expect, input.build())
    }

    companion object {
        @JvmStatic
        fun provideQueryBuilderTestCases(): Stream<Arguments> {
            data class TestCase(
                val input: List<SortQuery>,
                val expect: String
            )

            val cases = listOf(
                TestCase(
                    input = listOf(),
                    expect = ""
                ),
                TestCase(
                    input = listOf("createdAt".sq()),
                    expect = "createdAt"
                ),
                TestCase(
                    input = listOf("-createdAt".sq()),
                    expect = "-createdAt"
                ),
                TestCase(
                    input = listOf("updatedAt".sq().desc()),
                    expect = "-updatedAt"
                ),
                TestCase(
                    input = listOf("-updatedAt".sq().desc()),
                    expect = "-updatedAt"
                ),
                TestCase(
                    input = listOf("-updatedAt".sq().asc()),
                    expect = "updatedAt"
                ),
                TestCase(
                    input = listOf("publishedAt".sq().desc(), "id".sq()),
                    expect = "-publishedAt,id"
                ),
            )

            return cases.stream().map {
                Arguments.of(it.input, it.expect)
            }
        }
    }

}
