# Changes

---
## General

- NOTE: Change Log Version
  - This is a randomizer built of a basis of the universal-pokemon-randomizer-zx v4.5.1
    - https://github.com/Ajarmar/universal-pokemon-randomizer-zx
      - with many elements from https://github.com/brentspector/universal-pokemon-randomizer
  - This changelog is to express the additions/modifications made from that project to this one
  - Bugs which existed in that version of universal-pokemon-randomizer-zx likely remain
    - This project does not aim for optimization, polish, or professional presentation
    - Mostly for personal use, however some new options may cater to the interests of some Pokemon randomizer fans
      - That being said, comments about possible features to add or insight on features not working properly are encouraged
        - However, updates are not promised

- CHANGED: Randomization Log
  - Updated the log to export an HTML document
    - Format built upon the log of the brentspector randomizer

- **NEW:** Added "Log All" Option
  - Added an option to log all loggable information about the game, even bits which were not changed
    - Does not apply to Move Updates (which are null unless changed)
    - Does not apply to starters, since starters are not checked unless changed

- CHANGED: Similar Strength Options
  - Similar Strength not decided by pure BST
    - Uses a value called BST-Stat Power which is a measure of the relative strength of each of
      the Pokemon's stats summed together.
      - High stats tend to lend to more viability to a Pokemon than low stats take away.
        - A lot of a Pokemon's viability comes from things like typing, ability, moveset, etc.
          Since all of these things can be randomized, a Pokemon's individual stats have a
          much stronger influence on that Pokemon's viability in a given randomizer vs its BST.
          A Pokemon with straight 75's in its stats will likely be less useful than a
          Pokemon whose massive Attack/Speed stats can properly inspire a game plan based on
          a random set of other attributes, despite its lacking Special Attack/Defense.
  - Added two organized list of Pokemon
    - One sorted by BST-Stat Power before randomization and the other after randomization
    - Similar Strength is measured by how close the new Pokemon's index on the updated list is
      to the index of the old Pokemon in the base BST list

---
## Pokemon Traits

### Pokemon Base Statistics

- **NEW:** Random BSTs
  - Added an option to randomize the BSTs of pokemon.
    - If not Followng Evolutions, the BSTs are randomized with a gaussian distribution with an average and standard
      deviation based on its evolution stage and the size of its evolution chain.
      - Otherwise, evolution BST is a direct multiplication of the previous stage's BST.
    - Made a probability check for single evos to be "legendary" in strength. 50% (roughly matches prevalence in Gens I-V)
    - Made a probability check for final evos to be "pseudo-legendary" in strength. 10% (roughly matches prevalence in Gens I-V)
      - Made secondary probabilities for Pokemon which don't make these cuts
    - BSTs are randomize before Pokemon stats are randomized, and these settings are disjointed from each other.

- **NEW:** BSTs - Shuffle
  - Added option to Shuffle BSTs between pokemon
    - Shuffles BSTs between Pokemon of similar evolution stages
      - Particularly their distance from their final evolution
    - Follow Evolutions swaps the BSTs of the base forms of evolutions lines and scales their BSTs up through evolution

- **NEW:** Minimum/Maximum BSTs
  - Added the ability to scale BSTs within a chosen Minimum and/or Maximum
    - THis setting is applied after all stats have been changed/randomized
      - Can also be applied even without any randomizations

- **NEW:** BST Rounding
  - Added a feature which rounds BSTs to the nearest multiple of the chosen number
    - This feature is offered for those who want more neatly data from the randomizer
      - Or for those who want a deliberate gap in power between tiers of Pokemon
  - This option can be utilized without randomizing BSTs
    - Could allow for an intentional, general buff or nerf to all Pokemon in a game
  - Option can be made to round BSTs up, down, or naturally (default)

