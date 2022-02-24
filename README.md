# **Bingo**- *Reloaded* 1.0.0
A Minecraft 1.18 Spigot Plug-in

***
Thanks for looking at my Bingo Plug-in!

This plug-in is written for Minecraft version 1.18 and up!

This plug-in is intended for use in closed groups, i.e. with your friends on your own server. 

To get started, use the `/bingo` command, after which a menu will open taht you can use to change the settings. To actually start the game, use the command `/bingo start`. To end a game prematurely, the `/bingo end` command can be used. Finally if during the game your Bingo card has been lost, use `/bingo getcard` to get it back!

# Features:
- Play in ***3*** different gamemodes!
  - Regular
  - Lockout
  - Complete-all
- Choose between ***4*** seperate kits!
  - Hardcore
  - Normal
  - Overpowered
  - Reloaded
- Choose between a card size of 3x3 or 5x5 for quick or longer games!
- Choose between 16 teams, based on Minecraft's beloved colors!
- Create your own BingoCards using the card editor! By default it will use a seperate list (that can be edited if you wanted to), containing 150 unique items from the game.
- Due to it's teleportation at the start, it's a great way to explore the new terrain generation that 1.18 brought to the game!
- Resume games that have been paused by the server or plug-in crashing or after having been reloaded plug-in due to the automatic card recovery system!
- Team Chat! you can use `/btc` to toggle team chat on or off when a game is in progress.


## Play bingo in 3 Different gamemodes
This plugin has 3 seperate ways to play bingo with your friends.
- **Regular**, this is the bingo everyone knows about. Be the first to complete 1 line going horizontally, vertically or diagonally to win the game!
- **Lockout**. Be the first to complete the majority of the card to win. But there is a catch, items that other teams have gathered cannot be used again!
- **Complete-All**. Be the first one to complete all items on the card. Complete-all games can be pretty long if you have made a hard card!


## Choose betreen 4 seperate kits
The game host can choose a kit that will be used by all participants.
- **Hardcore**. Some people might call it the classic or the only way to play bingo. This kit gives you nothing but the Card!
- **Normal**. A slightly more forgiving kit giving you iron tools and some food.
- **Overpowered**. Your tools are upgraded to Netherite! You also get access to the Go-Up-Wand!
- **Reloaded**. Same as overpowered but you also get an Elytra that (practically) doesn't break!


### The Go-Up-Wand
This items allows the user to teleport themselves high into the sky when right-clicked. this item can be used to get a better view of your current area or to get out of caves quickly. But be warned, there is a 5 second cooldown on using it! This item makes firework rockets obsolete when using an elytra.


## Create your own BingoCards using the card editor!
This plugin includes a way to easily create new cards without messing around in the configuration files. 
Using the commands `/card` and `/itemlist` you can assemble your own card configurations and then directly use them in the next game!

When creating a card using `/card create <name>` a menu will come up where you can add item lists to the card. Each list has a maximum number of times an item from it can appear on the card. This will allow you to have varying rarity between the item lists you create. 
If all the numbers on the added itemlists don't add up to amount of slots on the card (i.e. 25 for a 5x5 card) the rest will be filled up from items in the default list.

To add new Item lists, use the `/itemlist create <name>` command. In the list editor you can pick from every block in the game with one exception being stained glass panes. Make sure the items you select will be available during the game or no one can ever win :P.


***
**NOTE:** *As of version 1.0.0 the plug-in does not really use permissions so be carefull messing around with it on public servers, as anyone could change the bingo settings! Also as of version 1.0.0 the plug-in lacks a *

**NOTE(2):** *If you do want to use this plug-in on your public server but don't know how to update the code that's available or are unsure of it's 'inner workings', please contact me!!*

**NOTE(3):** *I am a professional programmer, but this is the first time I have made a Spigot plug-in on my own. If you are a technical reader and notice something out of practise in my code or if you want to contribute, feel free to contact me!*