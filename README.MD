This library is not for public usage, but you can download it if you feel curious or just find it useful.

The author does not give any warranties.

# The plan

## 1) Plug the library in.
```xml
<dependency>
	<groupId>io.github.vladimirpianykh</groupId>
	<artifactId>bpa4j</artifactId>
	<version>1.0.3</version>
</dependency>
```

## 2) Use `_init` snippet in the `Main` class and `_start` snippet inside of the `main` method.

## 3) Fill in the application.
* Add all necessary users.
* Create editable objects required with `_editable` snippet.
* Register groups.
* Add new screens (`Feature`s) by calling `registerList()`, `registerBoard()`, etc on classes in the `com.futurefactory.defaults.features` package.
* 
## 4) Create and fill in the `helppath.cfg` file.
Call `Navigator.init()` and add all the mechanics implemented to the helppath.cfg list.
See the `HelpView` documentation for details.

# Troubleshooting

## Failed to connect Maven dependency.
* Do not forget about `dependencies` block outside of `dependency`.

## The library tells, that permissions are not defined, but they surely are.
* Make sure that you've used at least one permission (that `YourPermission` class has been loaded).

## The field, marked with `EditorEntry` is not visible in the editor.
* Change it's modifier to `public`

## ProjectGraph REST API (AI-generated docs)

Base URL: `http://localhost:{port}` (port is passed to `ProjectServer`)

Base path: `/graph`

- Core
  - GET `/graph` → summary: projectFolder and node counts
  - POST `/graph/reload` → rescan project folder and rebuild nodes
  - GET `/graph/problems` → list of problems

- Editables
  - GET `/graph/editables?detailed=true|false` → always returns array of objects with `name`, `objectName` (if present), `location` (file path). If `detailed=true`, includes `[properties:{name,type}]`.
  - POST `/graph/editables` → create editable
    - body: `{ "name": string, "objectName": string, "properties": [{"name": string, "type": STRING|INT|DOUBLE|BOOL|DATE|DATETIME}] }`
  - PUT `/graph/editables/{name}` → rename/update objectName and properties
    - body (any fields optional):
      `{ "newName": string, "newObjectName": string,
         "addProperties": [{"name": string, "type": enum, "varName": string}],
         "removeProperties": [string],
         "changeProperties": [{"name": string, "newName": string, "type": enum}] }`
  - DELETE `/graph/editables/{name}` → delete

- Permissions
  - GET `/graph/permissions?detailed=true|false` → always returns array of `{permission,location}` (location = file owning permission). No extras for detailed yet.
  - POST `/graph/permissions` body: `{ "permission": "AppPermission.X" }`
  - DELETE `/graph/permissions/{perm}`

- Roles
  - GET `/graph/roles?detailed=true|false` → always returns `{name,location}`. If `detailed=true`, includes `permissions`.
  - POST `/graph/roles` body: `{ "name": "ROLE_NAME" }`
  - DELETE `/graph/roles/{role}`
  - POST `/graph/roles/{role}/permissions` body: `{ "permission": "AppPermission.X" }`
  - DELETE `/graph/roles/{role}/permissions/{perm}`

- Navigator (helppath.cfg)
  - GET `/graph/navigator?detailed=true|false` → always returns list of `{text,location}` (location = helppath.cfg). If `detailed=true`, includes `instructions` array.
  - POST `/graph/navigator` → ensure `helppath.cfg` exists
  - PUT `/graph/navigator/entries/{text}` body: `{ "text": "new text" }`
  - POST `/graph/navigator/entries/{text}/instructions` body: `{ "type": START|FEATURE|TEXT|COMMENT, "text": string }`
  - PUT `/graph/navigator/entries/{text}/instructions/{index}` body: `{ "type": enum, "text": string }`
  - DELETE `/graph/navigator/entries/{text}/instructions/last`

- Features
  - GET `/graph/features?detailed=true|false` → array with `{name,location}` (file path). If `detailed=true`, also includes `type`.
  - GET `/graph/impl-features?detailed=true|false` → array with `{name,location}`. If `detailed=true`, also includes `type`.

- Maintenance
  - POST `/graph/save-initial-state` → copy runtime `Data.ser`/`Users.ser` into project `resources/initial`

- Raw access
  - GET `/graph/raw/node/{node-index}` → `{type,path}`

## AbstractUI REST API (AI-generated docs)

Base URL: `http://localhost:{port}` (port is passed to `UIServer`)

Base path: `/ui`

- UI structure
  - GET `/ui/structure`
    - `{ "validity": true|false, "structure": WindowJson }`
  - POST `/ui/call/{function-id}`
    - Returns HTTP 200 without body, calls the function by it's ID.
  - PUT `/ui/modify/{component-id}`
    - body:
      - `{`
      - `  "data": [ { propertyName: string } ]` // propertyName is some field name from the original structure json acquired via GET `/ui/structure`.
      - `}`
    - Returns HTTP 200 without body, modifies the component by it's ID.

### JSON contract

- WindowJson
  - `{"content": PanelJson}`

- PanelJson
  - `{`
  - `  "type": "panel",`
  - `  "x": number,` // panel X coordinate
  - `  "y": number,` // panel Y coordinate
  - `  "width": number,` // panel width
  - `  "height": number,` // panel height
  - `  "children": [ ComponentJson ... ]` // JSON of all child components in layout order
  - `}`

`ComponentJson` is one of the following, depending on `type`:

- Label
  - `{`
  - `  "type": "label",`
  - `  "x": number, "y": number, "width": number, "height": number,`
  - `  "text": string`
  - `}`

- Button
  - `{`
  - `  "type": "button",`
  - `  "x": number, "y": number, "width": number, "height": number,`
  - `  "text": string,`
  - `  "enabled": boolean`
  - `  "onClick": string` // function ID
  - `}`

- CheckBox
  - `{`
  - `  "type": "checkbox",`
  - `  "x": number, "y": number, "width": number, "height": number,`
  - `  "text": string,`
  - `  "selected": boolean,`
  - `  "enabled": boolean`
  - `}`

- ComboBox
  - `{`
  - `  "type": "combobox",`
  - `  "x": number, "y": number, "width": number, "height": number,`
  - `  "items": [string...],`
  - `  "selectedIndex": number,` // -1 if nothing selected
  - `  "selectedItem": string|null,`
  - `  "enabled": boolean`
  - `}`

- TextField
  - `{`
  - `  "type": "textfield",`
  - `  "x": number, "y": number, "width": number, "height": number,`
  - `  "text": string,`
  - `  "editable": boolean,`
  - `  "maxLength": number`
  - `  "onAction": string` // function ID
  - `  "onTextChanged": string` // function ID
  - `}`

- TextArea
  - `{`
  - `  "type": "textarea",`
  - `  "x": number, "y": number, "width": number, "height": number,`
  - `  "text": string,`
  - `  "editable": boolean,`
  - `  "maxLength": number`
  - `}`

- ProgressBar
  - `{`
  - `  "type": "progressbar",`
  - `  "x": number, "y": number, "width": number, "height": number,`
  - `  "min": number,`
  - `  "max": number,`
  - `  "value": number,`
  - `  "indeterminate": boolean`
  - `}`