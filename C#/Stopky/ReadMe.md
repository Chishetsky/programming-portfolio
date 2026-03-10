#  Stopwatch Console Application (Stopky)

This is simple console application wriiten as a small practice project. 
The program allows the user to measure time, save laps, and store them persistently.

##  Features
-  Start, stop and reset stopwatch
-  Save lap times ("okruhy")
-  Display saved laps
-  Persistent storage of laps using JSON file

##  Technologies

-  C#
-  .NET
-  System.Diagnostics.Stopwatch
-  JSON serialization (System.Text.Json)

##  How it works

The application runs in console loop where the elapsed time is continuously displayed.
User input is handled through keyboard commands that control the stopwatch and manage laps.

Saved laps are stored in a JSON file inside the user's local application data directory.

##  Controls

| Key | Action |
|----|------|
| 1 | Start |
| 2 | Stop |
| 3 | Reset |
| 4 | Ukoncit |
| 5 | Ulozit okruh |
| 6 | Zobrazit zoznam okruhov |

##  Purpose of the project

Project was created to practice:

-  basic C# console application development
-  working with time measurement
-  simple data persistance using JSON
