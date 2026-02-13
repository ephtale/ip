# Aoko Chatbot

![Aoko Aozaki](https://static.wikia.nocookie.net/typemoon/images/9/9d/Melty_blood_aoko_ending.png/revision/latest?cb=20130622095353)

Aoko is a simple task-tracking chatbot with:
- A **CLI** (terminal) mode
- A **JavaFX GUI** mode

## Commands

- `todo <description>`
- `deadline <description> /by <date>`
- `event <description> /from <start> /to <end>`
- `list`, `mark <n>`, `unmark <n>`, `delete <n>`, `find <keyword>`, `on <date>`, `bye`
- `undo` (undoes the most recent successful change)

## Requirements

- **Java 17** (recommended: Temurin/OpenJDK 17)

## Run (developer)

CLI:

```powershell
./gradlew run
```

GUI:

```powershell
./gradlew runGui
```

## Build jars (for end users)

Build both jars:

```powershell
./gradlew shadowJar shadowGuiJar
```

Outputs:
- `build/libs/Aoko.jar` (CLI)
- `build/libs/Aoko-gui.jar` (GUI)

## Run jars (end users)

CLI jar:

```bash
java -jar Aoko.jar
```

GUI jar:

```bash
java -jar Aoko-gui.jar
```

## Open GUI by double-clicking a file (Windows)

The GUI jar supports an optional first argument: a path to the save file.

Example:

```bash
java -jar Aoko-gui.jar "C:\path\to\my-save.txt"
```

To make “double-click a file to open the GUI” work on Windows, you need to associate your chosen file type (e.g. `.aoko`) to a command like:

```text
javaw -jar "C:\path\to\Aoko-gui.jar" "%1"
```

Notes:
- `javaw` avoids opening a console window.
- This requires Java 17+ installed on the user’s machine.
- If you think a method is too long, it's probably filled with assertions.