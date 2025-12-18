---
name: Миграция ProjectGraph на JavaParser
overview: ""
todos:
  - id: 285d7355-5709-4eb2-a8f2-e7cd3364f914
    content: Добавить зависимость JavaParser в pom.xml
    status: pending
  - id: 3a382f1f-663f-4713-87b2-05b7fd1648b6
    content: Создать PermissionsNodeV2 с парсингом enum Permission через JavaParser
    status: pending
  - id: 563bca49-4028-42d2-acaf-08cf491a4941
    content: Создать RolesNodeV2 с парсингом enum Role и лямбда-выражений
    status: pending
  - id: 3c7997f8-18a9-4c91-ae06-faa4d5fbdea5
    content: Создать EditableNodeV2 с парсингом классов Editable и аннотаций @EditorEntry
    status: pending
  - id: 9299d3ca-1925-44e9-9713-241d09bfa014
    content: Создать ClassNodeV2 с функцией переименования через JavaParser
    status: pending
  - id: 34d68f40-bc90-436a-8db8-e96d979ded8e
    content: Создать ProjectGraphV2, интегрирующий все V2-классы
    status: pending
  - id: 2486ce5f-1216-493d-ab2a-6b9eec304682
    content: Протестировать парсинг всех типов узлов
    status: pending
  - id: 8b7b6318-51d3-41b4-b100-a0353666fb65
    content: Протестировать все операции модификации (add/remove/change)
    status: pending
  - id: 507c075c-7769-4569-a52b-667db353c79a
    content: Переименовать классы и переключиться на V2 реализацию
    status: pending
---

# Миграция ProjectGraph на JavaParser

## Обзор

Создать параллельную реализацию с JavaParser, сохранив ParseUtils для других частей проекта. Все операции модификации (добавление/удаление permissions, roles, properties) будут переписаны с использованием JavaParser API.

## Текущая логика парсинга (что ищут Regex)

### PermissionsNode

- `"public enum \\w+ implements.*?Permission"` — находит enum, реализующий интерфейс Permission
- Извлекает константы enum до первой точки с запятой: `READ_CUSTOMER, CREATE_ORDER, ...`
- Модификация: вставка/удаление констант в теле enum

### RolesNode  

- `"public enum \\w+ implements.*?Role"` — находит enum, реализующий интерфейс Role
- Парсит константы вида: `ADMIN(()->new Permission[]{...}, ()->new Feature[]{})`
- Извлекает лямбда-выражения с массивами разрешений
- Обрабатывает случай `Permission.values()` (все разрешения)
- Модификация: вставка/удаление констант и элементов массивов

### EditableNode

- `"(\\w+)\\s*((?: (?:extends|implements) .*?\\s*){0,2})\\s*\\{"` — находит определение класса
- `"@EditorEntry\\s*\\(.*?translation\\s*=\\s*\"(.*?)\".*?\\).*?(\\w+)\\s*\\w+;"` — находит поля с аннотацией @EditorEntry
- `"//\\s*FIXME:\\s*add property\\s*\"(.*?)\""` — находит FIXME-комментарии для свойств
- `"\\w+\\s*\\(\\)\\s*\\{[\n\\s]*super\\(\"(?:нов[^ ]* )?(.*?)\""` — извлекает objectName из конструктора
- Модификация: добавление/удаление полей, изменение типов и имен

### ClassNode

- Переименование класса во всех файлах проекта
- `"(\\W)"+Pattern.quote(prevName)+"(\\W)"` — замена имени класса с учетом границ слов

### NavigatorNode

- Парсит конфигурационный файл `helppath.cfg` (не Java-код)
- Формат: `s<text>.f<text>.t<text> DisplayName`

## Изменения

### 1. Добавить зависимость JavaParser

**Файл:** `bpa4j/pom.xml`

```xml
<dependency>
    <groupId>com.github.javaparser</groupId>
    <artifactId>javaparser-core</artifactId>
    <version>3.25.7</version>
</dependency>
```

### 2. Создать новые классы с суффиксом V2

#### `PermissionsNodeV2.java`

- Использовать `StaticJavaParser.parse()` для парсинга файла
- Найти enum через `cu.findAll(EnumDeclaration.class)` с фильтром по `implements Permission`
- Извлечь константы через `enumDecl.getEntries()`
- Модификация: использовать `enumDecl.addEnumConstant()` и `entry.remove()`

#### `RolesNodeV2.java`

- Найти enum через `cu.findAll(EnumDeclaration.class)` с фильтром по `implements Role`
- Парсить аргументы констант через `entry.getArguments()`
- Извлекать лямбда-выражения через `LambdaExpr` и массивы через `ArrayCreationExpr`
- Модификация: манипулировать AST для добавления/удаления элементов массивов

#### `EditableNodeV2.java`

- Найти класс через `cu.getClassByName()` или `cu.findAll(ClassOrInterfaceDeclaration.class)`
- Фильтровать по `extends Editable`
- Извлекать поля с `@EditorEntry` через `field.getAnnotationByName("EditorEntry")`
- Парсить параметр `translation` из аннотации
- Находить FIXME-комментарии через `field.getComment()`
- Извлекать objectName из конструктора через `cu.findAll(ConstructorDeclaration.class)` и анализ `super()` вызова
- Модификация: использовать `classDecl.addField()`, `field.remove()`, `field.setType()`

#### `ClassNodeV2.java`

- Базовый класс для работы с именем класса
- Переименование: парсить все файлы, найти использования через `cu.findAll(NameExpr.class)` и заменить

#### `ProjectGraphV2.java`

- Использовать V2-версии всех node-классов
- Сохранить ту же логику обхода файлов
- Использовать JavaParser для определения типа класса вместо regex

### 3. Сохранить ParseUtils

`ParseUtils.java` остается без изменений — используется в других частях проекта (ClassNode, EditableNode).

### 4. Тестирование (выполняется пользователем)

После реализации V2-классов пользователь самостоятельно протестирует:

- Корректность парсинга enum Permission, Role, Editable классов
- Все операции модификации (add/remove/change)
- Сравнение результатов V1 и V2

### 5. Переключение

- После успешного тестирования переименовать классы:
  - `ProjectGraph` → `ProjectGraphLegacy`
  - `ProjectGraphV2` → `ProjectGraph`
  - Аналогично для всех node-классов
- Обновить импорты в зависимых классах

## Ключевые преимущества

- Надежный парсинг через AST вместо хрупких regex
- Поддержка сложных конструкций Java
- Упрощение модификации кода через JavaParser API
- Сохранение обратной совместимости через параллельную реализацию