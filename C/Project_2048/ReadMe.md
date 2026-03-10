#  2048 Game (C Console Application)

Simple console implementation of the classsic **2048 game** written in **C**.

The game runs in the terminal and allows the player to move titles using keyboard arrows.
Titles with the same value merge together and increase the score. The objective is to reach
the **2048 title**.

The application also supports saving the current game state and display the leaderboard with
the best scores.

---

##  Features

-  Classic **2048 gameplay**
-  4x4 game board
-  Control using **arrow keys or WASD**
-  Title merging and score calculation
-  Random title generation (2 or 4)
-  Save and resume previous game
-  Basic **leaderboard system**
-  Console-based interface

---

##  Technologies

-  **C**
-  Standard C/C++ libraries
-  Console input handling (`conio.h`)
-  File handling for saving game state and scores

---

##  How the Game Works

The game uses a **4x4 matrix** representing the game board.

Gameplay loop:

1. Player pressers a direction key
2. Titles move in the selected direction
3. Titles with equal values merge together
4. Player score increases
5. A new title (2 or 4) appears on a random empty position
6. The game continues until:
    -the player reaches **2048** (win),
    -no more moves are possible (game over)

---

   



