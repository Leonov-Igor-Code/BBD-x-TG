package io.github.romanvht.byedpi.utility

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StrategyVariantsUtilsTest {

    @Test
    fun `variants are valid for parse_offset rules`() {
        listOf(StrategyVariantsUtils.PRESET_MEDIUM, StrategyVariantsUtils.PRESET_FULL).forEach { preset ->
            StrategyVariantsUtils.generate("google.com", preset).forEach { command ->
                Regex("\\+([a-z]+)").findAll(command).forEach { match ->
                    val flags = match.groupValues[1]
                    assertTrue("некорректный первый символ +$flags в $command", flags[0] in "shn")
                    if (flags.length > 1) {
                        assertTrue("некорректный второй символ +$flags в $command", flags[1] in "emrs")
                    }
                    assertTrue("лишние символы +$flags в $command", flags.length <= 2)
                }
            }
        }
    }

    @Test
    fun `every variant is a command line with the adapter flag`() {
        listOf(StrategyVariantsUtils.PRESET_MEDIUM, StrategyVariantsUtils.PRESET_FULL).forEach { preset ->
            StrategyVariantsUtils.generate("google.com", preset).forEach { command ->
                assertTrue("нет -a1 в $command", command.endsWith("-a1"))
                assertTrue("команда не начинается с флага: $command", command.startsWith("-"))
            }
        }
    }

    @Test
    fun `medium preset size in range`() {
        val size = StrategyVariantsUtils.generate("google.com", StrategyVariantsUtils.PRESET_MEDIUM).size
        assertTrue("умеренный пресет вне диапазона 60–120: $size", size in 60..120)
    }

    @Test
    fun `full preset size in range`() {
        val size = StrategyVariantsUtils.generate("google.com", StrategyVariantsUtils.PRESET_FULL).size
        assertTrue("полный пресет вне диапазона 150–350: $size", size in 150..350)
    }

    @Test
    fun `variants are unique`() {
        listOf(StrategyVariantsUtils.PRESET_MEDIUM, StrategyVariantsUtils.PRESET_FULL).forEach { preset ->
            val variants = StrategyVariantsUtils.generate("google.com", preset)
            assertEquals("дубликаты в пресете $preset", variants.size, variants.distinct().size)
        }
    }

    @Test
    fun `generation is deterministic`() {
        listOf(StrategyVariantsUtils.PRESET_MEDIUM, StrategyVariantsUtils.PRESET_FULL).forEach { preset ->
            assertEquals(
                StrategyVariantsUtils.generate("google.com", preset),
                StrategyVariantsUtils.generate("google.com", preset),
            )
        }
    }

    @Test
    fun `sni placeholder is substituted`() {
        listOf(StrategyVariantsUtils.PRESET_MEDIUM, StrategyVariantsUtils.PRESET_FULL).forEach { preset ->
            StrategyVariantsUtils.generate("example.org", preset).forEach { command ->
                assertFalse("остался { в $command", "{" in command)
                assertFalse("остался } в $command", "}" in command)
            }
        }
        assertTrue(
            "sni не подставлен в кавычках",
            StrategyVariantsUtils.generate("example.org", StrategyVariantsUtils.PRESET_MEDIUM).any { "\"example.org\"" in it },
        )
    }
}
