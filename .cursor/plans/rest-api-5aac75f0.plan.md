<!-- 5aac75f0-c620-4f23-883a-2b4f1e3c5d47 de81af6b-5986-4032-a1a0-ee29381e8d83 -->
# Implement ProjectGraph REST API

## Overview

Mount REST at `/graph` with read/write operations across project structure: Editables, Roles, Permissions, Navigator, plus maintenance. Port is provided by `ProjectServer` constructor.

## Endpoints (mounted at `/graph`)

- Core
- GET `/graph` → projectFolder, counts by node type
- POST `/graph/reload` → rescan projectFolder and rebuild nodes
- GET `/graph/problems` → `findProblems()` list
- EditableNodes
- GET `/graph/editables` → list all with `name`, `objectName`, `properties`
- POST `/graph/editables` → `createEditableNode(name,objectName,properties[])`
- PUT `/graph/editables/{name}` → rename class/objectName; mutate properties (add/remove/change type/name)
- DELETE `/graph/editables/{name}` → delete via `deleteNode`
- Permissions
- GET `/graph/permissions` → list from `PermissionsNode.permissions`
- POST `/graph/permissions` → add via `PermissionsNode.addPermission`
- DELETE `/graph/permissions/{perm}` → `PermissionsNode.removePermission`
- Roles
- GET `/graph/roles` → list roles with permissions
- POST `/graph/roles` → `RolesNode.addRole(name)`
- DELETE `/graph/roles/{role}` → `RolesNode.removeRole(name)`
- POST `/graph/roles/{role}/permissions` → grant permission
- DELETE `/graph/roles/{role}/permissions/{perm}` → revoke permission
- Navigator (helppath.cfg)
- GET `/graph/navigator` → entries with instructions
- POST `/graph/navigator` → create helppath if missing
- POST `/graph/navigator/entries` → add entry (text)
- PUT `/graph/navigator/entries/{text}` → `changeText`
- DELETE `/graph/navigator/entries/{text}` → `deleteEntry`
- POST `/graph/navigator/entries/{text}/instructions` → `appendInstruction`
- PUT `/graph/navigator/entries/{text}/instructions/{index}` → `replaceInstruction`
- DELETE `/graph/navigator/entries/{text}/instructions/last` → `deleteLastInstruction`
- Features
- GET `/graph/features` → list all `FeatureConfigNode` present in graph
- GET `/graph/impl-features` → list all `FeatureNode` present in graph
- Maintenance
- POST `/graph/save-initial-state` → copy `Data.ser`/`Users.ser` to `resources/initial`

## Files to add/update

- `bpa4j/src/main/java/com/bpa4j/util/codegen/ProjectServer.java`
- Start Grizzly on given port; keep `HttpServer` instance; `start()/stop()`
- Register `ResourceConfig` with `GsonJsonProvider` and HK2 binder for `ProjectGraph`
- `bpa4j/src/main/java/com/bpa4j/util/codegen/rest/GraphResource.java`
- `@Path("/graph")`, implement endpoints invoking `ProjectGraph` APIs
- `bpa4j/src/main/java/com/bpa4j/util/codegen/GsonJsonProvider.java`
- Already added; ensure it’s registered
- `bpa4j/src/main/java/com/bpa4j/util/codegen/rest/dto/*.java`
- Minimal DTOs for request bodies (editable create/update, role/permission operations)

## DI/Context strategy

- Bind the existing `ProjectGraph` instance via HK2 so resources can `@Inject ProjectGraph` safely.

## Tests (optional)

- Smoke test: start server on ephemeral port; GET `/graph`; CRUD one editable; grant/revoke permission; list features and impl-features.

## Documentation

- After implementation, add a README section named "ProjectGraph REST API (AI-generated docs)" describing endpoints, request/response examples, and server startup usage.

### To-dos

- [ ] Implement Grizzly bootstrap in ProjectServer with start/stop and DI binding
- [ ] Add GraphResource read endpoints incl. editables, roles, permissions, navigator, problems, features, impl-features
- [ ] Add write endpoints: editables CRUD, roles CRUD, permissions grant/revoke, navigator ops, reload, save-initial-state
- [ ] Add minimal DTOs for node create/update and role/permission operations
- [ ] Ensure GsonJsonProvider is registered in ResourceConfig
- [ ] Write README section: ProjectGraph REST API (AI-generated docs)