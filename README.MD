# Sudoku Solver

A production-quality command-line Sudoku game implemented in Java 25, featuring dynamic grid sizing, comprehensive test coverage, and clean architecture.

## Overview

This application allows users to play Sudoku on the command line with full support for puzzle generation, user interaction, and validation. The implementation demonstrates advanced software engineering principles including SOLID design, Test-Driven Development (TDD), and 100% test coverage.

**Key Features:**
- Interactive command-line gameplay
- Dynamic grid sizing (supports 4x4, 9x9, up to 25x25)
- Intelligent puzzle generation using backtracking algorithm
- Real-time validation (rows, columns, subgrids)
- Hint system for assistance
- Auto-solve functionality (fills all except one cell - not in original plan, implemented for ease of manual testing)
- Dockerized deployment
- 100% test coverage

For the complete problem statement and requirements, see [SudokuProblemStatement.md](SudokuProblemStatement.md).

---

## Project Structure

```
Sudoku/
├── src/
│   ├── main/
│   │   ├── java/com/sudoku/
│   │   │   ├── SudokuApplication.java           # Main entry point & game loop
│   │   │   ├── board/                           # Domain model layer
│   │   │   │   ├── Board.java                   # Grid management
│   │   │   │   ├── Cell.java                    # Individual cell logic
│   │   │   │   └── Position.java                # Coordinate value object
│   │   │   ├── command/                         # Command pattern infrastructure
│   │   │   │   ├── Command.java                 # Command interface
│   │   │   │   ├── CommandResult.java           # Result value object
│   │   │   │   ├── CommandParser.java           # Parses user input into commands
│   │   │   │   └── handlers/                    # Concrete command implementations
│   │   │   │       ├── QuitCommand.java         # Exit game
│   │   │   │       ├── CheckCommand.java        # Validate current grid
│   │   │   │       ├── HintCommand.java         # Reveal correct number
│   │   │   │       ├── AutosolveCommand.java    # Fill all except one cell
│   │   │   │       ├── ClearCommand.java        # Clear cell (e.g., A1 clear)
│   │   │   │       ├── PlaceNumberCommand.java  # Place number (e.g., B3 7)
│   │   │   │       └── InvalidCommand.java      # Handle invalid input
│   │   │   ├── game/                            # Application logic layer
│   │   │   │   ├── GameSession.java             # Orchestrates game state
│   │   │   │   ├── PuzzleGenerator.java         # Generation interface
│   │   │   │   ├── PuzzleGeneratorImpl.java     # Backtracking algorithm
│   │   │   │   └── Validator.java               # Rule validation engine
│   │   │   ├── ui/                              # Presentation layer
│   │   │   │   └── ConsoleRenderer.java         # ANSI terminal rendering
│   │   │   └── util/                            # Cross-cutting concerns
│   │   │       ├── Config.java                  # Dynamic configuration
│   │   │       ├── Constants.java               # Command constants
│   │   │       └── Messages.java                # Externalized strings
│   │   └── resources/
│   │       ├── config.properties                # Grid dimension config (SIZE=9)
│   │       └── messages.properties              # User-facing messages
│   └── test/
│       └── java/com/sudoku/                     # Mirrored test structure
│           ├── SudokuApplicationTest.java
│           ├── board/
│           │   ├── BoardTest.java
│           │   ├── CellTest.java
│           │   └── PositionTest.java
│           ├── command/
│           │   ├── CommandParserTest.java
│           │   ├── CommandResultTest.java
│           │   └── handlers/
│           │       ├── QuitCommandTest.java
│           │       ├── CheckCommandTest.java
│           │       ├── HintCommandTest.java
│           │       ├── AutosolveCommandTest.java
│           │       ├── ClearCommandTest.java
│           │       ├── PlaceNumberCommandTest.java
│           │       └── InvalidCommandTest.java
│           ├── game/
│           │   ├── GameSessionTest.java
│           │   ├── PuzzleGeneratorImplTest.java
│           │   └── ValidatorTest.java
│           ├── ui/
│           │   └── ConsoleRendererTest.java
│           └── util/
│               ├── ConfigTest.java
│               ├── ConstantsTest.java
│               └── MessagesTest.java
├── target/                                      # Maven build output
│   ├── classes/                                 # Compiled production classes
│   ├── test-classes/                            # Compiled test classes
│   ├── site/jacoco/                             # JaCoCo coverage reports
│   └── SudokuSolver-1.0-SNAPSHOT.jar            # Executable JAR
├── pom.xml                                      # Maven configuration
├── Dockerfile                                   # Multi-stage Docker build
├── .gitignore
├── SudokuProblemStatement.md                    # Original requirements
└── READ.MD                                      # This file
```

