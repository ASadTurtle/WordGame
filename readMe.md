# Word Game

## JSON Format

Scenes and player info are stored in json files under the chapters directory.
Each chapter stores . Each chapter stores a dictionary of scenes. The players
current save is stored in a seperate json file, along with information about the player.
This includes their perks, statuses, items, and name. When the player creates a
new save, they create a copy of the chapter.json file, which can be overwritten.
This is to prevent save corruption, such as players not being able to traverse
previous branches in later saves.
