package io.github.romanvht.byedpi.utility

/**
 * Детерминированный генератор параметрических вариаций стратегий для кнопки
 * «Расширенный подбор»: не случайная генерация и не декартово произведение
 * всех флагов (полный перебор = десятки часов и шум), а сетки значений вокруг
 * проверенных семейств desync-механизмов.
 *
 * Формат команд соответствует правилам разбора позиционных флагов ядра
 * (parse_offset): первый символ после `+` — s/h/n, второй — e/m/r/s.
 */
object StrategyVariantsUtils {
    const val PRESET_MEDIUM = 1
    const val PRESET_FULL = 2

    // Сетки позиций split/disorder для умеренного и полного пресетов
    private val SPLIT_MEDIUM = listOf(1, 2, 3, 4, 5, 7, 9, 12, 15, 20, 25, 30, 35)
    private val SPLIT_FULL = (1..35).toList() + listOf(40, 50)
    private val TLSREC_MEDIUM = listOf(1, 2, 3, 5)
    private val TLSREC_FULL = (1..7).toList()
    private val DISORDER_SPLIT_MEDIUM = listOf(3, 6, 9, 12, 20)
    private val DISORDER_SPLIT_FULL = listOf(3, 6, 9, 12, 15, 20, 25, 30)
    private val OOB_MEDIUM = listOf(1, 2, 3, 5)
    private val OOB_FULL = (1..7).toList()
    private val DISOOB_MEDIUM = listOf(1, 3, 5)
    private val DISOOB_FULL = (1..5).toList()
    private val CROSS_FULL = listOf(1, 2, 3, 5, 7)

    /** Детерминированный список вариаций; {sni} подставляется в шаблоны. */
    fun generate(sni: String, preset: Int): List<String> {
        val templates = if (preset == PRESET_FULL) fullTemplates() else mediumTemplates()
        return templates
            .map { it.replace("{sni}", "\"$sni\"") }
            .distinct()
    }

    // ~75 стратегий: 13+13+3+15+9+6+4+5+4+3
    private fun mediumTemplates(): List<String> = buildList {
        SPLIT_MEDIUM.forEach { p ->
            add("-s$p+s -a1")
            add("-d$p+s -a1")
        }
        listOf(1, 2, 3).forEach { p -> add("-s$p+sm -a1") }
        (1..3).forEach { p ->
            listOf(4, 6, 8, 10, 12).forEach { t -> add("-f-$p -t$t -a1") }
        }
        (1..3).forEach { p ->
            listOf(6, 8, 12).forEach { t -> add("-n {sni} -Qr -f-$p -t$t -a1") }
        }
        (1..3).forEach { p ->
            add("-f-$p+sm -t8 -a1")
            add("-f-$p+se -t8 -a1")
        }
        TLSREC_MEDIUM.forEach { p -> add("-s1+s -r$p+s -a1") }
        DISORDER_SPLIT_MEDIUM.forEach { p -> add("-d1 -s$p+s -a1") }
        OOB_MEDIUM.forEach { p -> add("-o$p -a1") }
        DISOOB_MEDIUM.forEach { p -> add("-q$p -a1") }
    }

    // ~196 стратегий: 37+37+5+24+18+18+7+8+7+5+25+5
    private fun fullTemplates(): List<String> = buildList {
        SPLIT_FULL.forEach { p ->
            add("-s$p+s -a1")
            add("-d$p+s -a1")
        }
        listOf(1, 2, 3, 5, 7).forEach { p -> add("-s$p+sm -a1") }
        (1..3).forEach { p ->
            (2..16 step 2).forEach { t -> add("-f-$p -t$t -a1") }
        }
        (1..3).forEach { p ->
            (4..14 step 2).forEach { t -> add("-n {sni} -Qr -f-$p -t$t -a1") }
        }
        listOf(6, 8, 12).forEach { t ->
            (1..3).forEach { p ->
                add("-f-$p+sm -t$t -a1")
                add("-f-$p+se -t$t -a1")
            }
        }
        TLSREC_FULL.forEach { p -> add("-s1+s -r$p+s -a1") }
        DISORDER_SPLIT_FULL.forEach { p -> add("-d1 -s$p+s -a1") }
        OOB_FULL.forEach { p -> add("-o$p -a1") }
        DISOOB_FULL.forEach { p -> add("-q$p -a1") }
        CROSS_FULL.forEach { p1 ->
            CROSS_FULL.forEach { p2 -> add("-s$p1+s -d$p2+s -a1") }
        }
        (1..5).forEach { p -> add("-f-1 -t8 -r$p+s -a1") }
    }
}
