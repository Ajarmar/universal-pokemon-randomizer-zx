# What this is - Universal Pokemon Randomizer ZXQ

To allow for more granularity in randomizing your Pokemon Nuzlocke runs I added a feature to the popular Universal Pokemon Randomizer ZX.
This feature contains a dialog to edit a list of Dex ID's of Pokemon you don't want to see in your Nuzlocke, be it wild encounters, static encounters or trainer Pokemon.
Next to the list there is a specific set of buttons and check boxes to automatically generate all the Dex ID's based on Pokemon Types.
Besides the Banned Pokemon Editor you will also find a feature to have the randomizer only affect the wild/static/gifted or foe Pokemon on your list of Banned Pokemon. This way, you can choose which encounters remain un-randomized and it should provide a randomize-lite experience tailored to your personal preferences.
Popular use cases
  1 Go through each Pokemon 	individually and ban whatever you don't wish to encounter. (I 	recommend you save this to your 'personal_banlist.rnbp')
  2 Ban everything, then unban 	Pokemon by their type to have a broad selection of Pokemon 	with those shared typings (either primary or secondary). Then, 	optionally, paste the list from option 1 into the text area (don't 	worry about duplicates)
  3 Unban everything, and ban 	Pokemon by their type to have a narrow selection of Pokemon 	of those types (they get banned by either of their types). Once 	again optionally paste in your personal list of bans.
  4 Mix and mesh! Just think about what each click does to your 	list and have fun with the result.
## Cool modes
Here are some of the ideas I had when developing this feature about the possible new ways of playing a randomized Pokemon Nuzlocke.
  * No more Mr. Mime. Hate clowns? Ban him. Afraid of spiders? Ban the buggers. This feature 	takes away some of the disappointment of first encounters of Pokemon 	that just make the game worse for you.
  * Be your favorite trainer type: Role play as a Rich Kid, or a Hiker and ban everything but the 	Pokemon/Types that they use in battle. You can even go as far as 	banning everything but a Rattata and show Joey that yours' is the 	real top 1%
  * Multiplayer Soul Link: Divide the pool of Pokemon types among you and for friends and ban/unban 	everything else, either taking turns choosing types or after 	discussing among each other. See if you can manage with only part of 	the typing system and still become the best.
  * Head to head: 	Much like in the multiplayer soul link you ban/unban each others 	Pokemon taking turns and thus secure specific types for yourself. 	Only this time you try and make it as difficult as possible for your 	opponent to assemble a winning team. Optionally only randomizing 	banned Pokemon to guarantee certain encounters for yourself while 	banning them away for your opponent.
  * Legacy: Ban all 	Pokemon you used in a previous Nuzlocke and have a fresh experience 	for your new run. Building up a list of Banned Pokemon as you go 	about your attempts. Using the extra 	option to only randomize banned Pokemon should result in an 	experience much like the original game.
  * Be creative: You can really push the limits and ban all but a single Pokemon, only leave Baby 	Pokemon unbanned, or ban everything and unban only the Pokemon with 	a single type attribute for a pure typed run.
## How to use the feature
Once you've loaded your ROM file you can check the box to the left of the Limit Pokemon button in the top left of the window. This should enable it and allow you to bring up a dialog by clicking on the Limit Pokemon button. Inside this dialog select at least one generation of Pokemon you want to have available (or all of them) and that should allow you to select the "Ban Pokemon by ID's" check box, check it and close the dialog with the "OK" button.
Then you can find the Banned Pokemon Editor in the settings menu on the right. Click settings, and then "Banned Pokemon Editor". In this dialog you will find a text area on the left, it will be empty, some check marks and buttons on the right, and a save/load/close button across the bottom.
Whenever you click a ban or unban button on the right, the relevant Pokemon ID will be added to the text area on the left, tailor the list however you see fit. Then press save in the bottom left. This will prompt you to select a file; you can choose any name for the file so you can find it later more easily or just choose the default. The tool will always save a "bannedpokemon.rnbp" file to use for randomizing, the custom named file is just for bookkeeping.
After saving be sure to choose your preferred randomization settings and click randomize. Presto! Your newly patched game will be custom randomized to your liking :)
Besides the Editor there are check boxes at each of the static, gifted, foe and wild Pokemon tabs to exclusively randomize Pokemon that are present on your Banned Pokemon List. Any Pokemon not on that list will remain untouched.
Where to get it

Some settings don't play nice when you ban too many Pokemon, i.e. randomizing by theme can cause weird behavior if you've banned all Pokemon of a type, or randomizing starters with 2 evolutions after banning too many basic Pokemon. I tried to program some break-outs for them, but you might encounter an unhandled exception. Remember that randomizing should take seconds, not minutes and to close down the tool if it takes too long.
Final notes

# Original project
(pull request pending over [here](https://github.com/Ajarmar/universal-pokemon-randomizer-zx/pull/695)

Universal Pokemon Randomizer ZX by Ajarmar

With significant contributions from darkeye, cleartonic

Based on the Universal Pokemon Randomizer by Dabomstew

# Info

This fork was originally made to make some minor tweaks and fixes, but became a bit more ambitious since 2020. There are several new features and supported games (including 3DS games) compared to the original Universal Pokemon Randomizer.

Have a look at the [release page](https://github.com/Ajarmar/universal-pokemon-randomizer-zx/releases) for changelogs and downloads.

# Contributing

If you want to contribute something to the codebase, we'd recommend creating an issue for it first (using the`Contribution Idea` template). This way, we can discuss whether or not it's a good fit for the randomizer before you put in the work to implement it. This is just to save you time in the event that we don't think it's something we want to accept.

See [the Wiki Page](https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Building-Universal-Pokemon-Randomizer-ZX) for setting up to build/test locally.

### What is a good fit for the randomizer?

In general, we try to make settings as universal as possible. This means that it preferably should work in as many games as possible, and also that it's something that many people will find useful. If the setting is very niche, it will mostly just bloat the GUI.

If your idea is a change to an existing setting rather than a new setting, it needs to be well motivated.

# Feature requests

We do not take feature requests.

# Bug reports

If you encounter something that seems to be a bug, submit an issue using the `Bug Report` issue template.

# Other problems

If you have problems using the randomizer, it could be because of some problem with Java or your operating system. **If you have problems with starting the randomizer specifically, [read this page first before creating an issue.](https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/About-Java)** If that page does not solve your problem, submit an issue using the `Need Help` issue template.
