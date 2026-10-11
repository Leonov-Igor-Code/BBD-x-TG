<div align="center">
  
# ByeByeDPI × TG

**Обход блокировок сайтов и прокси для Telegram — в одном приложении**

<br>

<a href="https://github.com/Leonov-Igor-Code/ByeByeDPI-x-tg/releases/latest">
  <img src="https://img.shields.io/github/v/release/Leonov-Igor-Code/ByeByeDPI-x-tg?style=for-the-badge&logo=github&label=Release&color=0aa1dd" alt="Release">
</a>
<img src="https://img.shields.io/badge/Android-API_21%2B_target_34-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android SDK">
<img src="https://img.shields.io/badge/Rust-Edition_2021-000000?style=for-the-badge&logo=rust&logoColor=white" alt="Rust Version">
<img src="https://img.shields.io/badge/Kotlin-2.2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
<a href="https://github.com/Leonov-Igor-Code/ByeByeDPI-x-tg/stargazers">
  <img src="https://img.shields.io/github/stars/Leonov-Igor-Code/ByeByeDPI-x-tg?style=for-the-badge&logo=github&color=ffca28&labelColor=24292e" alt="Stars">
</a>

</div>
<br>

**ByeByeDPI × TG** — это два инструмента под одной крышей: **обход блокировок сайтов** на ядре `byedpi` и локальный **MTProto-прокси для Telegram**, который перенаправляет трафик мессенджера через защищённые CloudFlare WebSocket-соединения. YouTube открывается, Telegram оживает — нажатием одной кнопки каждое.

<!-- Скриншот главного экрана: сделай скрин приложения и вставь сюда
<img width="..." src="..." />
-->

---

## Возможности

- **Отдельный процесс движка:** ядро обхода работает само по себе — сбои в сети не роняют интерфейс.
- **Фоновый подбор стратегий:** перебор вариантов обхода идёт в фоне и показывает прогресс в уведомлении.
- **Диагностика и инструменты:** экран диагностики сети, встроенный файловый менеджер и импорт/экспорт настроек по категориям.
- **Глубокая интеграция с Android:** Private DNS, Always-On VPN и поддержка 16 KB-страниц памяти (Android 15+).
- **Два переключателя вместо десяти настроек:** сайты и Telegram управляются отдельно, статус обоих виден на главном экране.
- **Интеграция с Telegram:** кнопка **«Применить в Telegram»** передаёт прокси в совместимые клиенты через `tg://proxy` (официальный, AyuGram, Plus Messenger, NekoGram и другие).
- **Проверка стратегий:** приложение само перебирает варианты обхода и показывает рабочие — как на реальных телефонах, так и «из коробки».
- **Диагностика TG WS одной кнопкой:** сервис, порт, нативное ядро и доступность серверов проверяются сами, с человеческим вердиктом.
- **FakeTLS для Telegram:** необязательный домен-маскировка превращает MTProto-трафик в «обычный TLS» (секрет `ee`) — глубже прячет прокси от DPI.
- **Порт 80 для CloudFlare:** переключатель для мобильных сетей, где порт `443` заблокирован оператором.
- **Свои Cloudflare Workers:** можно указать список воркер-доменов вместо автоматического подбора.
- **Лог-вьюер:** живые логи обоих движков с фильтрами — видно, что происходит с подключением прямо сейчас.
- **Тёмная и светлая темы,** автозапуск при включении телефона и защита от выгрузки из памяти.

---

## Как это работает

```text
Telegram → 127.0.0.1:1082 (MTProto) → движок на Rust → WSS (через CloudFlare или напрямую) → Telegram DC
```

