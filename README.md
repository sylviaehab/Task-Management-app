# Storage Options Matrix

| Storage Option | Data Type Stored                       | Storage Capacity Limits        | ACID Support | Backup Difficulty | Example in This App |
|----------------|----------------------------------------|--------------------------------|--------------|-------------------|---------------------|
| Files          | Unstructured (images, PDFs, media)     | Limited by device storage      | No           | Medium            | Store attachments (images, pdfs) |
| DataStore      | Structured key-value / proto (preferences) | Small (recommended < ~10 MB) | No           | Easy              | User preferences (dark mode, sort order) |
| Room (SQLite)  | Structured relational data (entities & relations) | Limited by device storage / SQLite limits | Yes | Easy              | Store Users, Projects, Tasks, Attachments metadata |



# Performance Report

Query: Get all Projects with more than 3 tasks.

| Query Type  | Time (ns) for 100 runs |
|-------------|------------------------|
| Room @Query | To be filled after run |
| RawQuery    | To be filled after run |

Notes:
- Run times will vary by device/emulator and number of rows.
- After running MainActivity you will see logs:
  - PERf -> "Room query (100 runs): Xns"
  - PERf -> "Raw query (100 runs): Yns"




# Task Manager — Room Data Layer 

## Quick start
1. Create an Android app project (Kotlin). Minimum SDK 24 recommended.
2. Add Room, Lifecycle, Coroutines, and Kotlin dependencies in `build.gradle`:
   - implementation "androidx.room:room-runtime:2.5.2"
   - kapt "androidx.room:room-compiler:2.5.2"
   - implementation "androidx.room:room-ktx:2.5.2"
   - implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0"
   - implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0"
   - implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.6.1"
   - implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"
3. Paste the Kotlin files into the package `com.example.taskmanager`.
4. Run the app. `MainActivity` will automatically insert test data and log outputs.

## Suspend vs Flow (2.4)
- `suspend fun getAllProjectsOnce()` returns a single snapshot of the data when called.
- `fun getAllProjectsFlow(): Flow<List<Project>>` is reactive and emits every time the underlying table changes.

## What the logs show
- `DB_TEST` — insert logs and relation query output.
- `DAO_TEST` — suspend vs flow outputs.
- `PERF` — performance measurement outputs for Room @Query vs @RawQuery.

## UML (textual)
User (1) -> (many) Project
Project (1) -> (many) Task
Task (1) -> (many) Attachment
