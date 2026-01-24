# Aoko User Guide

Aoko is a simple CLI chatbot for managing tasks (todos, deadlines, and events). Tasks can be marked as done/not done, deleted, and listed. Aoko automatically saves your task list to disk and loads it on startup.

---

## Quick start

1. **Build & run**
   - Compile and run from the project root (example):
     - `javac -cp src\main\java -d bin src\main\java\*.java`
     - `java -classpath bin Aoko`

2. **Try a few commands**
   - `todo borrow book`
   - `deadline return book /by Sunday`
   - `event project meeting /from Mon 2pm /to 4pm`
   - `list`
   - `mark 2`
   - `unmark 2`
   - `delete 1`
   - `bye`

---

## Commands

### `list`
Shows all tasks in the list.
```
list
```

### `todo <description>`
Adds a ToDo task (no date/time attached).
```
todo borrow book
```

### `deadline <description> /by <by>`
Adds a Deadline task to be done before a given time/date.  
`<by>` is stored as a **string** (no parsing required).
```
deadline return book /by Sunday
deadline do homework /by no idea :-p
```

### `event <description> /from <from> /to <to>`
Adds an Event task with a start and end.  
`<from>` and `<to>` are stored as **strings**.
```
event project meeting /from Mon 2pm /to 4pm
event orientation week /from 4/10/2019 /to 11/10/2019
```

### `mark <taskNumber>`
Marks a task as done.
```
mark 2
```

### `unmark <taskNumber>`
Marks a task as not done yet.
```
unmark 2
```

### `delete <taskNumber>`
Deletes a task from the list.
```
delete 3
```

### `bye`
Exits the program.
```
bye
```

---

## Task display format

- `[T]` = ToDo
- `[D]` = Deadline
- `[E]` = Event
- `[X]` = done, `[ ]` = not done

Examples in `list`:
- `1.[T][X] read book`
- `2.[D][ ] return book (by: June 6th)`
- `3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)`

---

## Data storage (auto-save)

Aoko saves automatically whenever the task list changes (add/mark/unmark/delete), and loads on startup.

- **Path (relative to project root):** `./data/aoko.txt`
- **If the file/folder doesn’t exist:** Aoko starts with an empty list and creates it on first save.
- **If the file has corrupted lines:** those lines are skipped.

### Storage format (one task per line)
```
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

---

## Semi-automated UI testing (I/O redirection)

This project supports running scripted commands and comparing output.

1. Go to:
   - `text-ui-test/`
2. Edit `input.txt` with commands to feed into Aoko.
3. Run:
   - `runtest.bat`
4. The script will:
   - compile to `bin/`
   - run `Aoko < input.txt > ACTUAL.TXT`
   - compare `ACTUAL.TXT` with `EXPECTED.TXT`