---

## Architecture & Design

### Layered Architecture

The application follows a clean **layered architecture** with clear separation of concerns:

1. **Domain Layer** (`board` package)
   - Contains pure business logic with no external dependencies
   - Immutable value objects (Position) and encapsulated entities (Cell, Board)
   - Grid management and cell state logic

2. **Application Layer** (`game` package)
   - Orchestrates game state through GameSession
   - Puzzle generation using backtracking algorithm
   - Validation engine enforcing Sudoku rules

3. **Presentation Layer** (`ui`, `command`, `SudokuApplication`)
   - User interaction via command-line interface
   - Command Pattern for extensible input handling
   - ANSI terminal rendering with color support

4. **Infrastructure Layer** (`util` package)
   - Configuration management (dynamic grid sizing)
   - Externalized messages for localization
   - Constants for command parsing

### Design Patterns

**1. Command Pattern** (`command` package)
- Replaced if-else chain with concrete command classes
- Supports Open/Closed Principle (add new commands without modifying existing code)
- Clean separation between parsing and execution

**2. Factory Pattern**
- `Cell.createEmpty()` and `Cell.createPrefilled()` for clear object creation
- `Position.fromCliInput(input)` for parsing user coordinates

**3. Strategy Pattern**
- `PuzzleGenerator` interface with `PuzzleGeneratorImpl` implementation
- Allows swapping generation algorithms without changing GameSession

**4. Facade Pattern**
- `GameSession` provides simplified interface to complex subsystems
- Coordinates Board, Validator, and PuzzleGenerator

**5. Value Object Pattern**
- `Position` (Java Record) - immutable coordinate representation
- `CommandResult` - encapsulates command execution results

### SOLID Principles

**Single Responsibility Principle (SRP)**
- Each class has one reason to change:
  - `Board` manages the grid
  - `Validator` checks Sudoku rules
  - `ConsoleRenderer` handles display
  - `CommandParser` parses user input

**Open/Closed Principle (OCP)**
- Command Pattern allows adding new commands without modifying CommandParser
- PuzzleGenerator interface supports multiple generation strategies

**Liskov Substitution Principle (LSP)**
- All Command implementations can be used interchangeably
- PuzzleGenerator implementations are substitutable

**Interface Segregation Principle (ISP)**
- Focused interfaces: `Command`, `PuzzleGenerator`
- No client depends on methods it doesn't use

**Dependency Inversion Principle (DIP)**
- High-level GameSession depends on PuzzleGenerator abstraction
- CommandParser depends on Command interface, not concrete handlers

---

## Design and Assumptions

### Core Design Decisions

**1. Dynamic Grid Sizing**
- Configurable via `src/main/resources/config.properties`
- Default: 9x9 grid (standard Sudoku)
- Supports perfect square sizes up to 25x25 (4x4, 9x9, 16x16, 25x25)
- Subgrid size automatically calculated: √SIZE x √SIZE

**2. Puzzle Generation**
- Uses backtracking algorithm to create valid puzzles
- Generates completely solved grid, then removes numbers
- Ensures exactly 30 pre-filled cells for 9x9 grid
- Configurable difficulty through prefilled cell count

