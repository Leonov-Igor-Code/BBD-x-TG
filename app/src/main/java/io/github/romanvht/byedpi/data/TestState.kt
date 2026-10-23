package io.github.romanvht.byedpi.data

data class TestState(
    val runId: Long = 0,
    val isLoaded: Boolean = false,
    val isRunning: Boolean = false,
    val isStopping: Boolean = false,
    val currentStrategy: Int = 0,
    val strategies: List<StrategyResult> = emptyList(),
    val failed: Boolean = false,
    val wasInterrupted: Boolean = false,
    // Фоновая диагностика TG WS (форк): не блокирует тест стратегий
    val isTgWsChecking: Boolean = false,
    val tgWsReport: String = "",
    // Снимок активных списков доменов на момент старта прогона / загрузки
    // результатов: для группировки сайтов в карточках и логе. Gson'ом не
    // сериализуется (в proxy_test_results.json хранятся только StrategyResult).
    val domainGroups: List<DomainList> = emptyList(),
)
