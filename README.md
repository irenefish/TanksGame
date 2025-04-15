# Tanks Game

## Index 
- [How To Run](#how-to-run)
- [Controls](#controls)

## How To Run
There are two steps to take when running the program:
### 1. To build the program:
```
gradle build
```
### 2. To run the program:
```
gradle run
```
## Controls
Players take turns controlling tanks positioned based on the level's layout file. Each tank starts with:
- 250 units of fuel
- 100 health
- 50 initial power (cannot exceed health)

| Keyboard Input | Action                         | Rate of Change                   |
|----------------|--------------------------------|----------------------------------|
| UP arrow       | Tank turret moves left         | +3 radians per second            |
| DOWN arrow     | Tank turret moves right        | -3 radians per second            |
| LEFT arrow     | Tank moves left                | -60 pixels per second            |
| RIGHT arrow    | Tank moves right               | +60 pixels per second            |
| W              | Increase turret power          | +36 units per second             |
| S              | Decrease turret power          | -36 units per second             |
| SPACEBAR       | Fire a projectile (ends turn)  | Instant                          |

- Moving consumes 1 unit of fuel per horizontal pixel.
- If health drops to 0, the tank explodes with a 15-radius blast.
- Falling below the map triggers a 30-radius explosion.