**3. Input Format**
- Case-insensitive commands (e.g., "QUIT", "quit", "Quit")
- Position format: Row (A-I) + Column (1-9), e.g., "B3"
- Commands:
  - `A3 7` - Place number 7 at row A, column 3
  - `C5 clear` - Clear cell at row C, column 5
  - `hint` - Request a hint
  - `check` - Validate current grid
  - `autosolve` - Fill all except one cell (for easier manual testing)
  - `quit` - Exit the game

**4. Validation Rules**
- No duplicate numbers in any row
- No duplicate numbers in any column
- No duplicate numbers in any 3x3 subgrid (for 9x9 grid)
- Users cannot modify pre-filled cells

**5. Test Coverage**
- 100% instruction coverage
- 100% branch coverage
- Tests cover: normal flow, edge cases, error conditions, boundary values

### Key Assumptions

1. **User Input:** Assumes users will follow the command format shown in prompts
2. **Grid Size:** Default 9x9 grid can be changed in config.properties
3. **Terminal Support:** Assumes ANSI color support for best experience
4. **Number Range:** Valid numbers are 1 to SIZE (1-9 for standard Sudoku)
5. **Prefilled Cells:** Cannot be modified during gameplay (enforced by Cell class)
6. **Puzzle Difficulty:** Based on number of prefilled cells (30 for 9x9 grid)
7. **Replay:** After solving, users can start a new game automatically

---

## Required Environment

### Development Environment

**Java:**
- JDK 25 or higher (Amazon Corretto 25 recommended)
- Download: https://docs.aws.amazon.com/corretto/latest/corretto-25-ug/downloads-list.html

**Build Tool:**
- Apache Maven 3.8.0 or higher
- Download: https://maven.apache.org/download.cgi

**IDE (Optional):**
- IntelliJ IDEA (recommended)
- Eclipse
- VS Code with Extension Pack for Java

**Operating System:**
- Linux
- macOS
- Windows (with WSL2 recommended for best terminal experience)

### Runtime Environment

**For JAR Execution:**
- Java 25 JRE or higher
- Terminal with ANSI color support (most modern terminals)

**For Docker Execution:**
- Docker

### Dependencies

All dependencies are managed by Maven and automatically downloaded:

| Dependency | Version | Purpose |
|------------|---------|---------|
| JUnit Jupiter | 5.11.4 | Unit testing framework |
| AssertJ | 3.27.3 | Fluent assertions |
| Mockito Core | 5.15.2 | Mocking framework |
| Mockito JUnit Jupiter | 5.15.2 | JUnit 5 integration |
| JaCoCo | 0.8.13 | Code coverage analysis |

---

## How to Build and Run

### Using Maven

#### 1. Build the Project

```bash
# Clean and compile
mvn clean compile

# Run tests with coverage report
mvn clean test

# Generate JaCoCo coverage report
mvn clean test jacoco:report

# View coverage report (open in browser)
open target/site/jacoco/index.html
```

#### 2. Package the Application

```bash
# Create JAR file
mvn clean package

# This creates: target/SudokuSolver-1.0-SNAPSHOT.jar
```

#### 3. Run the Application

```bash
# Run directly from Maven (default 9x9 grid)
mvn compile exec:java -Dexec.mainClass="com.sudoku.SudokuApplication"

# Run with dynamic grid size (e.g., 4x4 grid with 2x2 subgrids)
mvn compile exec:java -Dexec.mainClass="com.sudoku.SudokuApplication" -Dsubgrid.rows=2 -Dsubgrid.cols=2

# OR run the packaged JAR
java -cp target/SudokuSolver-1.0-SNAPSHOT.jar com.sudoku.SudokuApplication
```

#### 4. Verify Code Coverage

