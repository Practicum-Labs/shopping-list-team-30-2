# 🛒 Shopping List

## 📌 Описание

Android-приложение для создания и управления списком покупок.
Позволяет добавлять, редактировать и удалять товары, а также отмечать купленные.

---

## 🛠️ Стек технологий

| Компонент   | Выбор             |
|-------------|-------------------|
| **UI**      | **Compose + MVI** | 
| **DB**      | **Room**          | 
| **DI**      | **Koin**          | 
| **Network** | **Retrofit**      |

**Выбранная архитектура:** Clean Architecture + MVI

**Тип проекта:** Монолитный

---

## 🚀 Сборка проекта

Проект использует **Gradle Wrapper**, поэтому все команды запускаются через `./gradlew`.

### 🔧 Debug сборка

```bash
./gradlew assembleDebug
```

---

### 🚀 Release сборка

```bash
./gradlew assembleRelease
```

> ⚠️ Для release-сборки может потребоваться настройка подписи (keystore)

---

### 🏗️ Полная сборка проекта

```bash
./gradlew build
```

### 🔍 Статический анализ (Detekt)

```bash
./gradlew detekt
```

Отчёты находятся в:

```
build/reports/detekt/
```

---

## 📁 Структура проекта (схематично)

```
📦 ShopperApp/                                         ← корень проекта
├── 📄 build.gradle.kts                                ← корневой (project)
├── 📄 settings.gradle.kts                             ← только один модуль app
│
└── 📱 app/                                            ← модуль проекта
    ├── 📄 build.gradle.kts                            ← зависимости всего приложения
    │
    └── 📂 src/main/java/{package}/
        │
        ├── 🚀 {App}Application.kt                     ← инициализация Koin
        │
        ├── 📂 di/                                     ← Koin-модули
        │   ├── AppModule.kt                           ← общие зависимости
        │   ├── RepositoryModule.kt                    ← репозитории
        │   ├── UseCaseModule.kt                       ← use cases
        │   └── ViewModelModule.kt                     ← viewModel'и
        │
        ├── 📂 core/                                   ← общий код
        │   ├── 📂 base/
        │   │   └── BaseViewModel.kt
        │   ├── 📂 extensions/
        │   │   └── ContextExtensions.kt
        │   └── 📂 ui/
        │       └── 📂 theme/
        │           ├── Theme.kt
        │           ├── Color.kt
        │           └── Type.kt
        │
        ├── 📂 data/                                   ← слой данных
        │   ├── 📂 local/
        │   │   ├── 📂 dao/
        │   │   │   └── {Feature1}Dao.kt
        │   │   └── 📂 entity/
        │   │       └── {Feature1}Entity.kt
        │   ├── 📂 remote/
        │   │   ├── 📂 api/
        │   │   │   └── {Feature1}Api.kt
        │   │   └── 📂 dto/
        │   │       └── {Feature1}Dto.kt
        │   ├── 📂 repository/
        │   │   └── {Feature1}RepositoryImpl.kt
        │   └── 📂 mapper/
        │       └── {Feature1}Mapper.kt
        │
        ├── 📂 domain/                                 ← бизнес-логика
        │   ├── 📂 model/
        │   │   └── {Feature1}Model.kt
        │   ├── 📂 repository/
        │   │   └── {Feature1}Repository.kt            ← интерфейс
        │   └── 📂 usecase/
        │       └── Get{Feature1}UseCase.kt
        │
        └── 📂 feature/                                ← UI + ViewModel (MVI)
            ├── 📂 {feature1}/
            │   ├── {Feature1}State.kt
            │   ├── {Feature1}Intent.kt
            │   ├── {Feature1}SideEffect.kt
            │   ├── {Feature1}ViewModel.kt
            │   ├── {Feature1}Screen.kt
            │   ├── {Feature1}Preview.kt
            │   └── 📂 components/
            │       ├── MainContent.kt
            │       ├── BottomBar.kt
            │       └── ListItem.kt
            │
            └── 📂 {feature2}/
                ├── {Feature2}State.kt
                ├── {Feature2}Intent.kt
                ├── {Feature2}SideEffect.kt
                ├── {Feature2}ViewModel.kt
                ├── {Feature2}Screen.kt
                ├── {Feature2}Preview.kt
                └── 📂 components/
                    ├── Header.kt
                    └── CardItem.kt
```

---

## ⚙️ Полезные команды

Очистка проекта:

```bash
./gradlew clean
```

# Архив  

## Выбор стека

**Итоговый:**

| Компонент   | Выбор             |
|-------------|-------------------|
| **UI**      | **Compose + MVI** | 
| **DB**      | **Room**          | 
| **DI**      | **Koin**          | 
| **Network** | **Retrofit**      |

**Рассуждение:**

| Компонент   | Выбор          | Баллы | Почему                                                                                              |
|-------------|----------------|-------|-----------------------------------------------------------------------------------------------------|
| **UI**      | **XML + MVVM** | 0     | Compose крут, но для ТЗ с кучей требований (свайпы, drag&drop, планшеты, ориентация) XML надёжнее.  |
| **DB**      | **Room**       | 0     | Проще всего. SQLite вручную - не вижу смысла. SQDelight оверкилл.                                   |
| **DI**      | **Koin**       | 0     | Dagger даёт +3 балла, но вспоминать/учить его сейчас - потеря времени. Koin хватит за глаза.        |
| **Network** | **Retrofit**   | 0     | Стандарт. Ktor +2 балла, но для чего тут сеть? В ТЗ про сеть ничего нет. Возможно задел на будущее. |

## Решение на сейчас

Исходя из полученных на вход вводных, и проведенного анализа задачи, предлагается:

- для **UI** выбрать **Compose + MVI**, почему?

1. MVI идеально ложится на ТЗ
    - Сортировка, фильтрация, drag&drop - всё через единый State поток
    - Легко отлаживать и тестировать
    - Нет проблем с жизненным циклом (ориентация экрана решается через rememberSaveable)

2. Планшеты в Compose - проще чем XML
    - Одна верстка на оба размера

3. Свайпы и drag&drop - есть готовые решения
    - Свайп: swipeToDismiss модификатор
    - Drag&drop: draggable + dropTarget (появились официальные API)

4. Темная тема из коробки
    - Минусы (и почему XML может быть безопаснее)

Минусы тоже присутствуют, в основном все они относятся к заданиям со звездочками, поэтому
первоначальная цель, реализовываем первоначальный функционал, обязательный по ТЗ.

Все остальное оставляем из разряда как проще. Остальные дополнительные фичи и требования
реализовываем, если на это есть ресурсы в виде времени.
