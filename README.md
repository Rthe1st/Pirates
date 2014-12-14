Pirates
=======
Pirates is a tile based, turn based puzzle game written for android.
It's not very fun and has (unironic) bad graphics.
It's also half finished because it's hard to stay motivated when you realise the base idea is bad.
Yay!

Game:
The player completes levels by moving their ship from its start point to the goal tile.
They must do so whilst avoiding enemy ships, optionally using mines to block them.
It also has a level editor.

Code Structure:
TileView is a custom view which the main game map is drawn on.
LevelActivity holds this view.
GameLogic interacts with both these, it manages the game state.
MapData is inside GameLogic, it holds the actual data on in game obejcts and their states.
MapData is mainly made up of a series of HashMaps, 1 per in game object type (ship, enemy, tile, mine).
Together these hash-maps are used together to form an implicit map by using Cords as their keys.

For the level editor, the same structure applies but GameLogic is replaced.
