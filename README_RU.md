# BPA4j — библиотека для быстрой разработки приложений автоматизации бизнес‑процессов

BPA4j — это DI‑ориентированная библиотека на Java для стремительной сборки настольных приложений автоматизации: роли/права, редактирование объектов, доски задач, календари, отчёты, списки, модульный редактор и система подсказок маршрутов действий.

- **Статус**: внутренняя библиотека, распространяется "как есть", без гарантий.
- **Javadoc**: см. `target/apidocs/index.html`.
- **Примеры**: см. `bpa4j/src/test/java/com/bpa4j/*` (особенно `FullTester.java`).

### Установка (Maven)
```xml
<dependency>
	<groupId>io.github.vladimirpianykh</groupId>
	<artifactId>bpa4j</artifactId>
	<version>1.0.19</version>
</dependency>
```

# Принципы работы

## Объекты
В библиотеке всё основано на создании подклассов Editable. Editable - это  некоторый объект доступный для редактирования или просмотра пользователем. Таких объектов будет много, и часть их явно попросят создать в ТЗ. В то же время, иногда придётся создавать вспомогательные объекты.  
Такие объекты подлежат использованию в редакторе (`Editor`), где они отображают те поля, что помечены аннотацией `@EditorEntry`.
Для стандартных типов данных предусмотрены редакторы полей (`EditorEntryBase`) по умолчанию, для типов посложнее потребуется указать редактор поля явно. Вот, как это делать:

```
//           отображаемое название,  редактор
@EditorEntry(translation = "Поле 1", editor = MyEditor.class)
MyType field;
```
Ещё раз: если тип стандартный (в т. ч. другой `Editable`), редактор, скорее всего, уже есть. Если что, можно указать вручную.

## Роли, пользователи/подсистемы
Почти всегда в первой же сессии требуется разделить приложение на подсистемы. Обычно без пароля. Понятно, что все функции как-то распределяются по подсистемам. Чтобы их вот так распределить, мы придумали следующие штуки:
- `User` - подсистема или пользователь (используйте `User.register(String login, String pass, Role role)`, чтобы создать пользователя/подсистему с нужной ролью )
- `Role` - роль, объект с информацией о типе подсистемы
- `Feature` - функция, нечто, что появится на панели вкладок у пользователя с нужной ролью
- `Permission` - разрешение, флаг, который используется `Feature`-ами

Некоторые `Feature` уже заготовлены в программе. Например, вкладка, которая открывает список с объектами, которые можно просмотреть в редакторе - `DefaultFeature.MODEL_EDITING`.
Помимо `DefaultFeature` есть так называемые Board-подобные классы, которые можно легко создать и настроить почти под любую задачу. Нужно просто к ним привыкнуть.

## Регистрация
Чтобы пользоваль мог увидеть объекты в редакторе, нужно ещё как-то их туда доставить.

## Редакторы

### Как сделать свой редактор
<Пока не заполнил>
### Редакторы-обёртки
<Пока не заполнил>

# Использование (написано ИИ, 100% найдутся ошибки)

## Быстрый старт
1) Инициализация навигатора и старт приложения
```java
Navigator.init();
ProgramStarter.welcomeMessage = "Приветственное сообщение";    // опционально
ProgramStarter.authRequired = false;   // нужно ли требовать пароль для каждого пользователя, или просто предложить список пользователей (по умолчанию true - то есть требует пароль)
ProgramStarter.runProgram();           // запуск UI
```

2) Пользователь, роли и права (сделать это можно прямо в главном классе до метода `main`)
- Опишите `enum`, реализующий `User.Role`, и перечисление прав `User.Permission`.
- Зарегистрируйте права и роль, затем создайте пользователя.
```java
enum AppPermission implements User.Permission {
	READ_X, CREATE_X;
	AppPermission() { ProgramStarter.register(this); }
}

enum AppRole implements User.Role {
	MYROLE(() -> AppPermission.values(),
		() -> new User.Feature[]{ DefaultFeature.MODEL_EDITING })
	;
	AppRole(Supplier<User.Permission[]> p, Supplier<User.Feature[]> f) {
		SwingUtilities.invokeLater(() -> ProgramStarter.register(this, f.get(), p.get()));
	}
}

User.register("Пользователь 1", "", AppRole.MYROLE);
```

3) Редактируемые объекты (`Data.Editable`)
- Наследуйтесь от `Data.Editable`. Публичные поля, помеченные `@EditorEntry`, будут редактируемыми в UI.
```java
public static class MyEditable extends Data.Editable {
	@EditorEntry(translation = "Название")
	public String nameField = "Новый объект";
	public MyEditable() { super("Новый объект"); }
}
```

