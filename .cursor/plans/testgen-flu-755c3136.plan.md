<!-- 755c3136-7398-4d37-a3b3-62b4795a3d57 b8e6bab7-763d-4981-8897-5c2e9bb8ec75 -->
# Refactor TestGen to Fluent API

## Overview

Transform `TestGen` from a static utility into a generic fluent builder that implements `Supplier<T extends Editable>`, supporting customizable field generation with configurable default sources. The `get()` method returns individual objects, and `to(group, amount)` initiates generation of n objects.

## API Design

### Core Usage Pattern

```java
TestGen<Customer> gen = TestGen.gen(Customer.class)
    .withField("age", 25)
    .withField("email", () -> generateEmail())
    .withName(() -> generateName())
    .setDefaultSource(String.class, () -> randomString())
    .to(customerGroup, 10);

Customer c1 = gen.get();  // Get individual objects

List<Customer> list = TestGen.gen(Customer.class)
    .withName("Test Customer")
    .toList(5);  // Generate 5 without adding to group
```

### Additional Methods

**Generation Control:**

- `toList(int n)` - Generate n objects and return as List<T> without adding to any group
- `toArray(int n)` - Generate n objects and return as T[] array
- `reset()` - Reset the generator to start from index 0 again
- `withAmount(int n)` - Set amount before calling to(group) (alternative to to(group, n))

**Field Configuration:**

- `withField(String name, Object valueOrSupplier)` - Set specific field value or supplier
- `withFields(Map<String, Object> fieldsMap)` - Bulk set multiple fields at once
- `setDefaultSource(Class<?> type, Supplier<?> source)` - Override default handler for all fields of given type
- `skipField(String... fieldNames)` - Exclude specific fields from auto-population
- `copyFieldsFrom(T template)` - Copy field values from an existing object as defaults

**Naming Configuration:**

- `withName(Object valueOrSupplier)` - String or Supplier<String> for name generation
- `withNamePattern(String pattern)` - Pattern like "Customer #%d" where %d is index
- `withNameProvider(NameProvider p)` - Override name generation strategy

**Post-Processing:**

- `forEach(Consumer<T> action)` - Apply action to each generated object after creation
- `withPostProcessor(Function<T, T> processor)` - Transform each object after generation
- `onComplete(Runnable action)` - Execute action when all objects are generated

**Validation:**

- `withVerifier(Verifier v)` - Override verification logic
- `skipVerification()` - Disable verification warnings
- `throwOnInvalid()` - Throw exception instead of warning on verification failure

**Utilities:**

- `withRandomSeed(long seed)` - Reproducible random generation
- `peek(Consumer<T> action)` - Execute action on each object without modifying it (for debugging)
- `clone()` - Create a copy of this TestGen with the same configuration

## Implementation Plan

### 1. Create TestGen Builder Class

**File**: `bpa4j/src/main/java/com/bpa4j/util/testgen/TestGen.java`

- Make `TestGen<T extends Editable>` generic, remove final modifier
- Add instance fields: `type`, `amount`, `target`, `currentIndex`, `fieldOverrides`, `nameSupplier`, `defaultSources`, `verifier`, `skipFields`, `template`, `verificationPolicy`
- Implement `Supplier<T>` with `get()` method that returns single objects (tracks count internally)
- Static factory method: `gen(Class<T> type)` returns new `TestGen<T>` instance
- `to(EditableGroup<T> target, int amount)` sets target group and amount, returns this
- `toList(int n)` generates n objects and returns as ArrayList<T>
- Deprecate old `generate(int amount, EditableGroup<?>... groups)` method

### 2. Implement Fluent Methods

- Core methods: `withField`, `withName`, `setDefaultSource`, `to`, `toList`, `toArray`
- Advanced methods: `withFields`, `copyFieldsFrom`, `withNamePattern`, `forEach`, `withPostProcessor`
- Validation methods: `withVerifier`, `skipVerification`, `throwOnInvalid`
- Utility methods: `reset`, `peek`, `clone`, `withRandomSeed`
- All methods return `this` for chaining (except `toList`, `toArray`, `get`)

### 3. Create Default Source System

**File**: `bpa4j/src/main/java/com/bpa4j/util/testgen/DefaultSourceRegistry.java`

Built-in handlers for common types:

- `String`: random strings or existing names array
- `int`, `Integer`: random integers (0-1000 range)
- `double`, `Double`: random doubles
- `boolean`, `Boolean`: random boolean
- `Editable` subclasses: cycle through corresponding group elements

### 4. Update Generation Logic

- Move field population logic from old `generate()` into new `get()` method
- Apply `defaultSources` first, then field-specific `withField()` overrides
- Support both direct values and `Supplier<?>` using generics with compile-time detection
- Track `currentIndex` to know when generation is complete
- Apply post-processors and validation after object creation

### 5. Handle Edge Cases

- Type safety: ensure suppliers return compatible types (use generics)
- Null handling: allow null from suppliers
- Field name validation: warn if field doesn't exist
- Multiple `to()` calls: reset state, allowing reuse

## Files to Modify

- `bpa4j/src/main/java/com/bpa4j/util/testgen/TestGen.java` - Main refactoring

## Files to Create

- `bpa4j/src/main/java/com/bpa4j/util/testgen/DefaultSourceRegistry.java` - Built-in type handlers

### To-dos

- [ ] Refactor TestGen into builder pattern with instance fields and Supplier implementation
- [ ] Implement withField, withName, setDefaultSource, to, and additional convenience methods
- [ ] Create DefaultSourceRegistry with built-in type handlers for common types
- [ ] Move generation logic from old generate() to new get() method with override support
- [ ] Mark old generate() method as deprecated and add migration notes