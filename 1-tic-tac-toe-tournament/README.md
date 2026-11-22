# Tic-Tac-Toe Tournament (EX1)
Tic-Tac-Toe Tournament (EX1)

An n x n Tic-Tac-Toe tournament implementation in Java with a customizable k-in-a-row win condition. Supports human and automated players (NaivePlayer, WhateverPlayer, SmartPlayer), factory pattern instantiation, and console or void rendering.

Automated Player Strategies:
- WhateverPlayer: Chooses a random unoccupied cell on the board.
- NaivePlayer: Scans the board sequentially row by row and picks the first available cell.
- SmartPlayer: Prioritizes winning moves, blocks opponent threats, and targets key board positions to build winning streaks.
