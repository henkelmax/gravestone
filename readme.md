<!-- modrinth_exclude.start -->

# GraveStone Mod

## Links
- [Modrinth](https://modrinth.com/mod/gravestone-mod)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/gravestone-mod)
- [Credits](https://modrepo.de/minecraft/gravestone/credits)

---

<!-- modrinth_exclude.end -->

## Basic Functionality
Every time you die, a grave is placed at your position.
To retrieve your items just break the grave.
There is also a config option to get your items back by sneaking on the grave (1.16.3+ only).

![](https://i.imgur.com/7CGWKim.png)

When you right-click the grave it will show information regarding the death of the player.

![](https://i.imgur.com/2CtZE7H.png)

## Sorting (1.16.3+)
Since version 1.16.3-2.0.0 the grave is now able to store your items back into their original slots.

![](https://media1.giphy.com/media/em8yzTjuJOxrMQJqBG/giphy.gif)

## The Obituary
Everytime you die you get an obituary. If you don't want that you can disable it in the config.

It contains:

- The name of the player
- The dimension the player died in
- The date the player died
- The coordinates where the player died
- An image of the player with its equipment at the time of death
- A list of every item the player had when he died

If you don't want to keep the obituary after you recovered your items, you can enable the automatic removal in the config.

![](https://i.imgur.com/mc2CMaK.png)

![](https://i.imgur.com/vOVetsl.png)

## Naming the Grave
If you break the grave with silk touch, you get it dropped as an item.
You can also craft graves.

Naming the grave in an anvil, allows you to have custom text displayed on your grave.

![](https://i.imgur.com/fB6gl6Y.png)

![](https://i.imgur.com/auVMOS1.png)

## Customization
There are several things you can change in the configs:

- The color of the text on the grave
- If the skull on the grave should be rendered
- Whether you want to get the obituary on death
- The blocks that can get replaced by a grave
- If the death note should get removed form the players inventory when breaking a grave
- If only the owners of the grave should be able to break it
- If a ghost of the player should spawn when breaking the grave
- If the ghost should be friendly and defend the player or if it should attack the player
- If the grave should get broken when sneaking on the grave
- If the items should get sorted back into their original slots when breaking the grave

## Recovering Lost Items (1.16.3+)
If you somehow lost your items you can recover them with the recover command.

The syntax is `/restore <player> <death_id> <replace|add>`.

The parameter `player` is the player whose inventory should get restored.

The parameter `death_id` is the ID of the death. You can find it out by enabling advanced tooltips (F3 + H) and opening the obituary. The death ID also gets written into the logs if a grave couldn't get placed.

The last parameter defines if you want to get the players inventory replaced (Overwritten) or added (Just adds the items to the players existing inventory).


You can also get a pre made restore command by sneaking and right-clicking the obituary as an admin.


## Edge Cases
If you die inside a block, the grave is placed at the next empty space above your location. If there is no free block above your position, or you are above the build limit, your items drop as usual.

If you fall into the void, the grave is placed at the lowest point in the world where blocks can be placed. If there is no free space it will get placed at the next free spot above.