- **NEW:** Exp Yield Modifications
  - Changes the base exp stats of Pokemon.
  - Only works for Gens 1-4.
    - Minimal. 1 Base Exp for all Pokemon.
    - Average. Base Exp for all Pokemon is the average.
    - Maximal. BAse Exp for all Pokemon is the same the Pokemon with the highest Base Exp.
    - Hit Points. Base EXP equals Pokemon's Base HP.
    - Highest Stat. Base Exp equals Pokemon's highest stat.
    - Stat Average. Base Exp equals Pokemon's stat average.

### Pokemon Types

- **NEW:** Secondary Type Only
  - Added a setting to ensure only the secondary type is changed for each Pokemon.

- CHANGED: Relationship with Random Evolutions
  - Changed seeting such that Follow Evolutions and Same Typing options for random types and random evos
    cannot both be selected.
    - If Follow Evoutions is selected, then types will be randomized after evolutions and the new evolution
      lines will share typing insted of each Pokemon original evolution line.
      - Otherwise, types will be randomized before evolutions to allow Same Typing option to function
  - Updated how "Secondary Typing Only" option functions with Follow Evolutions
    - Will randomize the base Pokemon's secondary typing and have evolution keep the secondary typing,
      and the primary typing will always a Pokemon's original primary typing

### Pokemon Abilities

- **NEW:** Empty Abilities Only
  - Added a setting which simply adds additional, random abilities to Pokemon which do not have a full set

### Pokemon Evolutions

- **NEW:** Space Evolutions
  - Added setting to space out evolution levels when evolutions are randomized.
    - Prevents Pokemon from evolving one after the other and offers a more natural evolution experience.

- CHANGED: Make Evolutions Easier
  - Added a slider to choose the maximum level for Pokemon evolution - max of 55 (bring Gen V Pokemon down to others).
  - Changed the method with which the randomizer condenses Pokemon evolution levels.
    - The randomizer will reduce the highest level evolution to the chosen max.
    - Final stage evolutions will only be reduced if they exceed the chosen max.
    - Intermediate evolutions will be reduced if they exceed 60% of the chosen max.
    - The reduction is in the form of mapping the evolution levels between the chosen max and its 60% mark.

- CHANGED: Evolutions Log
  - Changed the method of logging evolutions
    - Now recursively logs an entire evolution chain from the base stage Pokemon
      - This allows for a review of an entire evolution chain at a quick glance

---
## Starters, Statics & Trades

### Starter Pokemon

- **NEW:** Added Expanded Similar Strength Options
  - Added a drop-down to select different options for starter strength
    - Original: Selects Pokemon of similar relative strength as the game's original starters
    - Each Other: Selects Pokémon such that the three starters are comparable in strength, with no other restriction
    - Weakest: Selects Pokemon starting by looking at the bottom 10% in strength.
    - Strongest: Selects Pokemon starting by looking at the top 10% in strength
    - Moderate: Selects Pokemon starting by looking at the middle 10% in strength
  - Added an option to select Pokemon of similar strength of final evoutions

- CHANGED: Random
  - Removed the option for Random Base Pokemon with 2 Evos
    - Replaced with a slider which filters Pokemon based on the number of remaining evos they have
      - 0 has no restriction, 1 or more, 2 or more
        - This does not filter to base Pokemon only, however
          - That setting will be added later

- **NEW:** Added Misc Starter Restrictions
  - Exact Evos: Find Pokemon with the exact number of remaining evos as which is selected.
  - Base Evos Only: Find Pokemon which have no prior evolutions.
  - No Split Evos: Find Pokemon which do not have any split evolutions remaining.
  - Distinct Types: Find three Pokemon sharing no primary type, and preferably no secondary type either.

- CHANGED: Log
  - A table will be generated, showcasing the starters and evolutions
    - Includes stats, types, and abilities

---
## Moves & Movesets

### Pokemon Movesets