1. Приложение поднимает локальный MTProto-прокси средствами нативного движка на языке **Rust** (порт по умолчанию — `1082`, секрет генерируется сам).
2. Извлекает из пакета номер датацентра и устанавливает защищённое WebSocket-соединение с нужным DC, при необходимости проксируя через CloudFlare.
3. Держит пул соединений и переподключается при обрывах — чтобы в реальных сетях не отваливалось.
4. Дополнительно: **FakeTLS** (маскировка MTProto под TLS, секрет `ee`+hex-домен), переключение **порта CF 80/443** для мобильных операторов и обработка фрагментированных WebSocket-сообщений — всё синхронизировано с апстримом [tg-ws-proxy-android](https://github.com/amurcanov/tg-ws-proxy-android).

Сайтами в это же время занимается ядро [byedpi](https://github.com/hufrea/byedpi): desync-методы fake/split/oob и списки доменов настраиваются прямо в приложении.

---

## Технологии и версии

| Компонент | Версия |
|---|---|
| Приложение | `1.8.2-tg.2` (versionCode 1822) |
| Kotlin | 2.2.0 |
| Android Gradle Plugin | 8.11.0 |
| Gradle | 8.14.3 |
| JDK | 17 |
| Android SDK | compileSdk 36 · targetSdk 34 · minSdk 21 |
| NDK / CMake | 27.0.12077973 / 3.22.1 |
| Rust-ядро `tgwsproxy` | 1.0.0 (edition 2021, синхронизация с апстримом 2026-10) |
| [byedpi](https://github.com/hufrea/byedpi) | git-сабмодуль |
| [hev-socks5-tunnel](https://github.com/heiher/hev-socks5-tunnel) | git-сабмодуль |

Rust-движок собирается отдельно и поставляется готовыми `.so` — для сборки APK он не нужен.

---

## Быстрый старт

1. Скачайте актуальный APK со **[страницы релизов](https://github.com/Leonov-Igor-Code/ByeByeDPI-x-tg/releases/latest)**.
2. Установите приложение (Android попросит разрешить установку — это нормально, приложение мимо Google Play).
3. Нажмите большую кнопку ⏻ — заработают сайты.
4. Для Telegram включите зелёный переключатель **TG WS**.
5. Нажмите **«Применить в Telegram»** и подтвердите прокси в мессенджере.
6. Не завелось? Меню теста → **«Проверить TG WS»** — приложение само покажет, где затык.

---

* **Краши и проблемы:** если приложение вылетает или не ставится — сохраните логи (меню ⋮ → «Логи TG WS») и приложите их к issue.

> [!NOTE]
> ### Отчёты об ошибках
> Приложение адаптировано под мобильные сети, однако фоновой работе могут мешать агрессивные оптимизации батареи — разрешите приложению их игнорировать при первом запуске.
>
> Мелкие ошибки в логах при нормально работающем прокси можно игнорировать. Перед созданием issue загляните в «Проверку стратегий» и «Проверить TG WS» — часто ответ уже там.

---

<details>
<summary>🔨 <b>Сборка из исходников</b></summary>

<br>

Каждый пуш собирается GitHub Actions ([вкладка Actions](https://github.com/Leonov-Igor-Code/ByeByeDPI-x-tg/actions)), пуш тега `v*` создаёт релиз, коммит с `[skip ci]` сборку пропускает.

Локально:

```bash
git clone https://github.com/Leonov-Igor-Code/ByeByeDPI-x-tg.git
cd ByeByeDPI-x-tg
git submodule update --init --recursive
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/
```

> На Windows без прав на симлинки git раскладывает ссылки в `hev-socks5-tunnel` текстовыми заглушками — включите `core.symlinks` или собирайте в WSL.

Нужны JDK 17 и Android SDK (NDK скачается сам). Собирать Rust **не нужно** — готовые `.so` движка уже лежат в `app/src/main/tglibs/`.

Правки Rust-ядра пересобираются отдельно (Windows, нужны [Rust](https://rustup.rs) и NDK):

```bat
third-party\tg-ws-proxy-rs\build_so.bat
```

Скрипт сам находит Android SDK (переменные окружения, `local.properties` или стандартный путь), собирает оба ABI и кладёт свежие `.so` в `app/src/main/tglibs/`.

</details>

<details>
<summary>💙 <b>Цепочка форков и благодарности</b></summary>

<br>

**Сайты:** [hufrea/byedpi](https://github.com/hufrea/byedpi) (ядро) → [romanvht/ByeByeDPI](https://github.com/romanvht/ByeByeDPI) (Android-обёртка) → **этот форк**

**Telegram:** идея — [Flowseal/tg-ws-proxy](https://github.com/Flowseal/tg-ws-proxy), Android-версия с Rust-движком — [amurcanov/tg-ws-proxy-android](https://github.com/amurcanov/tg-ws-proxy-android) → интегрировано в этот форк

Спасибо авторам оригинальных проектов 💙

</details>

## Лицензия

Этот форк распространяется под лицензией **GPLv3** (см. [LICENSE](LICENSE)).

- **ByeByeDPI для Android** и его разработчик [romanvht](https://github.com/romanvht) — спасибо за основу приложения 💙
- Ядро [byedpi](https://github.com/hufrea/byedpi) от [hufrea](https://github.com/hufrea)
- Оригинальный `tg-ws-proxy` от [Flowseal](https://github.com/Flowseal) доступен под **MIT**
- Android-порт с Rust-движком — [amurcanov/tg-ws-proxy-android](https://github.com/amurcanov/tg-ws-proxy-android)

---

<div align="center">

Помогло? Кинь ⭐ репозиторию и/или [поддержи автора](https://pay.cloudtips.ru/p/ae0eac6d) ❤️

</div>
