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

## Scene dictionaries

Scenes and player info are stored in json files under the chapters directory.
Each chapter stores a dictionary of scenes. The players current save is stored
in a seperate json file, along with information about the player. This includes
their perks, statuses, items, and name. When the player creates a new save,
they create a copy of the chapter.json file, which can be overwritten.
This is to prevent save corruption, such as players not being able to traverse
previous branches in later saves.

Scene libraries are essentially *the game*. Each game is stored as a directory
under the `./data` directory. The name of the directory denotes the name of the
game the player may play, written in snake case (i.e. '***The Curse ofSigfried***'
is stored n directory `./data/The_Curse_of_Sigfried`) so the engine may print
the name as the author intends.

Within each of these directories, there one or many `chapter*.json` files, each
being a JSON dictionary of the chapters of the game, containing multiple scenes.
As of writing this I am considering a game config.json file to provide the
game settings to the engine prior to starting it, for things such as:

- Game startup message
- Declare final chapter
- Credits

A saved game is stored as a JSON file, containing a list of scenes, the players
current state, an index for the scene the player is currently in, an index
for the next chapter.

## JSON format specifications

### `gameName`

This field is a string, which informs the game which data folder the scenes are
being loaded from. This is so that when one chapter ends, the game can then load
the next chapter from the correct folder under `/data`.

### `nextChapter`

This field is a string, which informs the game which chapter the player will
start after this one has concluded. It is not a required field, though this is
an optional field. If the field is not found the game will assume this chapter
is the final of the game.

### `currScene`

This field is simply a string, which should match the key of a scene in the
`scenes` field of the save. If it does not match a scenes key then the game will
not be able to load the last scene the player was in, and behaviour will be
unpredictable.

```json
"currScene": "1.1"
```

### `scenes`

This field stores all scenes as a dictionary of JSON objects. Each object in the
array is a JSON representation of a `Scene` class object. The structure of these
objects is implemented in `src/scenes`. Each scene has also neeeds a key, which
corresponds to the scenes index in the chapter. The key is a string, and its format
can be decided by the author. I choose to organise my scenes by a numbered index
such as [`1.1`, `1.1.1`, `1.1.2`, etc.]. The following fields are required for a
JSON representation of a scene:

- `lines: String[]` - A list of strings, where each is a line to be printed to
the terminal. This is the message that will be printed at the start of a scene
to the player. This field can be an empty list if you wish.
- `sceneType: String` - A scene can have multiple types, and
their behaviours are documented in another section. For the game to identify
which types the scenes have, each JSON object in the `scenes` array has a
field `sceneType`, which **must** match the type of some scene from the engine.
The parser should handle loading and saving a scenes type on its own, but it
is suggested you do not alter the type of a scene in the save file.
- `event: Event` - This field is **optional**. It represents a JSON Event object.
object.
- `roots: String[]` - This list holds all the indices of scenes that directly
lead to this scene. When this scene is resolved, the root scenes should no
longer have a branch that leads to this scene. The roots list may be empty, in
the case of the first scene the player enters.

```json
"scenes" : {
    "1.1": {
            "lines": [
                "You only remember your name. Your attempts to recall more of your past only bring the fever in your mind back to your attention. Shifting slightly over the stone bed, you feel the sticky dampness of blood in your clothes, and recognise the smell of copper and smoke."
            ],
            "sceneType": "node", 
            "roots": [],
            ...
        },
    ...
}
```

The following fields are required for `node` type scenes:

- `branches: Branch[]` - A list of JSON Branch objects.

```json
{
    "1.1.1": {
            "lines": ["You slowly draw your attention inward, using your will to focus past the pain and assess your body."],
            "sceneType": "node",
            "roots": ["1.1"],
            "branches": [
                {"bScene": "1.1.1.0", "prompt": "It takes little effort, I’ve always been resiliant. I feel fine.", "event": {"type": "getPerk", "arg": "Hale"}},
                {"bScene": "1.1.1.0", "prompt": "The strong thumping of my hearts is no trouble, my engine throttles faster than most.", "event": {"type": "getPerk", "arg": "Athletic"}},
                {"bScene": "1.1.1.0", "prompt": "My body feels like a great, Brassteel machine. I know I’m stronger than this.", "event": {"type": "getPerk", "arg": "Strong"}},
                {"bScene": "1.1.1.0", "prompt": "Nothing appears out of the ordinary."},
                {"bScene": "1.1.1.1", "prompt": "Something feels missing..."}
            ]
    },
    ...
}
```

The following fields are required for `leaf` type scenes:

- `nextScene: String` - index of the next scene after this leaf scene. **must**
correspond to an existing scene in the library.

### `event`

An event object consists of the following fields:

- `type: String` - The type of the event. Similar to `sceneType` it is important
this field corresponds to a valid Event class from the `src/events` package.
This determines the behaviour of the event when run in the game.
- `arg: String` - The argument of the event, usually a perk, status, or item.

```json
"event": {"type": "getPerk", "arg": "Rebuilt"}
```

### `branch`

A branch object consists of the following fields:

- `bScene: String` - index referencing a scene this branch is designated
to.
- `prompt: String` - the text which is printed to the player for this
string. (e.g. what choice the player is making in this branch)
- `event: Event` - This field is **optional**. It represents a JSON Event object.

```json
{"bScene": "1.1.1", "prompt": "Assess my wounds"},
{"bScene": "1.1.2", "prompt": "Try to stand"},
{"bScene": "1.1.3", "prompt": "Try to remember"},
{"bScene": "1.1.4", "prompt": "Listen"}
```

### `player`

This field stores a JSON object of the player with the following fields.

- `name: String` - The name of the player.
- `perks: String[]` - a list of strings, each representing a perk
- `items: String[]` - a list of strings, each representing an item
- `statuses: String[]` - a list of strings, each representing a status

A game may choose to have a default start for the player when starting a new
game, with a `playerDefault.json` in its data directory. Otherwise all fields
will be interpreted as empty when a new game starts.

```json
"player": {
        "name": "Mikhael",
        "perks": ["Athletic"],
        "items": ["Axe"],
        "statuses": ["Poisoned"]
    }
```

## Credits

- Christian Politis: Teaching me to use Git like someone who has seen a computer
before. General advice for maintaining this project for my portfolio. My
original Rubber Duck for this project.
- Tank: The digital pet turtle who monitors my progress. Second rubber duck for
this project.
- Dad: Nodding while I explain my project, discussing old CYOA books and games
he used to play. Advising to touch grass.

## Personal notes

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
