# FourteenDaysMarathon

A focused Kotlin practice module inside `kotlin-playground` that captures 14 days of hands-on learning, interview-style exercises, and coroutine/Flow experiments.

## Overview

This module was created as a day-by-day Kotlin marathon to strengthen core language concepts, Java interop, collections, DSLs, generics, delegates, coroutines, and Flow APIs.

The codebase is intentionally practice-oriented:
- some files are concept demos
- some files are interview-style drills
- some files are exploratory experiments to understand runtime behavior
- a few files may contain rough edges or warning-producing code as part of learning

## Topics Covered

### Day-wise practice
- Day 1: execution order, constructors, `init`, and property initialization
- Day 2: properties, companion objects, and backing fields
- Day 3: interfaces, abstract classes, and sealed classes
- Day 4: class and property delegates
- Day 5: overrides and visibility
- Day 6: generics
- Day 7: refactoring and concept refresh
- Day 8: inline functions and SAM conversions
- Day 9: coroutines, coroutine interview practice, and advanced coroutine behavior
- Day 10: Kotlin DSLs and `@DslMarker`
- Day 11: Java interop and annotations
- Day 12: idiomatic Kotlin patterns
- Day 13: assessment-style Kotlin interview practice

### Additional focused exercises
- collections practice
- extension function behavior
- longest substring and sliding-window problems
- grouping and frequency problems
- `StateFlow` and `SharedFlow` practice
- Java scratch files for interop and quick experiments

## Project Structure

```text
FourteenDaysMarathon/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── src/main/kotlin/
│   ├── Day1_ExecutionOrder.kt
│   ├── Day2_Companion.kt
│   ├── Day3_IF_Abstrct_Sealed.kt
│   ├── Day4_Delegates.kt
│   ├── Day5_OverridesAndVisibility.kt
│   ├── Day6_Generics.kt
│   ├── Day7_Refactor.kt
│   ├── Day8_InlineAndSAM.kt
│   ├── Day9_Coroutines.kt
│   ├── Day9_Coroutines_1.kt
│   ├── Day9_InterviewPractice.kt
│   ├── Day10_DSL.kt
│   ├── Day10_DslMarkerExperiement.kt
│   ├── Day12_Idiomatic_Patterns.kt
│   ├── Day13_AssesmentDay.kt
│   ├── CollectionsPractice.kt
│   ├── ExtensionFunction.kt
│   ├── LongestSubstringWithoutDuplicate.kt
│   ├── MockInterviewPractice.kt
│   ├── SharedFlowWorkout.kt
│   ├── StateFlowAndSharedFlow.kt
│   └── com/
│       ├── Day9_Coroutines_Advanced.kt
│       └── interop/
│           ├── Day11_Drills.kt
│           ├── Day11_InteropAndAnnotations.kt
│           ├── Day11_InterviewStyleFixed.kt
│           └── Day11_Interview_Style_Quest.kt
└── src/main/java/
    ├── JavaApp.java
    ├── JavaCaller.java
    ├── JCircle.java
    ├── JSquare.java
    └── SealedClass.java
```

## Tech Stack

- Kotlin JVM `1.9.22`
- Gradle
- Kotlin Coroutines `1.7.3`

## How To Run

### Option 1: IntelliJ IDEA

1. Open the `FourteenDaysMarathon` folder as a Gradle project.
2. Sync Gradle when prompted.
3. Open any file with a `main()` function.
4. Run that file directly from the IDE.

### Option 2: Gradle

To verify the project builds:

```bash
./gradlew test
```

## Validation

The module currently builds successfully with:

```bash
./gradlew test
```

Current notes from the build:
- no dedicated automated tests are present yet
- compilation succeeds
- some compiler warnings remain in coroutine practice files
- Gradle reports a JVM target mismatch warning between Java and Kotlin compilation settings

## What To Explore First

If you want a quick walkthrough, start with these files:

- `src/main/kotlin/CollectionsPractice.kt`
- `src/main/kotlin/Day9_Coroutines.kt`
- `src/main/kotlin/com/Day9_Coroutines_Advanced.kt`
- `src/main/kotlin/StateFlowAndSharedFlow.kt`
- `src/main/kotlin/SharedFlowWorkout.kt`
- `src/main/kotlin/MockInterviewPractice.kt`
- `src/main/kotlin/Day13_AssesmentDay.kt`

## Notes

- This module is learning-driven rather than library-style production code.
- Several files are intentionally verbose because they are meant to demonstrate concepts clearly.
- Some experiments are designed to understand failure modes, cancellation, replay behavior, and backpressure in coroutines and Flow.
- Java files are included where interop or comparison helps explain Kotlin behavior.

## Purpose

This module serves as:
- a Kotlin revision journal
- an interview preparation workspace
- a coroutine and Flow playground
- a reference set of runnable examples for language fundamentals

## Status

Active learning module with incremental additions and refinements over time.