- Для коллекций используйте `EditableGroup<T>` с иконками навигации.
```java
EditableGroup<MyEditable> group = new EditableGroup<>(
	new PathIcon("ui/left.png",  h, h),
	new PathIcon("ui/right.png", h, h),
	MyEditable.class
);
ProgramStarter.register(group);
```

4) Фичи (экраны приложения)
Из коробки доступны:
- `Board.registerBoard(id, Class<T>)` — таблица с компонентами настройки, может отображать
- `Calendar.registerCalendar(id, Class<? extends Calendar.Event>)` — календарь
- `Report.registerReport(id)` — отчёты с визуализациями
- `ItemList.registerList(id, Class<T>)` — плоский список
- `DatedList.registerList(id, Class<T>)` — список с датами

Пример регистрации в роли:
```java
() -> new User.Feature[]{
	DefaultFeature.MODEL_EDITING,
	Board.registerBoard("board", MyProcessable.class),
	Calendar.registerCalendar("calendar", MyEvent.class),
	Report.registerReport("report"),
	ItemList.registerList("list", MyProcessable.class),
	DatedList.registerList("dlist", MyProcessable.class)
}
```

5) Модульный редактор
- Используйте `ModularEditor` и модули из `com.bpa4j.editor.modules`.
```java
ProgramStarter.editor = new ModularEditor(
	new LimitToModule(new StageApprovalModule(), MyProcessable.class),
	new LimitToModule(new LogWatchModule(),   MyProcessable.class),
	new LimitToModule(new CustomerModule(),   MyCustomer.class),
	new ExcludeModule(new FormModule(),       MyProcessable.class, MyCustomer.class)
);
```

6) helppath.cfg (навигатор)
- Создайте `helppath.cfg` (по строке на шаг) в рабочей директории. Формат пути — элементы через точку: флаг+текст.
- Флаги: `s` — "войти пользователем", `f` — "выбрать фичу", `t` — обычный текст, `c` — затемнённый курсив.
- Используйте подчёркивания вместо пробелов и точку с запятой вместо точки внутри элемента.
- См. `com.bpa4j.HelpView` для деталей.

```text
s:User_1.f:board.t:Откройте_сортировку.c:Выберите_параметр
```

## Расширенные возможности

### Процессные объекты (`Processable`) с этапами
- Наследуйтесь от `defaults.editables.Processable` и задайте цепочку `Stage` с правами и обработчиками.
```java
public static class MyProcessable extends Processable {
	public MyProcessable() {
		super(
			"Новый объект",
			new Stage("Этап 1", AppPermission.MANAGE_PROCESSABLE, AppPermission.MANAGE_PROCESSABLE, 0),
			new Stage("Этап 2", AppPermission.MANAGE_PROCESSABLE, AppPermission.MANAGE_PROCESSABLE, 0)
		);
	}
}
```

### Настройка редакторов полей
- Аннотация `@EditorEntry` поддерживает `translation`, `properties` (например, `readonly`, `initonly`) и привязку редакторов (`editorBaseSource`).
- Доступны готовые редакторы: `ConditionalWEditor`, `FlagWEditor`, `FunctionEditor` и др.
```java
@EditorEntry(translation = "Строка", editorBaseSource = ConditionalWEditor.class)
public String strField;

static {
	ConditionalWEditor.configure(MyProcessable.class.getField("strField"), null, e -> Math.random() > 0.4);
}

@EditorEntry(translation = "Группа", editorBaseSource = FlagWEditor.class)
public EditableGroup<MyEditable> group;

FlagWEditor.configure(MyEditable.class.getField("group"), null, null,
	() -> new EditableGroup<>(new PathIcon("ui/left.png", width, height), new PathIcon("ui/right.png", width, height), MyEditable.class)
);
```

### Настройка фич
- Board: сортировки, поставщики элементов.
```java
Board<MyProcessable> board = Board.getBoard("board");
board.setSorter(new Board.Sorter<>() {
	private final JComboBox<Boolean> c = new JComboBox<>();
	public JComponent getConfigurator(Runnable saver, ArrayList<MyProcessable> objects) { c.removeAllItems(); c.addItem(true); c.addItem(false); c.addItemListener(e -> saver.run()); return c; }
	public int compare(MyProcessable a, MyProcessable b) { return a.name.length() - b.name.length(); }
}).setElementSupplier(new GroupElementSupplier<>(MyProcessable.class)).setAllowCreation(true);
```