```bash
# Run tests with coverage enforcement (100% required)
mvn clean verify

# This will:
# - Compile all source code
# - Run all tests
# - Generate coverage report
# - Fail if coverage < 100% (instruction or branch)
```

### Maven Build Lifecycle Phases

```bash
mvn clean          # Remove target/ directory
mvn compile        # Compile production code
mvn test           # Run tests
mvn package        # Create JAR file
mvn verify         # Run tests + coverage check
mvn install        # Install to local Maven repository
```

---

## Using Docker

### Build Docker Image

The project uses a **multi-stage Docker build** for optimal image size:

```bash
# Build the image
docker build -t sudoku-app:1.0.0 .
```

**Docker Build Process:**
1. **Stage 1 (Build):** Uses Maven with Amazon Corretto 25
   - Copies pom.xml and downloads dependencies (cached layer)
   - Copies source code
   - Builds JAR file with `mvn clean package -DskipTests`

2. **Stage 2 (Runtime):** Uses Amazon Corretto headless JRE
   - Minimal JRE-only image (no JDK overhead)
   - Copies compiled JAR from build stage
   - Optimized image size

### Run Docker Container

```bash
# Run interactively (default 9x9 grid)
docker run --rm -it sudoku-app:1.0.0

# Run with dynamic grid size (e.g., 4x4 grid with 2x2 subgrids)
docker run --rm -it -e JAVA_TOOL_OPTIONS="-Dsubgrid.rows=2 -Dsubgrid.cols=2" sudoku-app:1.0.0
```

**Docker Run Options:**
- `--rm`: Automatically remove the container when it exits
- `-it`: Interactive terminal (required for gameplay)
- `-e`: Set environment variables for configuration

### Docker Image Optimization

The multi-stage build provides:
- **Smaller image size** (headless JRE vs full JDK)
- **Layer caching** (dependencies cached until pom.xml changes)
- **Security** (minimal attack surface with headless JRE)
- **Fast rebuilds** (only source code changes trigger full rebuild)

---

## Using Dynamic Grid Sizes

### Configuration

Edit `src/main/resources/config.properties`:

```properties
# Standard 9x9 Sudoku (default)
SIZE=9

# Mini 4x4 Sudoku (2x2 subgrids)
# SIZE=4

# Mega 16x16 Sudoku (4x4 subgrids)
# SIZE=16

# Custom size (must be perfect square: 4, 9, 16, 25, etc.)
# SIZE=25
```

### Grid Size Rules

**Requirements:**
- SIZE must be a **perfect square** (4, 9, 16, 25, 36, etc.)
- Subgrid size = √SIZE x √SIZE
- Valid numbers: 1 to SIZE

**Examples:**

| SIZE | Grid | Subgrid | Valid Numbers | Rows/Cols |
|------|------|---------|---------------|-----------|
| 4    | 4x4  | 2x2     | 1-4           | A-D / 1-4 |
| 9    | 9x9  | 3x3     | 1-9           | A-I / 1-9 |
| 16   | 16x16| 4x4     | 1-16          | A-P / 1-16|
| 25   | 25x25| 5x5     | 1-25          | A-Y / 1-25|

### Rebuild After Changing Grid Size

```bash
# Recompile with new configuration
mvn clean compile

# Run with new grid size
mvn exec:java -Dexec.mainClass="com.sudoku.SudokuApplication"
```

### Performance Notes

- **4x4 Grid:** Generates and solves almost instantly
- **9x9 Grid:** Standard performance (generation < 1 second)
- **16x16 Grid:** Slower generation (2-5 seconds typical)
- **25x25 Grid:** Significantly slower (10-30 seconds)

Larger grids exponentially increase the search space for the backtracking algorithm.

---

## Test Coverage Report

### Summary (100% Coverage)

```
BUILD SUCCESS - All tests passing
```

| Metric | Coverage |
|--------|----------|
| Instructions | 100% |
| Branches | 100% |
| Lines | 100% |
| Methods | 100% |
| Classes | 100% |

