# Changes

---
## General

- **NEW:** Banning Pokemon by Dex ID
    - A Banned Pokemon Editor allows a list of Dex ID's to be excluded from the Pokemon Pool in the various randomizers.
    - A new Check Box in the Limit Pokemon Dialog
    - A new Editor Dialog in the Settings Menu
    - Usage of the .rnbp file extension to maintain multiple lists of Banned Pokemon.

- ADDED SUPPORT: Limited randomization by Dex ID
  - Various Check Boxes at the randomizer menu's to allow only randomization of Banned Pokemon.

---

### Pokemon Evolutions

- ADDED SUPPORT: When Randomizing Evolutions, Pokemon cannot evolve into Banned Pokemon.


---
## Starters, Statics & Trades

### Starter Pokemon

- ADDED SUPPORT: All random options are supported, however with small Pokemon Pools the randomizer may resort to Banned Pokemon.

### Static Pokemon

- ADDED SUPPORT: All random options are supported, however with small Pokemon Pools the Swap Standard & Swap Legendaries may crash.

### In-Game Trades

- ADDED SUPPORT: All random options are supported.

---
## Foe Pokemon

### Trainer Pokemon

- ADDED SUPPORT: All random options are supported, however if a Theme is chosen, but all Pokemon from that Theme are banned, the randomizer will fall back on out-of-theme Pokemon.


---
## Wild Pokemon

### Wild Pokemon

- ADDED SUPPORT: All random options are supported, however 1-to-1 mapping may lead to infinite loading if the Pokemon pool is very small.


---
## Misc Tweaks

- **NEW SETTING**: Only randomize Banned Pokemon
  - This setting is found on Static Encounters, Gifted Pokemon, Foe Pokemon and Wild Pokemon Encounters and allows the randomizers to only affect Pokemon present in the Banned Pokemon List.

- **NEW Editor**: Banned Pokemon Editor Dialog
    - This dialog allows the customization of the Banned Pokemon List. It maintains a list of Dex ID's on the left where users can freely edit its values. On the right hand side are buttons to ban Pokemon and add ID's programmatically to the list. Either through banning Pokemon based on their Type, or through individually banning Pokemon.