- Calendar: наполнение событий и "датер".
```java
Calendar<MyEvent> cal = Calendar.getCalendar("calendar");
cal.setEventFiller(map -> { /* заполнение */ })
   .setDater(new EventDater<>(EventDater::listProperties));
```

- Report: конфигураторы и визуализаторы (диаграммы, таблицы, ответы).
```java
JSpinner s = new JSpinner(new SpinnerNumberModel(50, 10, 100, 1));
Report.getReport("report")
	.addConfigurator(saver -> { s.addChangeListener(e -> saver.run()); return s; })
	.addDataRenderer(new ChartDataRenderer(ChartMode.BAR, new ChartDataConverter<>(new GroupElementSupplier<>(MyEditable.class)), "Заголовок", "X", "Y"));
```

### Полезные утилиты
- `PathIcon` — загрузка и масштабирование иконок (`bpa4j/src/main/resources/ui/*`).
- `ImageRenderer` — генерация PNG с текстом или поверх иконки (см. `ImageRendererTest`).

## Лучшие практики
- Держите поля, помеченные `@EditorEntry`, публичными — иначе они не появятся в редакторе.
- Убедитесь, что хотя бы одно право из `enum` реально используется (класс загружен), иначе система посчитает права не определёнными.
- Регистрации (`ProgramStarter.register(...)`) выполняйте рано при старте (часто в статическом инициализаторе роли/прав).
- Для наглядности храните `helppath.cfg` рядом с исполняемым файлом/рабочей директорией.

## Частые проблемы
- Maven не подтягивает зависимость: проверьте, что блок `<dependencies>` объявлен и версия корректна.
- Сообщение: права не определены: убедитесь, что ваш класс с правами загружен (право где‑то упомянуто/зарегистрировано).
- Поле с `@EditorEntry` не видно: сделайте поле `public`.

## Полный пример
См. `bpa4j/src/test/java/com/bpa4j/FullTester.java` — демонстрирует:
- объявление прав и роли, регистрацию пользователя;
- определение `Editable`, `Processable`, `EditableGroup`;
- настройку `ModularEditor` и подключение модулей;
- регистрацию и настройку фич: `Board`, `Calendar`, `Report`, `ItemList`, `DatedList`.


## REST UI и менеджеры компоновки

### Архитектура REST UI
BPA4j поддерживает два типа пользовательского интерфейса:
- **Swing UI** — традиционный десктопный интерфейс на основе Java Swing
- **REST UI** — веб-интерфейс, работающий через HTTP API

REST UI использует абстрактные компоненты (`com.bpa4j.ui.rest.abstractui.*`), которые сериализуются в JSON и отправляются клиенту. Клиент (браузер) отображает эти компоненты как HTML элементы.

### Менеджеры компоновки (Layout Managers)

REST UI предоставляет три основных менеджера компоновки:

#### 1. GridLayout
Размещает компоненты в сетке с фиксированным количеством строк и столбцов.

```java
// Создание GridLayout: rows, columns, hgap, vgap
Panel panel = new Panel(new GridLayout(3, 2, 5, 5));
```

**Особенности:**
- Все ячейки имеют одинаковый размер
- Компоненты добавляются слева направо, сверху вниз
- Если `rows=0`, количество строк вычисляется автоматически
- Если `columns=0`, количество столбцов вычисляется автоматически
- Размер ячейки = `(targetSize - gaps) / count`

**Использование:**
- Таблицы данных (`RestBoardRenderer`, `RestReportRenderer`)
- Календарные сетки (`RestCalendarRenderer`)
- Равномерное распределение элементов

#### 2. FlowLayout
Размещает компоненты последовательно в заданном направлении.

```java
// Горизонтальное размещение (слева направо)
Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT, FlowLayout.LTR, 5, 5));

// Вертикальное размещение (сверху вниз)
Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT, FlowLayout.TTB, 0, 5));
```

**Константы направления:**
- `FlowLayout.LTR` — слева направо (left-to-right)
- `FlowLayout.TTB` — сверху вниз (top-to-bottom)

**Константы выравнивания:**
- `FlowLayout.LEFT` — выравнивание по левому краю
- `FlowLayout.CENTER` — выравнивание по центру
- `FlowLayout.RIGHT` — выравнивание по правому краю

**Особенности:**
- Компоненты сохраняют свой предпочтительный размер
- Автоматический перенос на новую строку/столбец при нехватке места
- Поддерживает горизонтальные и вертикальные отступы (hgap, vgap)

