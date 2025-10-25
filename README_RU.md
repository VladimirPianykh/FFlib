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
	AppPermission() { Registrator.register(this); }
}

enum AppRole implements User.Role {
	MYROLE(() -> AppPermission.values(),
		() -> new User.Feature[]{ DefaultFeature.MODEL_EDITING })
	;
	AppRole(Supplier<User.Permission[]> p, Supplier<User.Feature[]> f) {
		SwingUtilities.invokeLater(() -> Registrator.register(this, f.get(), p.get()));
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
Registrator.register(group);
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
- Регистрации (`Registrator.register(...)`) выполняйте рано при старте (часто в статическом инициализаторе роли/прав).
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

## Лицензия
Apache License 2.0. См. `bpa4j/pom.xml`.
