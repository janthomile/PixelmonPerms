# PixelmonPerms
Helper mod that restricts parts of the Pixelmon mod based on permission nodes. Made for forge 1.16.5.

## Features
* Restrict NPC interactions and Trainer battles based on permission nodes!
* Add dialogues that show when a permission node isn't met!
* Add commands to execute when a permission node isn't met!

## Commands
Base command is `/pixelmonperms` which should be followed by `get`, `set`, or `remove`.

`get` can be followed by:
* `message` to show an indexed list of cancel messages.
* `permission` to show an indexed list of required permissions.
* `failcommand` to show an indexed list of fail-commands.

`set` can be followed by:
* `message` to add a cancel message to the npc's list.
* `permission` to add a permission node to the npc's list.
* `failcommand` to add a fail-command to the npc's list.

`remove` can be followed by:
* `message`
* `permission`
* `failcommand`

followed by an whole-number `index` (seen through the `get` subcommand) to remove an entry from any of the above lists.

Cancel messages and fail commands show/execute under the same condition, being when a player does not have required permissions as set on an NPC.
Cancel messages show a pixelmon npc dialogue.
Fail commands execute as top-level commands focused on the interacting player.