**Использование:**
- Списки элементов (`RestItemListRenderer`, `RestDatedListRenderer`)
- Панели кнопок (`RestWorkFrameRenderer`)
- Вертикальное стекирование без растяжения

#### 3. BorderLayout
Размещает компоненты в пяти областях: NORTH, SOUTH, EAST, WEST, CENTER.

```java
Panel panel = new Panel(new BorderLayout(5, 5)); // hgap, vgap
BorderLayout layout = (BorderLayout) panel.getLayout();

// Добавление компонентов в области
layout.addLayoutComponent(header, BorderLayout.NORTH);
layout.addLayoutComponent(content, BorderLayout.CENTER);
panel.add(header);
panel.add(content);
```

**Области:**
- `BorderLayout.NORTH` — верхняя область (фиксированная высота)
- `BorderLayout.SOUTH` — нижняя область (фиксированная высота)
- `BorderLayout.EAST` — правая область (фиксированная ширина)
- `BorderLayout.WEST` — левая область (фиксированная ширина)
- `BorderLayout.CENTER` — центральная область (занимает оставшееся пространство)

**Особенности:**
- CENTER растягивается на всё доступное пространство
- NORTH/SOUTH определяют высоту по `getPreferredSize()`
- EAST/WEST определяют ширину по `getPreferredSize()`
- Компоненты в краевых областях растягиваются в перпендикулярном направлении

**Использование:**
- Основная структура окон (`RestCalendarRenderer`, `RestReportRenderer`)
- Разделение на header/content/footer
- Навигационные панели

### Лучшие практики для REST UI

#### Явное указание размеров
В REST UI важно явно указывать размеры компонентов:

```java
// Проверка и установка размера по умолчанию
int targetWidth = target.getWidth();
int targetHeight = target.getHeight();
if (targetWidth == 0 || targetHeight == 0) {
    targetWidth = RestRenderingManager.DEFAULT_SIZE.width();
    targetHeight = RestRenderingManager.DEFAULT_SIZE.height();
    target.setSize(targetWidth, targetHeight);
}
```

#### Расчёт размеров для списков
Для динамических списков рассчитывайте высоту на основе количества элементов:

```java
int rowHeight = 35;
int totalHeight = Math.max(100, objects.size() * rowHeight + (objects.size() - 1) * 5);
listPanel.setSize(targetWidth, totalHeight);
target.setSize(targetWidth, 60 + totalHeight + 20); // header + list + footer
```

#### Учёт отступов в GridLayout
При расчёте размеров ячеек учитывайте отступы:

```java
int totalGaps = (columns - 1) * hgap;
int cellWidth = (targetWidth - totalGaps) / columns;
```

#### Обновление UI
Всегда вызывайте `target.update()` после модификации компонентов:

```java
target.add(panel);
target.update(); // Обязательно!
```

#### Выбор правильного Layout Manager
- **GridLayout** — для таблиц с равными ячейками
- **FlowLayout TTB** — для вертикальных списков без растяжения
- **FlowLayout LTR** — для горизонтальных панелей кнопок
- **BorderLayout** — для структурирования окна на области

### Примеры использования

#### Вертикальный список
```java
Panel list = new Panel(new FlowLayout(FlowLayout.LEFT, FlowLayout.TTB, 0, 5));
list.setSize(targetWidth, totalHeight);
for (Object item : items) {
    Panel row = new Panel(new FlowLayout());
    row.setSize(targetWidth, 35);
    row.add(new Label(item.toString()));
    list.add(row);
}
```

#### Таблица данных
```java
int rows = data.size() + 1; // +1 для заголовка
int columns = fields.size();
Panel table = new Panel(new GridLayout(rows, columns, 5, 5));
int cellWidth = (targetWidth - (columns - 1) * 5) / columns;
int totalHeight = rows * 40 + (rows - 1) * 5;
table.setSize(targetWidth, totalHeight);
```

#### Структура окна
```java
Panel root = new Panel(new BorderLayout());
Panel header = new Panel(new FlowLayout());
header.setSize(targetWidth, 60);
Panel content = new Panel(new FlowLayout(FlowLayout.LEFT, FlowLayout.TTB, 0, 5));

BorderLayout layout = (BorderLayout) root.getLayout();
layout.addLayoutComponent(header, BorderLayout.NORTH);
layout.addLayoutComponent(content, BorderLayout.CENTER);
root.add(header);
root.add(content);
```

## Лицензия
Apache License 2.0. См. `bpa4j/pom.xml`.

