# Scratch-From-Scratch

A Scratch-inspired DSL and game engine built in Java with JavaFX.

## Prerequisites

### Java
Download and install JDK 17 from:
https://www.oracle.com/java/technologies/downloads/#java17

### JavaFX SDK
Download the JavaFX SDK (Type: SDK — NOT jmods):
https://gluonhq.com/products/javafx/

### Maven

**Mac:**
1. Install Homebrew (if not already installed):
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```
2. Add Homebrew to your PATH (replace `your_username` with your Mac username):
```bash
echo >> /Users/your_username/.zprofile
echo 'eval "$(/opt/homebrew/bin/brew shellenv zsh)"' >> /Users/your_username/.zprofile
eval "$(/opt/homebrew/bin/brew shellenv zsh)"
```
3. Install Maven:
```bash
brew install maven
```

**Windows:**
1. Download Maven from https://maven.apache.org/download.cgi (Binary zip archive)
2. Extract it to a folder e.g. `C:\Program Files\Maven`
3. Add Maven to your PATH:
   - Search "Environment Variables" in the Start menu
   - Under System Variables, find `Path` → Edit → New
   - Add the path to Maven's `bin` folder e.g. `C:\Program Files\Maven\apache-maven-3.x.x\bin`
4. Restart your terminal and verify with `mvn --version`

## VS Code Setup
Add this to `.vscode/settings.json`:
```json
{
    "java.project.sourcePaths": ["src/main/java"],
    "java.configuration.updateBuildConfiguration": "automatic"
}
```

## Running the Project
```bash
mvn javafx:run
```

## Reference
- scratch.mit.edu