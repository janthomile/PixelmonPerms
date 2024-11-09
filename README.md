# PixelmonPerms
Helper mod that restricts parts of the Pixelmon mod based on permission nodes. Made for forge 1.16.5.

## Features
* Restrict NPC interactions and Trainer battles based on permission nodes and conditions!
* Add dialogues that show when a permission node isn't met!
* Add commands to execute when a permission node isn't met!

## Commands
Base command is `/pixelmonperms` which has primary subcommands `entry`, `reformat`, `legacy`, or `duplicatenpc`.
Commands that target NPCs do so by picking whichever one the player is looking at, with a maximum range of 16 blocks.

`entry` subcommands:
(`entry` selects the currently targeted NPC)
* `list` to show an indexed list of entries.
* `swap` followed by two indexes to swap their position in the list.
* `create` to create an empty entry at the end of the list.
* `remove` to show an indexed list of fail-commands.

* `<index>` as a number index to select an index, which then has the following subcommands:

`entry <index>` subcommands:
(`<property>` can be `eval`, `permission`, `message`, or `command`)
* `get <property>` to get that property's indexed list.
* `apply <property> <value/input>` to append all input to that property's list.
* `remove <property> <index>` to remove the index of that property's list.

`reformat` subcommands:
* `npc <overwrite>` to convert legacy format into the new entry format on the targeted NPC. If `overwrite` is false, the NPC will be ignored if it already has an Entry in the new format.
* `sweepRefactor <overwrite>` does the same function as above, but to all currently loaded NPCs. `overwrite` acts the same as above.
* `sweepClearLegacy` to clear legacy format. WILL NOT WORK IF THE NPC DOES DOES NOT HAVE NEW ENTRY DATA!

`legacy` subcommands represent the old legacy format and will be deprecated. For a clear list of them and their function see LEGACYGUIDE.md.

`duplicatenpc` duplicates the target NPC and its NBT data (with a new UUID). Primarily for testing purposes.

## Logic Examples
Entries are evaluated in index order from 0 onward. Thusly, 
The EVAL property of an Entry determines the conditional operator for the permission list.
Only the first valid Entry which applies to the interacting player is used.
For example, imagine the following list of entries:
[
0 EVAL NOT, PERMISSION "node.block", MESSAGE "go away!"
1 EVAL AND, PERMISSION "node.followup", MESSAGE "see you again!"
]
The first Entry would block normal interaction until the player has the permission node, "node.block", after which the NPC's base interactions will take precedence.
Later, if the player gains the node, "node.followup", since the first entry no longer applies, the second Entry will take predecence over base NPC interactions.