### Package-Level Coverage

| Package | Instructions | Branches | Classes |
|---------|-------------|----------|---------|
| `com.sudoku.game` | 100% | 100% | 100% |
| `com.sudoku.command.handlers` | 100% | 100% | 100% |
| `com.sudoku.ui` | 100% | 100% | 100% |
| `com.sudoku.board` | 100% | 100% | 100% |
| `com.sudoku` | 100% | 100% | 100% |
| `com.sudoku.command` | 100% | 100% | 100% |
| `com.sudoku.util` | 100% | 100% | 100% |

### Viewing Coverage Reports

```bash
# Generate HTML coverage report
mvn clean test jacoco:report

# Open in browser (Linux/macOS)
open target/site/jacoco/index.html

# Open in browser (Windows)
start target/site/jacoco/index.html
```

---

## Gameplay Examples

### Starting the Game

```
Welcome to Sudoku!

Here is your puzzle:
    1 2 3 4 5 6 7 8 9
  A 5 3 _ _ 7 _ _ _ _
  B 6 _ _ 1 9 5 _ _ _
  C _ 9 8 _ _ _ _ 6 _
  D 8 _ _ _ 6 _ _ _ 3
  E 4 _ _ 8 _ 3 _ _ 1
  F 7 _ _ _ 2 _ _ _ 6
  G _ 6 _ _ _ _ 2 8 _
  H _ _ _ 4 1 9 _ _ 5
  I _ _ _ _ 8 _ _ 7 9

Enter command (e.g., A3 4, C5 clear, hint, check, quit):
```

### Placing Numbers

```
Enter command (e.g., A3 4, C5 clear, hint, check, quit):
A3 4

Move accepted.

Current grid:
    1 2 3 4 5 6 7 8 9
  A 5 3 4 _ 7 _ _ _ _
  B 6 _ _ 1 9 5 _ _ _
  ...
```

### Requesting Hints

```
Enter command (e.g., A3 4, C5 clear, hint, check, quit):
hint

Hint: Cell E5 = 5

Current grid:
    1 2 3 4 5 6 7 8 9
  A 5 3 4 _ 7 _ _ _ _
  ...
```

### Checking for Violations

```
Enter command (e.g., A3 4, C5 clear, hint, check, quit):
check

Number 3 already exists in Row A.
```

### Winning the Game

```
You have successfully completed the Sudoku puzzle!
Press any key to play again...
```

---

## Additional Documentation

- **[SudokuProblemStatement.md](SudokuProblemStatement.md)** - Original problem statement and requirements

---

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 25 |
| Build Tool | Apache Maven |
| Testing Framework | JUnit Jupiter |
| Assertion Library | AssertJ |
| Mocking Framework | Mockito |
| Coverage Tool | JaCoCo |
| Container Runtime | Docker (Multi-stage build) |
| Base Image | Amazon Corretto |

---

## Performance Characteristics

**Memory:**
- Heap usage: ~50MB for 9x9 grid
- Scales with grid size: O(SIZE²)

**Execution Time:**
- Puzzle generation: <1 second (9x9)
- Validation: <1ms per move
- Hint generation: <10ms

**Test Execution:**
- All tests complete in ~2-3 seconds
- Parallel execution supported by Maven Surefire

---

## Quality Guidelines

### Code Quality Standards

- **Test Coverage:** Maintain 100% instruction and branch coverage
- **Class Size:** Keep classes under 200 lines
- **Method Size:** Keep methods under 20 lines
- **Cyclomatic Complexity:** Keep complexity under 10 per method
- **Code Style:** Follow standard Java naming conventions

### Running Quality Checks

```bash
# Run all tests with coverage enforcement
mvn clean verify

# Check for code coverage gaps
mvn jacoco:check

# Generate coverage report
mvn jacoco:report
```

