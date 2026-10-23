package io.github.romanvht.byedpi.data

data class StrategyResult(
    val command: String,
    var successCount: Int = 0,
    var totalRequests: Int = 0,
    var currentProgress: Int = 0,
    var isCompleted: Boolean = false,
    val siteResults: MutableList<SiteResult> = mutableListOf(),
    var isExpanded: Boolean = false
) {
    val successPercentage: Int
        get() = if (totalRequests > 0) (successCount * 100) / totalRequests else 0
}

data class SiteResult(
    val site: String,
    val successCount: Int,
    val totalCount: Int
)

/**
 * Группа результатов по доменному списку: заголовок для раскрытой карточки
 * стратегии и копируемого лога. Домен из нескольких списков входит в каждую.
 */
data class SiteGroup(
    val name: String,
    val siteResults: List<SiteResult>,
    val successCount: Int,
    val totalCount: Int
) {
    val successPercentage: Int
        get() = if (totalCount > 0) (successCount * 100) / totalCount else 0
}

/**
 * Строит группы результатов по снимку активных списков (единая точка истины
 * для UI и лога). Пустые группы пропускаются; сайты вне всех списков
 * образуют хвост с именем [noListName]. Пустой снимок — пустой результат
 * (обратная совместимость: плоский список).
 */
fun buildSiteGroups(
    siteResults: List<SiteResult>,
    groups: List<DomainList>,
    noListName: String
): List<SiteGroup> {
    if (groups.isEmpty()) return emptyList()
    val bySite = siteResults.associateBy { it.site }
    val result = mutableListOf<SiteGroup>()
    val matched = mutableSetOf<String>()
    for (group in groups) {
        val groupResults = group.domains.mapNotNull { bySite[it] }
        if (groupResults.isEmpty()) continue
        matched.addAll(groupResults.map { it.site })
        result.add(
            SiteGroup(
                name = group.name,
                siteResults = groupResults,
                successCount = groupResults.sumOf { it.successCount },
                totalCount = groupResults.sumOf { it.totalCount }
            )
        )
    }
    val noList = siteResults.filter { it.site !in matched }
    if (noList.isNotEmpty()) {
        result.add(
            SiteGroup(
                name = noListName,
                siteResults = noList,
                successCount = noList.sumOf { it.successCount },
                totalCount = noList.sumOf { it.totalCount }
            )
        )
    }
    return result
}
