# canvas [![Build Status](https://travis-ci.org/IPVP-MC/canvas.svg?branch=master)](https://travis-ci.org/IPVP-MC/canvas)

A highly advanced and effective inventory management library for Bukkit plugins. The primary goal of canvas is to enable creation of elegant inventory systems without the quirks of existing libraries.

## Feature Overview
* [Menus](#menus) - the basics of GUI creation
    * [Close Handlers](#close-handlers) - handling close behavior
    * [Redrawing](#redrawing) - preventing cursor position resets
    * [Pagination](#pagination) - menu pages made easy
* [Slots](#slots) - controlling what GUI slots do
* [Templates](#templates) - rendering non-static items on a per-player basis
* [Masks](#masks) - inventory slot IDs made easy!
    * [Recipe Masks](#recipe-masks) - multiple item masks

## Using canvas

canvas is integrated into plugins through the use of Maven.

#### Requirements
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven 3](http://maven.apache.org/download.html)
* [Git](https://git-scm.com/downloads)

Then use the following command to install canvas to your local maven repository
```
git clone https://github.com/IPVP-MC/canvas.git
cd canvas/
mvn clean install
```

You will now be able to add canvas as a dependency in your pom.xml files with the following
```xml
<dependency>
    <groupId>org.ipvp</groupId>
    <artifactId>canvas</artifactId>
    <version>1.7.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

**Note**: You will need to use the Maven shade plugin in order to package your final `.jar` file. Add the following to your maven plugins section:
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.4</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>   
```
see [here](https://maven.apache.org/plugins/maven-shade-plugin/) for additional documentation on the shade plugin.

Once the dependency is registered, the only thing left to do is to register the [MenuFunctionListener](src/main/java/org/ipvp/canvas/MenuFunctionListener.java) with the Bukkit event dispatcher.

```java
Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), plugin);
```

## Features

### Menus
Out of the box, canvas supports the following inventory types as menus:
* [Chest menus](src/main/java/org/ipvp/canvas/type/ChestMenu.java)
* [Hopper menus](src/main/java/org/ipvp/canvas/type/HopperMenu.java)
* [Box menus](src/main/java/org/ipvp/canvas/type/BoxMenu.java) (ie. 3x3 inventories such as workbench, dispenser, dropper)
  * Note: Due to an error in internal Minecraft code, shift clicking is disabled in Hopper and Box menus.

The above menus can be created by using the Builder pattern available in their respective classes. Creating a standard [ChestMenu](src/main/java/org/ipvp/canvas/type/ChestMenu.java) with 4 rows would look as such:

```java
public Menu createMenu() {
    return ChestMenu.builder(4)
            .title("Menu")
            .build();
}
```

Displaying the menu to a player is made simple with `Menu#open(Player)`
```java
public void displayMenu(Player player) {
    Menu menu = createMenu();
    menu.open(player);
}
```

Simple yet effective, our result looks like this:

![](http://i.imgur.com/LXnCkLv.png)

#### Close handlers
Functionality when a Menu is closed can be added to Menus through the Menu.CloseHandler interface. The interface is meant to be used as a functional interface, and functionality is added elegantly with Java 8 lambda expressions.

Let's say we want to send the player some messages when they leave the inventory:
```java
public void addCloseHandler(Menu menu) {
    menu.setCloseHandler((player, menu1) -> {
            player.sendMessage("You just closed the menu...");
            player.sendMessage("See you next time!");
    });
}
```

#### Redrawing
When switching between different Menus for players, by default the cursor will reset to the middle of the screen. 
This behavior can be changed to preserve cursor location at the cost of not being able to update Menu titles by enabling
the `redraw` property of a Menu. When building a Menu via `MenuBuilder`, passing a value of `true` to `MenuBuilder#redraw(boolean)` 
enabled this functionality.

**Note**: If switching to a menu that has different dimensions, the `redraw` flag will be ignored and a new Inventory will
be opened for the player, resetting their cursor.

#### Pagination
Creating connected pages of Menus to display a catalog of items is made easy with the 
[PaginatedMenuBuilder](src/main/java/org/ipvp/canvas/paginate/PaginatedMenuBuilder.java) class. The utility is able to be
configured to set the proper previous and next page icons, and any necessary functions for items that are added.

In the basic example below, we create a simple menu displaying various static items.
```java
Menu.Builder pageTemplate = ChestMenu.builder(3).title("Items").redraw(true);
Mask2D itemSlots = Mask2D.builder(pageTemplate.getDimensions())
        .pattern("011111110").build();
List<Menu> pages = PaginatedMenuBuilder.builder(pageTemplate)
        .slots(itemSlots)
        .nextButton(new ItemStack(Material.ARROW))
        .nextButtonEmpty(new ItemStack(Material.ARROW)) // Icon when no next page available
        .nextButtonSlot(23)
        .previousButton(new ItemStack(Material.ARROW))
        .previousButtonEmpty(new ItemStack(Material.ARROW)) // Icon when no previous page available
        .previousButtonSlot(21)
        .addItem(new ItemStack(Material.DIRT))
        .addItem(new ItemStack(Material.GRASS))
        .addItem(new ItemStack(Material.COBBLESTONE))
        .addItem(new ItemStack(Material.STONE))
        // ...
        .build();
```
Per-player items and click handlers are supported for added items as well, via the `PaginatedMenuBuilder.addItem(ItemStackTemplate)`
or `PaginatedMenuBuilder.addItem(SlotSettings)` methods.

If additional modifications need to be made to any newly created page that the builder doesn't support, adding functionality 
to modify a freshly created page is available by adding a `Consumer<Menu>` with the `PaginatedMenuBuilder.newMenuModifier(Consumer<Menu>)` method. 

### Slots
A [Slot](src/main/java/org/ipvp/canvas/slot/Slot.java) is exactly what you'd expect it to be, however canvas allows 
incredible customization of what they can do. Menus grant access to their slots through the `Menu#getSlot(int)` method.

There are 3 major pieces to Slot functionality:
* [ClickOptions](src/main/java/org/ipvp/canvas/slot/ClickOptions.java)
* [ClickInformation](src/main/java/org/ipvp/canvas/ClickInformation.java)
* [ClickHandler](src/main/java/org/ipvp/canvas/slot/Slot.java)

Additionally, Slots grant the ability to render non-static items within their parent Menu via 
[ItemStackTemplate](src/main/java/org/ipvp/canvas/template/ItemStackTemplate.java) (see below).

#### ClickOptions
Click options are the primary method of controlling what actions and click types can be performed on the raw item contents of the holding inventory. Two basic sets are provided with the library, which are `ClickOptions.ALLOW_ALL` and `ClickOptions.DENY_ALL`. By default, slots carry the DENY_ALL trait, denying all pickup and dropping off of items in the respective inventory. These behaviors are easily modified with the `Slot#setClickOptions(ClickOptions)` method.

Creation of custom options is done through the `ClickOptions.Builder` class. In the following example, we show you how to only allow dropping off of items into a specific slot, but not picking it up.

```java
public void addClickOptions(Slot slot) {
    ClickOptions options = ClickOptions.builder()
            .allow(ClickType.LEFT, ClickType.RIGHT)
            .allow(InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME)
            .build();
    slot.setClickOptions(options);
}
```

#### ClickInformation
[ClickInformation](src/main/java/org/ipvp/canvas/ClickInformation.java) is a class constructed to provide the ClickHandler of a Slot with all available information about a click performed on the Slot. Also available is the possibility to change the resulting outcome of the click (whether interaction in the raw inventory occurs).

#### ClickHandler
Click handlers are where most of the logic of a slot will occur. As a slot is clicked, the click handler (if present) is triggered with information about who clicked as well as the click performed. The handler of a slot will always be triggered, regardless of whether or not the options of a slot forbid interaction with it. Keep in mind that the result of the click will be set by the options before the handler is triggered and as such the ClickInformation will represent this result.

Adding a handler is made simple with `Slot#setClickHandler(ClickHandler)`:

```java
public void addClickHandler(Slot slot) {
    slot.setClickHandler((player, info) -> {
        player.sendMessage("You clicked the slot at index " + info.getClickedSlot().getIndex());
        // Additional functionality goes here
    });
}
```

### Templates
Item templates are used to render non-static items on a per-player basis. In certain situations, users of canvas may
require a Menu to be updated because state has changed. For example, if an icon in a Menu displays the level of a player
and the player has levelled up then the icon must be redrawn to reflect the changes. Item templates allow this functionality
to happen without requiring a completely new Menu for each player.

Item templates are assigned to slots via the `Slot#setItemTemplate(ItemStackTemplate)` method. Alternatively,  
`Slot#setItem(ItemStack)` can be used to set a static item that will render the same for every player.

Using the scenario above, where a player may be levelling up, code for an item may look something like the following:
```java
Menu menu = ChestMenu.builder(1).title("Level").build();
Slot slot = menu.getSlot(4);
slot.setItemTemplate(p -> {
    int level = p.getLevel();
    ItemStack item = new ItemStack(Material.EXP_BOTTLE);
    ItemMeta itemMeta = item.getItemMeta();
    itemMeta.setDisplayName("Level: " + level);
    item.setItemMeta(itemMeta);
    return item;
});
```
With the item template set in place, every time the Menu is updated for the player using `Menu.update(Player)`, the EXP bottle 
will be updated with the players current level and will be rendered in the inventory the player has open. 

### Masks
Masks create a layer of abstraction over raw inventory slot IDs. Through the usage of masks, populating specific slots inside an inventory has never been easier. Let's start with an example.

Suppose we begin with the previously created menu:

![](http://i.imgur.com/LXnCkLv.png)

If we wanted to create a basic border of white glass on the outer slots, we would normally have to figure out which 
values reference those slots. These 22 slot IDs are 0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 
34, 35. If we didn't already store these values inside an `int[]` array, some general code might look something like this:

```java
public void addWhiteBorder(Menu menu) {
    ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
    for (int i = 0 ; i < 9 ; i++) { // Setting first row
        menu.getSlot(i).setItem(glass);
    }
    menu.getSlot(17).setItem(glass);
    menu.getSlot(18).setItem(glass);
    for (int i = 26 ; i < 36 ; i++) {
        menu.getSlot(i).setItem(glass);
    }
}
```

As you can see, the code is fairly unintuitive and not at all friendly for refactoring or bug fixing and it only gets 
worse as the inventory size grows or we want to add more slots. What if we _accidentally_ missed or forgot a slot id? 
Finding it would be a nuisance! For your benefit (or not) we've purposely excluded a single slot in the above example 
so that our inventory would have a gaping hole, feel free to see how long it would take to find the missing number.

Here is where Masks come in play. For the above inventory, masking the slots is made simple using a 
[BinaryMask](src/main/java/org/ipvp/canvas/mask/BinaryMask.java).

```java
public void addWhiteBorder(Inventory inventory) {
    Mask mask = BinaryMask.builder(menu)
                    .item(new ItemStack(Material.STAINED_GLASS_PANE))
                    .pattern("111111111") // First row
                    .pattern("100000001") // Second row
                    .pattern("100000001") // Third row
                    .pattern("111111111").build(); // Fourth row
    mask.apply(menu);
}
```

Masks provide an incredibly simple interface for labelling slots. In the case of 
[BinaryMask](src/main/java/org/ipvp/canvas/mask/BinaryMask.java), each character represents a boolean value of whether or 
not the slot should be selected. A character value of '1' represents yes and all other characters the opposite. 
This model provides a semi-visual view of what the inventory will look like and is easy to add or remove specific slots.

The final product we end up with is:

![](http://i.imgur.com/BHt65l6.png)

#### Recipe Masks
A [RecipeMask](src/main/java/org/ipvp/canvas/mask/RecipeMask.java) is another abstraction of mask which enabled assigning
multiple types of items to slots. In the above image suppose we want every second item to be a red stained glass pane, we
could use a RecipeMask in the following way:
```java
public void addRedWhiteBorder(Inventory inventory) {
    Mask mask = RecipeMask.builder(menu)
            .pattern("wrwrwrwrw") // First row
            .pattern("r0000000r") // Second row
            .pattern("w0000000w") // Third row
            .pattern("rwrwrwrwr").build(); // Fourth row
    mask.apply(menu);
}
```
In the above, we use the `w` and `r` characters in the pattern wherever we want and then assign an item
to that character. If no item is assigned, the character will default to `AIR` in the final product.   

The final product we end up with is:

![](https://i.imgur.com/eWU3BuG.png)

## License
canvas is open source and is available under the [MIT license](LICENSE.txt).