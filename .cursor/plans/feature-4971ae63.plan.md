<!-- 4971ae63-7baa-4d5b-b355-d51d70d4bd5c df338f03-18a6-40f5-9f78-e74f9784622a -->
# Рефакторинг старых Feature-ов на контракт, модель и рендерер

## Структура

Для каждого старого Feature-а создаются:

1. **Контракт** (`transmission_contracts/`) - центральный элемент, регистрирует операции с моделью (как `ModelEditing`)
2. **Модель** (`models/`) - хранит данные Feature-а
3. **Рендерер** (`ui/swing/features/`) - отображает UI (методы `paint` и `fillTab` переносятся сюда)

## Feature-ы для рефакторинга

1. **Board** - таблица с объектами
2. **Calendar** - календарь событий
3. **Report** - отчёты с визуализациями
4. **ItemList** - плоский список объектов
5. **EditableList** - список редактируемых объектов
6. **DatedList** - список с датами
7. **MappedList** - список с маппингом
8. **DisposableDocument** - временный документ

## План действий

### 1. Создание контрактов

Для каждого Feature-а создать контракт в `defaults/features/transmission_contracts/`:

- Регистрировать операции через `Supplier`/`Consumer` (как в `ModelEditing`)
- Метод `getFeatureName()` возвращает имя Feature-а
- Реализовать `getImplementedInfo()` если нужно

### 2. Создание моделей

Для каждого Feature-а создать модель в `defaults/features/models/`:

- Перенести поля данных из старого Feature-а
- Реализовать `FeatureModel<Contract>`
- Метод `getTransmissionContract()` возвращает контракт

### 3. Создание рендереров

Для каждого Feature-а создать/обновить рендерер в `ui/swing/features/`:

- Перенести методы `paint()` и `fillTab()` из старого Feature-а
- Реализовать `FeatureRenderer<Contract>`
- Методы `getTransmissionContract()`, `render()`, `renderPreview()`

### 4. Обновление SwingWorkFrameRenderer

Обновить `SwingWorkFrameRenderer.WorkTabButton`:

- Использовать `FeatureRenderer` вместо прямого вызова `paint()`/`fillTab()`
- Получать рендерер через `Feature.getRenderer()`

### 5. Удаление старых классов

После миграции пока что не удалять старые классы Feature-ов из `ui/swing/features/` .

## Особенности по Feature-ам

- **Board/ItemList**: данные в `ArrayList<T> objects`, фильтры, сортировка
- **Calendar**: данные в `HashMap<LocalDate, List<T>> events`
- **Report**: данные в `ArrayList<Supplier<JComponent>> dataRenderers`
- **EditableList**: использует `EditableGroup<T>`
- **DatedList**: данные в `HashMap<T, Dater<T>> objects`
- **MappedList**: данные в `HashMap<T, V> objects`
- **DisposableDocument**: временный документ, не требует модели

## Файлы для изменения

- `bpa4j/src/main/java/com/bpa4j/defaults/features/transmission_contracts/` - новые контракты
- `bpa4j/src/main/java/com/bpa4j/defaults/features/models/` - новые модели
- `bpa4j/src/main/java/com/bpa4j/ui/swing/features/` - обновление рендереров

**Примечание**: `SwingWorkFrameRenderer` не изменяется, пользователь сам закончит там всё.

Класс `ModelEditing` и его рендерер с моделью использовать как пример. Рендереры сделать для `SwingRenderingManager` а.

### To-dos

- [ ] Создать контракты для каждого из классов: найти в старой реализации места, где меняется предполагаемая модель модель, и создать соответствующие поля операций, которые зарегистрирует модель (с окончанием на "op" (от "operation") и типами Supplier, Consumer и т. п. ), и методы, которые сможет использовать рендерер для изменения модели через контракт.
- [ ] Для каждого класса создать модель, открывающую методы для изменения данных, и заставить эту модель зарегистрировать