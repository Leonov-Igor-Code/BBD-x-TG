package io.github.romanvht.byedpi.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildSiteGroupsTest {

    private val noListName = "Без списка"

    private fun list(id: String, domains: List<String>) =
        DomainList(id = id, name = id.replaceFirstChar { it.uppercase() }, domains = domains)

    @Test
    fun `empty snapshot returns empty groups`() {
        val siteResults = listOf(SiteResult("youtube.com", 1, 1))

        val groups = buildSiteGroups(siteResults, emptyList(), noListName)

        assertTrue(groups.isEmpty())
    }

    @Test
    fun `sites are grouped by active lists in list order`() {
        val siteResults = listOf(
            SiteResult("youtube.com", 2, 2),
            SiteResult("rutube.ru", 0, 2),
            SiteResult("t.me", 1, 1),
        )
        val lists = listOf(
            list("youtube", listOf("youtube.com", "rutube.ru")),
            list("telegram", listOf("t.me")),
        )

        val groups = buildSiteGroups(siteResults, lists, noListName)

        assertEquals(2, groups.size)
        assertEquals("Youtube", groups[0].name)
        assertEquals(listOf("youtube.com", "rutube.ru"), groups[0].siteResults.map { it.site })
        assertEquals(2, groups[0].successCount)
        assertEquals(4, groups[0].totalCount)
        assertEquals(50, groups[0].successPercentage)
        assertEquals("Telegram", groups[1].name)
        assertEquals(100, groups[1].successPercentage)
    }

    @Test
    fun `domain in several lists is counted in each group`() {
        val siteResults = listOf(SiteResult("discord.com", 1, 2))
        val lists = listOf(
            list("discord", listOf("discord.com")),
            list("social", listOf("discord.com")),
        )

        val groups = buildSiteGroups(siteResults, lists, noListName)

        assertEquals(2, groups.size)
        groups.forEach { group ->
            assertEquals(1, group.siteResults.size)
            assertEquals(1, group.successCount)
            assertEquals(2, group.totalCount)
        }
    }

    @Test
    fun `empty groups are skipped`() {
        val siteResults = listOf(SiteResult("t.me", 1, 1))
        val lists = listOf(
            list("youtube", listOf("youtube.com")),
            list("telegram", listOf("t.me")),
        )

        val groups = buildSiteGroups(siteResults, lists, noListName)

        assertEquals(1, groups.size)
        assertEquals("Telegram", groups[0].name)
    }

    @Test
    fun `sites outside all lists form the tail group`() {
        val siteResults = listOf(
            SiteResult("youtube.com", 1, 1),
            SiteResult("unknown.site", 0, 1),
        )
        val lists = listOf(list("youtube", listOf("youtube.com")))

        val groups = buildSiteGroups(siteResults, lists, noListName)

        assertEquals(2, groups.size)
        assertEquals(noListName, groups[1].name)
        assertEquals(listOf("unknown.site"), groups[1].siteResults.map { it.site })
        assertEquals(0, groups[1].successPercentage)
    }

    @Test
    fun `zero totals do not divide by zero`() {
        val siteResults = listOf(SiteResult("youtube.com", 0, 0))
        val lists = listOf(list("youtube", listOf("youtube.com")))

        val groups = buildSiteGroups(siteResults, lists, noListName)

        assertEquals(1, groups.size)
        assertEquals(0, groups[0].totalCount)
        assertEquals(0, groups[0].successPercentage)
    }
}
