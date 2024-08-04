# Word Game

Word Game is a project I started on the July 25th 2024 as a learning exercise.
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

I started coding this in Java, and at the time you're reading this, I may have changed
to another language, likely C#, to expand my horizons in languages.

As of writing this, this repo exists on my uni account hosted by the UNSW Gitlab
service. I haven't set up my own project with a personal account, mostly because I
have lots of other work I'm doing in the UNSW account and keeping it together is
convenient.

If you're reading this I've likely pasted a link to this repo on your discord in
the middle of a conversation, and your now wondering if you should pretend to
care about this project, or simply change the convo. If you aren't interested
in this I'm fine with you letting me know, really. This is a project I'm doing
for my own development, and I only discuss it because it's something I'm interested
in at the time, and I like verbalising my thoughts on my current work to someone.
Right now you are probably my Rubber Duck.

If your reading this still of your own volition, thanks for sticking around, and
props for getting through a readMe file without getting bored. I'll take this
opportunity to say if you have any feedback for my project, I truly would love to
hear it. You can DM me on my Discord account any time, which is likely where you
found this repo in the first place.

When the project is done, I hope you enjoy ***'The Curse of Sigfried - A Dwarven Saga'***

## Installation

**Note**: *Requires JDK 22 and Maven to be installed to your machine.*

If you'd like to install the Wordgame project, follow the steps below:

```bash
# Get the repository
git clone https://github.com/ASadTurtle/WordGame.git
cd WordGame

# Compile project with maven
mvn compile

# Command to run engine [-q to make maven quiet]
mvn exec:java -q
```

## Scene dictionaries

Scenes and player info are stored in json files under the `./data` or `./saves` directories.
Each chapter stores a dictionary of scenes. The player's current save is stored
in a seperate json file, along with information about the player. This includes
their perks, statuses, items, and name. When the player creates a new save,
they create a copy of the chapter.json file, which can be overwritten.
This is to prevent save corruption, such as players not being able to traverse
previous branches in later saves.

Scene libraries are essentially *the game*. Each game is stored as a directory
under the `./data` directory. The name of the directory denotes the name of the
game the player may play, written in snake case (i.e. '***The Curse of Sigfried***'
is stored in directory `./data/The_Curse_of_Sigfried`) so the engine may print
the name as the author intends.

Within each of these directories, there are one or many `chapter*.json` files, each
being a JSON dictionary of the chapters of the game, containing multiple scenes.
As of writing this I am considering a game config.json file to provide the
game settings to the engine prior to starting it, for things such as:

- Game startup message
- Declare final chapter
- Credits

A saved game is stored as a JSON file, containing a list of scenes, the player's
current state, an index for the scene the player is currently in, an index
for the next chapter.

## JSON format specifications

There are two places where the GameParser reads from: Data files and Save files.
Both directories need json files in the correct format for the game engine to
parse them to game data correctly. If the format within these files does not
match the specifications, then the game will usually abort loading the data
to its game state.

Save files store all data about the gamestate when the game was saved in a
single json file. There are certain fields that are required only in a save
file, such as the `gameName` field, but all fields specified later are parsed
when loading a save.

Data files contain two main json files to start a new game:

- The `playerDefault.json` to specify the default state of the player on a new
game (this is useful if you want your character to have a specific name, and
don't plan on letting the player input their own, or they start with a
particular inventory). The `playerDefault` file does not require all fields in
the player JSON object; this is specified in the `player` section.
- The `chapter1.json` to specify the initial state of the game (Games do not
necessarily need to be longer than a single chapter, but a chapter1 file is
always required to set the initial gamestate). The chapter files contain data
for the scenes within the games, including possible branches, events,
requirements, and optionally, the next chapter. This is essentially 'The Game'
itself. All information about the scenes presented to the player, as well as
their behaviour, should be defined here.

To summarize, the `playerDefault.json` file should only contain a JSON player
object itself, while the `chapter*.json` file should contain all other
information (except `gameName` which is inferred on game startup).
Any save file needs all of the below fields in the one file.

In the sections below, you can find specifications for each JSON field for a
valid json game data file.

### `gameName`

This field is a string, which informs the game which `data` directory the scenes are
being loaded from. This is so that when one chapter ends, the game can then load
the next chapter from the correct directory under `data`. This field is only
necessary for save files. When starting a new game, the gameName is inferred
from the game directory name in `data`.

```json
"gameName": "The_Curse_Of_Sigfried"
```

### `nextChapter`

This field is a string, which informs the game which chapter the player will
start after this one has concluded. This is an optional field. If the field is
not found the game will assume this chapter is the final of the game.

```json
"nextChapter": "2"
```

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

- `lines: String` - A string of paragraphs (seperated by newlines). This is the
message that will be printed at the start of a scene to the player. This field
can be empty if you wish.
- `sceneType: String` - A scene can have multiple types, and
their behaviours are documented in another section. For the game to identify
which types the scenes have, each JSON object in the `scenes` array has a
field `sceneType`, which **must** match the type of some scene from the engine.
The parser should handle loading and saving a scenes type on its own, but it
is suggested you do not alter the type of a scene in the save file.
- `event: Event` - This field is **optional**. It represents a JSON Event
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
- `requirement: Requirement` - This field is **optional**. It represents a JSON
Requirement object.

```json
{"bScene": "1.1.1", "prompt": "Assess my wounds"},
...
{"bScene": "1.1.1.0", "prompt": "It takes little effort, I've always been resiliant. I feel fine.", "event": {"type": "getPerk", "arg": "Hale"}},
...
{
    "bScene": "1.1.4.3",
    "prompt": "There's strength in me yet... I can open this door.",
    "requirement": {"type": "or", "req": [{"type": "perk", "req": "Strong"}, {"type": "perk", "req": "Rebuilt"}]}
}
```

### `requirement`

A requirement object consists of a recursive json object with the following
fields:

- `type: String` - This field specifies what type of requirement this is.
The following are valid types:
  - `"item"`
  - `"perk"`
  - `"status"`
  - `"or"`
  - `"and"`
  - `"not"`
- `req: String | Requirement | Requirement[]` - This field can have one of three
types, depending on what type our requirement is:
  - If the requirement is a `"item"`, `"perk"`, or `"status"` type, then `req`
  will be a String representing the item/perk/status required.
  - If the requirement is a `"or"` or `"and"` type, then `req` will be an array
  of requirements. When evaluated, an `"and"` requirement will return true if
  **all** the requirements in its array are also true. Similarly, an `"or"`
  requirement will return true if **any** of the requirements in its array are
  also true.
  - If the requirement is a `"not"` type, then `req` will be a requirement. When
  evaluating a `"not"` requirement, it simply flips the boolean result of the
  sub-requirement.

With all of these combined, one can make branches with complex requirement logic
to form unique choices in the game.

```json
"requirement": {"type": "and", "req": [
        {"type": "item", "req": "Haft"},
        {"type": "perk", "req": "Axehead"},
        {"type": "perk", "req": "Blacksmith"},
        {"type": "not", "req": {"type": "status", "req": "Broken arm"}}
    ]
}
```

### `player`

This field stores a JSON object of the player with the following fields.

- `name: String` - The name of the player.
- `perks: String[]` - a list of strings, each representing a perk
- `items: String[]` - a list of strings, each representing an item
- `statuses: String[]` - a list of strings, each representing a status

The `perks`, `items`, and `statuses` fields are not required to parse a player.

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

Ideas:

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
- Make saves have a named directory matching a game name in `./data`?
- Ommit having `parseGameName` and storing `gameName` in save json
- Why not print save time in loadGame? (Access modified timestamp of save json)
