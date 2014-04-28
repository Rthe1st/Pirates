Pirates
=======
Un-neatened areas:
Database
Tutorials
Map loading (from xml)
Interface

Structure:
TileView is a custom view which the main game map is drawn on
LevelActivity holds this view
GameLogic interacts with both these, it manges the game state

MapData is inside gameLogic, it holds the actual data on in game obejcts and there states

MapData is mainly made up of a series of HashMaps, 1 per in game object type (ship, enemy, tile, mine)
These are used together to form an implict map by using Cords as the map key
