<!-- 9a7936e5-709d-48ab-ba74-3b0b8a88ede2 a7860da5-14ad-4800-8a87-58073092acba -->
# ProjectGraph Diagnostic Service: Plan

This plan describes key diagnostic checks to be implemented in `ProjectGraph.java` for automatic problem detection, based on code analysis and runtime inspection (where possible).

## Diagnostic Checks To Implement

1. **Track Redundant EditableGroups (using EditableGroupNode):**

- Report duplicate/overlapping group definitions, leveraging `EditableGroupNode` for parsing and analysis.

2. **Track Resource/Icon Path Issues (new ResourceNode):**

- Detect invalid/nonexistent resource paths (such as icon paths in PathIcon, etc.) by scanning known attributes and reporting paths that don't resolve to files.

3. **Detect Missing or Improper Editor Assignments**

- In `EditableNode`, add detection for fields with no assigned editor and for assigned editors that are invalid (do not exist or do not meet requirements; no applicable fallback editor). Report these fields.

4. **Generalization of Event, Report, and Stage Checks**

- Defer detailed checks for calendars, reports, and stages. For now, treat as future work/another diagnostic section, focusing current effort on the above.

5. **Registrator Calls**

- Acknowledge runtime-only tracking for non-direct calls to `Registrator`. Defer implementation to a future runtime analysis module, for later planning.

## Main Files/Classes Involved

- `ProjectGraph.java` — main diagnostics logic
- `EditableGroupNode.java` — group redundancy analysis
- `EditableNode.java` — editor assignment diagnostics
- (To be added) `ResourceNode.java` — resource path checks

## Implementation todos

- diag-group-redundant: Detect redundant EditableGroup definitions
- diag-resource-path: Implement node for icon/resource path resolution and validation
- diag-editor-assignment: Update EditableNode to report editor assignment problems
- stub-registrator-tracking: Add placeholder for (future) runtime Registrator call analysis

All changes should comply with golfy linting style and include JavaDoc as specified.