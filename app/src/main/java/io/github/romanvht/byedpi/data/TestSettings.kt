package io.github.romanvht.byedpi.data

data class TestSettings(
    val sites: List<String>,
    val commands: List<String>,
    val host: String,
    val port: Int,
    val delaySec: Int,
    val requestsCount: Int,
    val requestTimeout: Long,
    val requestLimit: Int,
    // Активные списки доменов — снимок для группировки результатов прогона
    val domainLists: List<DomainList> = emptyList(),
)
