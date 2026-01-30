Running the POS System (JavaFX 17)

Prerequisites
- JDK 17 installed and configured in your IDE or PATH.
- JavaFX 17 SDK downloaded. Example path used below:
  C:\Users\kalab\Downloads\openjfx-17.0.18_windows-x64_bin-sdk\javafx-sdk-17.0.18\lib

Run from PowerShell (modular)
1) Compile
```powershell
$lib="C:\Users\kalab\Downloads\openjfx-17.0.18_windows-x64_bin-sdk\javafx-sdk-17.0.18\lib"
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path out | Out-Null
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac --module-path "$lib" --add-modules javafx.controls,javafx.fxml -d out $files
```
2) Run
```powershell
java --module-path "$lib;out" --add-modules javafx.controls,javafx.fxml -m possystem/GUI.Main
```

Run in IntelliJ
- Set Project SDK to Java 17 (File → Project Structure).
- Add JavaFX `lib` folder as a Library and ensure it is on the Modulepath for the module.
- Edit Run Configuration for `GUI.Main` and add VM options:
  --module-path "C:\path\to\javafx\lib" --add-modules javafx.controls,javafx.fxml

Data storage
- Application stores data under your home folder in `possystem-data` (e.g. `%USERPROFILE%\possystem-data`).

If you want I can add a Gradle wrapper to automate builds and IDE config. Reply "Add Gradle" to proceed."