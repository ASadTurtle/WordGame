# Word Game

Word Game is a project I started on the 25th of July 2024 as a learning exercise.
The goal is to create a classic, text-based, CYOA game. I aimed to learn multiple
facets of Software Engineering and Game Development with this project, including
problems such as:

- game data storage (text libraries, save files, etc.)
- branching story telling
- building a text-based-game engine
- CLI design (potentially)

and any other issues that come up in the process of developing this game. I
didn't give myself a roadmap for this purposefuly, I wanted to organically encounter
each problem in development to motivate research into recommended practice and design.

I started coding this in Java, and at the time your reading this I may have changed
to another language, likely C#, to expand my horizons in languages.

As of writing this, this repo exists on my uni account hosted by the UNSW gitlab
service. I haven't set up my own project with a personal account, mostly because I
have lots of other work im doing in the UNSW account and keeping it together is
convenient.

If your reading this I've likely pasted a link to this repo on your discord in
the middle of a conversation, and your now wondering if you should pretend to
care about this project, or simply change the convo. If you aren't interested
in this I'm fine with you letting me know, really. This is a project I'm doing
for my own development, and I only discuss it because it's something im interested
in at the time, and I like verbalising my thoughts on my current work to someone.
Right now you are probably my Rubber Duck.

If your reading this still of your own volition, thanks for sticking around, and
props for getting through a readMe file without getting bored. I'll take this
opportunity to say if you have any feedback for my project, I truly would love to
hear it, you can DM me on my Discord account any time, which is likely where you
found this repo in the first place.

When the project is done, I hope you enjoy ***'The Curse of Sigfried - A Dwarven Saga'***

## Scene libraries

Scenes and player info are stored in json files under the chapters directory.
Each chapter stores . Each chapter stores a dictionary of scenes. The players
current save is stored in a seperate json file, along with information about the player.
This includes their perks, statuses, items, and name. When the player creates a
new save, they create a copy of the chapter.json file, which can be overwritten.
This is to prevent save corruption, such as players not being able to traverse
previous branches in later saves.

### Personal notes

Ideas for chapter storage:

- All games stored in `./data` directory.
- we can store different wordgames under different directories. I.e. `./data/CurseOfSigfried`, `./data/RyuuYukiMountain`, etc.
- In these files, chapters are standardly named from `chapter1.json` to
`chapterX.json`
- When running the WordGame, it will prompt which game you would like to select,
(options given are directory names in `./data`).
- When saving a game, the game you started isnt important to the game for
saving/loading. Game states and player data should be standard format regardless.
- How do we know when we have completed a game? Potential scene type? (Maybe
like `GameOverScene`, `ChapterEndScene`).
- Should I make a WH40K story at some point?
- Should I make a chapter document to JSON parsing tool? This will take
fucking ages to make. What does standard input format look like?
- Maybe new scene type specifically for dialogue with characters, that lets us
repeat options of dialogue with them?