- CHANGED: Move and Moveset Evaluation
  - Added a measure of each move's "efficacy"
    - The average level a move is learned
    - Moves are sorted into tiers based on their average level learned
      - Tiers 0-6
        - 0s are learned from level 1-9
        - 1s are learned from level 10-19
        - 2s are learned from level 20-29
        - 3s are learned from level 30-39
        - 4s are learned from level 40-49
        - 5s are learned from level 50-64
        - 6s are learned from level 65+ as well as moves never learned or strong moves learned at level 1
    - These tiers organize at which levels the move can be learned when movesets are randomized
  - A move's tier will shift when its power or accuracy is sufficiently reduced or improved (by 15%)
  - This ensures a fairly natural power curve in movesets
  - This is the default setting
    - May add an option for total randomness
  - The measure of "good damaging" has been replaced with simply "damaging" and a check of a move's "tier"
    - Damaging in the use of movesets simply means a move appropriate for the level at which the move is learned
    - TMs will use "tier 3" or higher moves when selecting moves
      - Move Tutors and Egg Moves use "Tier 4" or higher

- CHANGED: Log
  - Log will present all moves types, categories, power, accuracy, and PP

---
## Foe Pokemon

### Trainer Pokemon

- CHANGED: Additional Pokemon
  - Allowed the option to give additional pokemon even when not randomized
    - Pre-established pokemon will remain
    - New pokemon will be randomly cloned from pre-established pokemon
      - Level will be randomized
      - Moveset will be replaced with level-up moveset
      - This new team is made before other randomization
      - If a trainer has an ace, the ace will not be cloned unless it is their only pokemon

- **NEW:** Randomize Additional Pokemon Only Setting
  - Added a setting which will only randomize any additional pokemon added to a trainer

- CHANGED: Better Movesets
  - Allowed the option to give better movesets to specific kinds of trainers
    - Boss, Important, Regular
    - Damaging moves selected will be of an appropriate efficacy tier based on a Pokemon's level
  - Allowed giving better movesets without randomizing pokemon

- **NEW:** Buff Elite 4 Setting
  - Added a setting to pick Pokemon for the Elite Four which are in or around the Top 15% of Pokemon in power
    - Will slowly look for Pokemon lower on the list given other restrictions
    - Programmed to have a low chance to allow repeats on the same team

- **NEW:** Similar Types option
  - Added an option to give gyms/elite four/trainer classes similar types to the base game
    - Gym and Elite four trainers have had their types hard-assigned in the games' constants
  - Simply counts the occurrence of types in each group and then uses that count to determine a random chance of
    a Pokemon of that trainer being of each of those types

- **NEW:** Rival Carries Team Throughout
  - Added option to allow the Rival's team to remain constant through out the game
  - Works by checking the randomized Final Rival, then works backwards the evolutions of the Pokemon on their team
    - Allow for a natural growth to the strength of the Rival's team
      - ... unless the final team has a super strong single-stage Pokemon, then it may appear earlier

- **NEW:** Added No Type for Unique Trainers and Type Theme by Trainer Class options
  - These options are available when the "Type Theme" option is selected
  - No Type for Unique - makes sure the rival/champion is not given a type theme
  - Type Theme by Trainer Class - themes entire classes of trainers together, instead of each trainer individually

- CHANGED: Log
  - Log will present a trainer's full team
    - Each Pokemon will display its level, moveset, ability, and held item (if any)
    - First Pokemon on list will be the Pokemon the trainer leads with

---
## Wild Pokemon

### Wild Pokemon

- **NEW:** Scale Catch Rates
  - Added a setting which, when enabled, scales all Pokemon catch rates based on their BST Stat Power vs all other Pokemon

- CHANGED: Log
  - Added section of log which shows which Pokemon have been swapped with which when Global swap is chosen
  - Updated Area Pokemon log
    - Pokemon only appear once on each list, with their various levels after

---
## TM/HMs & Tutors

### TMs & HMs

- CHANGED: Move to TM distribution
  - Updated the distribution of moves into TMs to lower the number of overly weak and overly strong TMs
    - Helps "Better Movesets" on trainers not to be overstacked in strong moves

---
## Items

- CHANGED: Log - Pick Up Items
  - Log now displays all item available with the Pick Up ability
    - Sorted by Pokemon level range and probability

### Special Shops

- CHANGED: Log
  - Log now displays all randomized shops and what items they sell
