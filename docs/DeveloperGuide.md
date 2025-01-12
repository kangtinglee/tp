---
layout: page
title: Developer Guide
---
- [**Setting up, getting started**](#--setting-up--getting-started--)
- [**Design**](#--design--)
    * [Architecture](#architecture)
    * [UI component](#ui-component)
    * [Logic component](#logic-component)
    * [Model component](#model-component)
    * [Storage component](#storage-component)
    * [Common classes](#common-classes)
- [**Implementation**](#--implementation--)
    * [Design enhancements](#design-enhancements)
        + [Model update](#model-update)
        + [Storage update](#storage-update)
        + [Component Parser](#component-parser)
        + [Parser validation](#parser-validation)
        + [Data consistency](#data-consistency)
            - [Deletion of Person objects](#deletion-of-person-objects)
            - [Editiing of Person objects](#editing-of-person-objects)
            - [Deletion of Dish objects](#deletion-of-dish-objects)
            - [Editiing of Dish objects](#editing-of-dish-objects)
            - [Deletion of Ingredient objects](#deletion-of-ingredient-objects)
            - [Editing of Order objects](#editing-of-order-objects)  
            - [Logging of Order object](#logging-of-order-object)
        + [Concurrent list display](#concurrent-list-display)
        + [Data archiving](#data-archiving)
    * [Command enhancements](#command-enhancements)
        + [Add command](#add-command)
        + [Delete command](#delete-command)
        + [List command](#list-command)
        + [Find command](#find-command)
        + [Edit command](#edit-command)
    * [New Commands](#new-commands)
        + [order complete command](#order-complete-command)
        + [order history command](#order-history-command)
        
- [**Documentation, logging, testing, configuration, dev-ops**](#--documentation--logging--testing--configuration--dev-ops--)
- [**Appendix: Requirements**](#--appendix--requirements--)
    * [Product scope](#product-scope)
    * [User stories](#user-stories)
    * [Use cases](#use-cases)
    * [Non-Functional Requirements](#non-functional-requirements)
    * [Glossary](#glossary)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

![AD](images/ArchitectureDiagram.png) 

The ***Architecture Diagram*** given above explains the high-level design of the App. Given below is a quick overview of each component.

> **Tip:** The `.puml` files used to create diagrams in this document can be found in the [diagrams](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/docs/diagrams) folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.

**`Main`** has two classes called [`Main`](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/src/main/java/seedu/address/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

The rest of the App consists of four components.

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

Each of the four components,

* defines its *API* in an `interface` with the same name as the Component.
* exposes its functionality using a concrete `{Component Name}Manager` class (which implements the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component (see the class diagram given below) defines its API in the `Logic.java` interface and exposes its functionality using the `LogicManager.java` class which implements the `Logic` interface.

![Class Diagram of the Logic Component](images/ComponentsClassDiagram.png)

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `customer delete 1`.

![ArchitectureSequenceDiagram](images/ArchitectureSequenceDiagram.png)

The sections below give more details of each component.

### UI component

![Structure of the UI Component](images/UiClassDiagram.png)

**API** :
[`Ui.java`](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts, e.g. `CommandBox`, `ResultDisplay`, `ComponentListPanel`, `StatusBarFooter`, etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class.

The `UI` component uses the JavaFX UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* Executes user commands using the `Logic` component.
* Listens for changes to `Model` data so that the UI can be updated with the modified data.

### Logic component

![Structure of the Logic Component](images/LogicClassDiagram.png)
(Note: ComponentXYZCommand refers to a type of command within a component, such as the CustomerAddCommand, MenuAddCommand, OrderDeleteCommand, etc.)  

**API** :
[`Logic.java`](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

1. `Logic` uses the `JJIMYParser` class to differentiate the user command's component and parse the arguments to respective `ComponentParser` (e.g. `MenuParser`, `OrderPaser`, etc).
1. `ComponentParser` will then creates respective `ComponentXYZCommand`.
1. This results in a `Command` object which is executed by the `LogicManager`.
1. The command execution can affect the `Model` (e.g. adding a customer).
1. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.
1. In addition, the `CommandResult` object can also instruct the `Ui` to perform certain actions, such as displaying help to the user.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("delete 1")` API call.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

### Model component

![Structure of the Model Component](images/ModelClassDiagram.png)

**API** : [`Model.java`](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/src/main/java/seedu/address/model/Model.java)

The `Model`,

* stores a `UserPref` object that represents the user’s preferences.
* stores all the components' book data.
* exposes an unmodifiable `ObservableList<T extends Item>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.


### Storage component

![Structure of the Storage Component](images/StorageClassDiagram.png)

**API** : [`Storage.java`](https://github.com/AY2021S2-CS2103T-W15-3/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

The `Storage` component,
* can save `UserPref` objects in json format and read it back.
* can save all the components' book data in json format and read it back.

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Design enhancements

#### Model update

The model has been updated to contain new classes for the `menu`, `inventory`, and `order` components (`Dish`, `Ingredient`, and `Order` classes respectively), in addition to the original `Customer` class for the `customer` component.

`Person` class has had field classes `Phone`, `Address` and `Email` removed. `Tag` has also been replaced with `String` instead. The validation functionality will be moved to other classes.

`UniquePersonList` has been adapted to `UniqueItemList<T>`, with every model class implementing the `Item` interface.

Every component has its own `Book` class which uses `UniqueItemList<T>`, which have thgir functionality exposed through the `ModelManager` class (Facade pattern).

#### Storage update

The storage has been updated to handle the new classes and their relevant `Book` classes. 

Sample data has also been added for each book. JSON serializability of each class is ensured via the use of `Jackson` annotations.

No `JsonAdaptedPerson` or similar classes are used. Instead, the model class is directly annotated for deserialization.

#### Component Parser

The `ComponentParser` mechanism is facilitated by `JJIMYParser` with a general command input format of

    [component] [command] [arguments]

`JJIMYParser` will read in the first word of the input which is the `[component]` and pass the command on to the respective component parser (one of `CustomerParser`, `MenuParser`, `OrderParser` and `InventoryParser`) that implements `ComponentParser`. The component word is stripped by `JJIMYParser`, so the relevant `ComponentParser` receives an input format of

    [command] [arguments]

Finally, the respective `ComponentParser` will read in the `[command]` and return their respective `ComponentCommand` to be executed by `LogicManager`.

The following sequence diagram shows how a `CustomerAddCommand` operation is parsed and executed.
![Sequence diagram for a CustomerAddCommand](images/JJIMYParserSequenceDiagram.png)

The following activity diagram summarizes what happens when a user executes a new command.
![Activity diagram for a new command](images/JJIMYParserActivityDiagram.png)

#### Parser validation
Validation of fields through regex are moved out of the model classes and into their own classes.

Apart from regex, validation is also done through looking up of the model to ensure that no invalid links to other model classes (E.g. an order linking to a non-existing Dish) happens.

#### Data consistency

Given that in JJIMY, all objects are immutable and store local copies of other objects, to ensure data consistency, 
`delete` and `edit` functions need to proper data links that get triggered when they are called.  For instance, when an
order is logged, the `Person` object that represents the customer is duplicated and this new copy stored within the new
`Order` object. Therefore, simply editing the `Person` object in `PersonBook` is not enough to result in changes being
propagated across other parts of the programme such as `OrderBook` because each `Order` holds its own local copy of 
`Person` instead of a reference to a global copy of `Person` for a given customer.

##### Deletion of Person objects

When a `Person` is deleted from the model, all `Order`s related to that `Person` should also be deleted, since that 
`Person` no longer exists. This is illustrated in the following sequence diagram:

![Diagram showing example of cascading deletion](images/CascadingDeletionCustomers.png)

As seen from the above sequence diagram, when `deletePerson` is called on `ModelManager`, it first deletes the `Person`
from `PersonBook`. Next, `ModelManager::getOrdersFromPerson` is used to gather all `Order` objects that belong to that
`Person` object. `getOrdersFromPerson` functions by iterating over all `Order` objects within `Order` book and
checking if they belong to the `Person` object using `Order::isFromCustomer` which returns `true` if the `Order` is 
associated with the `Person` object and `false` otherwise. Those orders that are by the customer are then deleted
with `deleteOrder`.

Note that because of the cascading effects of the `customer delete INDEX` operation, in the event that there are `Order`
objects that will be deleted that are yet to be completed (ie the state of the order is `UNCOMPLETED`), then the user is
required to append `-f` to the end of the delete command to acknowledge this behavior. This is to prevent users from
unknowingly deleting orders that are still pending, resulting in pontential customer dissatisfaction.

##### Editing of Person objects

When a `Person` has its attribute edited via the `customer edit ...` command, there is a need to replace all instances
of the `Person` object within `OrderBook` as well. This is because each `Order` holds its own local and immutable copy 
of `Person` objects. Hence, it is not enough to edit the copy within `OrderBook` only.

```
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        personBook.setPerson(target, editedPerson);
        List<Order> ordersFromTarget = getOrdersFromPerson(target);
        for (Order orderFromTarget : ordersFromTarget) {
            Order updatedOrder = orderFromTarget.updateCustomer(editedPerson);
            setOrder(orderFromTarget, updatedOrder);
        }
    }
```
To do so, we use the `setPerson` method as shown above. `target` refers to the old copy that is to be replaced with
`editedPerson` which is identical to `target` except with certain attributes edited, where relevant. The method begins
with `personBook::setPerson` which replaces the `target` within `PersonBook` with `editedPerson`.
Next, all `Order` objects that belong to the `target` are gathered and for each of them, an updated `Order` object 
(`updatedOrder`) is created but with the `customer` attribute replaced with this new `editedPerson` object. Finally,
`updatedOrder` object is used to replace `orderFromTarget` within `OrderBook` via `setOrder`.

##### Deletion of Dish objects

When `Dish` objects are deleted, all orders that contain the particular dish are marked as cancelled. This is because
without the item available, those orders can no longer be fulfilled and hence, are cancelled. 

```
    Dish dishToDelete = lastShownList.get(targetIndex.getZeroBased());

    List<Order> outstandingOrders = model.getIncompleteOrdersContainingDish(dishToDelete);
    boolean isOutstandingOrders = !outstandingOrders.isEmpty();

    ...

    model.cancelOrders(outstandingOrders);
    model.deleteDish(dishToDelete);
```

The above code block is taken from `MenuDeleteCommand::execute` and contains the main logic behind the deletion of `Dish`
objects. All outstanding orders are first fetched via `Model::getIncompleteOrdersContainingDish` which returns a list
of `Order` objects that contain the affected `Dish` and is also `UNCOMPLETED`. Then, all of those orders are cancelled
using `Model::cancelOrders` which marks all the outstanding orders as `CANCELLED`. Finally, the `Dish` is deleted from
`DishBook` itself.

The `-f` flag is required for the same reason as the deletion of `Person` objects.

##### Editing of Dish objects

When `Dish` objects are edited, extra care is needed to ensure that the currently uncompleted orders, if replaced with 
these edited `Dish` objects, are able to fulfill themselves given the inventory. The mechanism for doing so is as follows

![Activity diagram for editing of Dish objects](images/EditDishObject.png)

As seen in the sequence diagram above, all uncompleted orders containing the dish to be edited are first gathered and
deleted from the database. Then, new orders are generated with the new `Dish` object containing the edits. With this
set of edited `Order` objects, check to see if there is currently sufficient inventory to fulfil these orders. If there
is sufficient inventory, then these new updated `Order` objects which contain the new updated `Dish` object are added
to the class. If there is insufficient inventory, then the old `Order` objects are inserted back into the database and
the edited `Order` object is not committed.

##### Deletion of Ingredient objects

Another key instance of data consistency occurs between the `Ingredient` and `Dish` classes. The deletion of an 
`Ingredient` object affects all the `Dish` objects that use that ingredient and hence, those `Dish` objects
will also be removed. This is because without the `Ingredient` in the inventory, there is no way that those `Dish` objects
that utilize the ingredients can be produced, hence, there is a cascading deletion of `Dish` objects when `Ingredients`
are deleted. The cascading deletion of `Dish` then triggers the cascading 

For this command, the `-f` flag is also required for the user to acknowledge this cascading deletion behavior. This is
to prevent the user from unknowingly removing items from the menu.

##### Editing of Order objects

When orders are edited, there is a need to ensure that given the new dishes and quantities, that there is still sufficient
inventory to produce the new order. Hence, when an `Order` is edited, old copy of `Order` is removed from the `OrderBook`
and the new `Order` object is attempted to be inserted as those it were a new order with accompanying checks that the
new order can be fulfilled. In the event that this new `Order` cannot be fulfilled given the inventory, then the old
copy of `Order` is reinserted and an error message is displayed to the user.

##### Logging of Order objects

Data consistency extends beyond deletion. When `Order` objects are created, the `Ingredient`s and their quantities are tabulated from the `Dish`es and their respective quantities. 
The quantity of each `Ingredient` is then decremented by the corresponding amount. 
This automated data link ensures that the restaurant owner will be notified when they are attempting to place orders for dishes that have insufficient stock to produce.

#### Concurrent list display

To increase the efficiency of adding food orders, the GUI has been improved to display two lists at the same time. The customer list will always be shown on the left column whereas the right column will display one of the other components.
 
Which component list is shown on the right will depend on the component of the last command input; the component of the `CommandResult` will cause `MainWindow` to display the corresponding component's item list. For example, using a `menu add` command will cause the right side to display the menu list, whereas `order add` will show the right side to display the order list. However, using a command on the `customer` component will only update the left list and not affect the right list.

#### Data archiving

Removing `Order` objects from the currently displayed list will not delete them, but to *archive* them for future reference (e.g. accounting purposes). This will be achieved with a `completed` field within each Order object, which will determine whether they are displayed in the currently active order list or in the archived order list.

### Command enhancements

#### Add command

The `add` command is implemented for all four components and can be called from the CLI input with the general form

	[component] add [arguments...]
	
The arguments differ depending on what component the `add` command is being called on. (For more details, see the description of individual `add` commands in the [User Guide](https://ay2021s2-cs2103t-w15-3.github.io/tp/UserGuide.html).) 

For details on how the command is parsed, refer to the explanation in the [Component Parser description](#component-parser). After the command is successfully parsed into an add `Command` object (e.g. `MenuAddCommand`), the `Command` object is executed by the `LogicManager`; the `add` commands' `execute` methods include validation routines to ensure the item to be added is both valid and not a duplicate of an item in the list.

Finally, the `ModelManager` is called to add the item to the relevant `Book`, and a `CommandResult` object is returned, which causes the `MainWindow` to update to display the result. The following sequence diagram shows how the `MainWindow` is updated after a `menu add` command is called by the user. Note that, as detailed in the [concurrent list display description](#concurrent-list-display), the right-hand side of the `MainWindow` will be updated to show the new state of the menu.

![Sequence diagram showing GUI update caused by a MenuAddCommand](images/MenuAddGUI.png)

#### Delete command

The `delete` command is implemented for all four components and can be called from the CLI input with the form

	[component] delete [arguments...]

The argument for the `delete` command is always the (1-indexed) index of the item to be deleted, *as shown in the currently displayed list* .

For details on how the command is parsed, refer to the explanation in the [Component Parser description](#component-parser). After the command is successfully parsed into an delete `Command` object (e.g. `MenuDeleteCommand`), the `Command` object is executed by the `LogicManager`; the `delete` commands' `execute` methods include validation routines to ensure the index selected is a valid index.

Finally, the `ModelManager` is called to delete the item from the relevant `Book`, and a `CommandResult` object is returned, which causes the `MainWindow` to update to display the result.

In some cases, use of the `delete` command may trigger cascading `delete`s on other lists to maintain data consistency. For more information, see the [data consistency section](#data-consistency) of this Developer Guide.

After execution, the GUI's displayed list is updated in a similar fashion to the GUI update caused by the [add command](#add-command).

#### List command

The `list` command is implemented for all four components and can be called from the CLI input with the form

	[component] list

There are no arguments for the `list` command. 

Unlike the other commands, the `list` command has no specific parsers beyond the base component parsers (e.g. `MenuParser`; there is **no** `MenuListParser`), since there are no further arguments to parse. Therefore, the `Command` object is created directly by the base component parser and returned to be executed into a `CommandResult` object. The `CommandResult` is used to update the GUI, as explained in the [concurrent list display description](#concurrent-list-display).

The following sequence diagram shows how the GUI is updated from `MainWindow` after a `menu list` command is called by the user.
![Sequence diagram showing GUI update caused by a MenuListCommand](images/MenuListGUI.png)

In the case of the `order` component, the `list` command will update the `FilteredOrderedList` object to only contain orders that are marked as `UNCOMPLETED` and return a `CommandResult` object to update the GUI.

#### Find command

The `find` command will be implemented for all four components and can be called from the CLI input with the general form

	[component] find prefix/[keyword]

Each prefix specifies which field to search for followed by a keyword or list of keywords to search for.

At least one prefix must be specified or find will throw an exception.

The `find` command will be parsed in a similar way to other commands (see the [Component Parser description](#component-parser)). The `find` command will update the `FilteredList` object to only contain items that match the keywords and return a `CommandResult` object to update the GUI, in a similar fashion to the GUI update caused by the [add command](#add-command).

#### Edit command

The `edit` command will be implemented for all four components and can be called from the CLI input with the general form

	[component] edit prefix/[value]

Similar to the implementation of the `add` command, the arguments will differ depending on what component the `edit` command is being called on.

At least one prefix must be specified or edit will throw an exception.

The `edit` command will be parsed in a similar way to other commands (see the [Component Parser description](#component-parser)). The `edit` command will select an object from the *currently displayed list* via its (1-indexed) index and create a new object with the same parameters, except for the parameters given as arguments to be updated. 

This new object will replace an object in the current book and return a `CommandResult` object to update the GUI from `MainWindow`, in a similar fashion to the GUI update caused by the [add command](#add-command).

### New Comamnds

#### Order complete command

The `order comlete` command can be called from the CLI input with the general form
	
	order complete [argument]

The argument of the `order complete` command will always be the index (1-indexed) of the order to be marked as completed. 

For details on how the command is parsed, refer to the explanation in the [Component Parser description](#component-parser). After the command is successfully parsed into an complete `Command` object (e.g. `OrderCompleteCommand`), the `Command` object is executed by the `LogicManager`; the `order complete` commands' `execute` methods include validation routines to ensure the index selected is a valid index.

Finally, the `ModelManager` is called to replace the order with an altered order - which is identical except it is marked as `COMPLETED` - in `orderBook`, and a `CommandResult` object is returned, which causes the `MainWindow` to update to display the result. The order will no longer appear in `order list` but instead show up in `order history` as a completed order.

#### Order history command

The `order history`  command can be called from the CLI input with the general form

	order history

There are no arguments for the `order history` command. The command is implemented similarly to the list command [List Command description](#list-command).

The command will update the `FilteredOrderedList` object to only contain orders that are marked as `COMPLETED` and `CANCELLED` and return a `CommandResult` object to update the GUI such that all orders that are completed and cancelled are displayed. Notably, `FilteredOrderedList` contains only `UNCOMPLETED` orders for all other component `order` commands. Further elaboration on how the GUI is updated can be found in the [concurrent list display description](#concurrent-list-display).

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of customers, orders, menu items and inventory
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage customers, orders, menu items and inventory faster than a typical mouse/GUI driven app


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a...                        | I want to...                                               | So that I can...                                            |
| -------- | ------------------------------ | ---------------------------------------------------------- | ----------------------------------------------------------- |
| `* * *`  | new user                       | see usage instructions                                     | refer to instructions when I forget how to use the App      |
| `* * *`  | fast typer                     | be able to input by CLI                                    | key in commands faster                                      |
| `* * *`  | restaurant owner               | add a customer's customer                                  | keep track of each customer's details                       |
| `* * *`  | restaurant owner               | remove a customer's customer                               | remove customers who no longer patronize the restaurant     |
| `* * *`  | restaurant owner               | add dishes to the menu                                     | keep track of dishes being offered                          |
| `* * *`  | restaurant owner               | remove dishes from the menu                                | remove dishes that are not being offered anymore            |
| `* * *`  | restaurant owner               | add food orders to the order list                          | keep track of the food I need to prepare                    |
| `* * *`  | restaurant owner               | remove food orders from the order list                     | remove the order if my customers changed their minds        |
| `* * *`  | restaurant owner               | add the ingredients that I have restocked to the inventory | know which ingredients I have in stock                      |
| `* * *`  | restaurant owner               | remove ingredients from the food inventory                 | remove an ingredient I have just used                       |
| `* * *`  | restaurant owner               | view a list of ingredients from the food inventory         | so I know which ingredients I have in stock                 |
| `* * *`  | restaurant owner               | add tasks to my shopping list        			 | so I can remember which items to restock                    |
| `* * *`  | restaurant owner               | remove tasks from my shopping list    		         | so I can remove tasks I don't need anymore                  |
| `* * *`  | restaurant owner               | view all tasks to my shopping list                         | so I can view which items to restock                        |
| `* * *`  | restaurant owner               | add dishes to the menu list                                | so I can keep track of the dishes being offered             |
| `* * *`  | restaurant owner               | remove dishes to the menu list                             | so I can remove dishes that are not being offered anymore   |
| `* * *`  | restaurant owner               | view all dishes to the menu list                           | so I can view all the dishes being offered                  |
| `* * *`  | restaurant owner               | view the list of food orders                               | so I know which dishes to prepare                           |
| `* *`    | restaurant owner               | edit a customer's customer                                 | rectify typos for customer errors                           |
| `* *`    | user with many customers       | find a customer's customer                                 | quickly locate the customer details of a particular customer |
| `* *`    | owner with a large menu        | find a dish on the menu                                    | quickly locate details of a dish on the menu                |
| `* *`    | owner of a busy restaurant     | find a food order from the order list                      | quickly locate the details of an order I'm working on       |
| `* *`    | owner with a complex inventory | find the quantity of an ingredient in the food inventory   | quickly check how much of a certain ingredient I have left  |

### Use cases

**Use case: Request help**

**MSS**

1.  User requests help
2.  JJIMY displays a list of commands

    Use case ends.

**Use case: Exit**

**MSS**

1.  User requests to exit
2.  JJIMY exits

    Use case ends.

**Use case: Add a customer**

**MSS**

1.  User requests to add a customer
2.  JJIMY adds the customer

    Use case ends.

**Extensions**

* 1a. JIMMY detects duplicate

	* 1a1. JIMMY shows an error message

    Use case ends.

* 1b. JIMMY detects that the given arguments are invalid

	* 1b1. JIMMY shows an error message

    Use case ends.

**Use case: List all customers**

**MSS**

1.  User requests to list customers
2.  JJIMY shows a list of customers

    Use case ends.

**Use case: Delete a customer**

**MSS**

1.  User requests to list customers
2.  JJIMY shows a list of customers
3.  User requests to delete a specific customer in the list
4.  JJIMY deletes the customer

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.
    
* 3b. The given contact has pending orders
    
    * 3b1. JJIMY prompts the user to append `-f` to the end of his command to confirm the cascading deletion of pending orders for the conact as well
      
      User case resumes at step 2.

**Use case: Edit a customer**

**MSS**

1.  User requests to list customers
2.  JJIMY shows a list of customers
3.  User requests to edit a customer
4.  JJIMY edits the details of the relevant customer

    Use case ends.

**Extensions**

* 3a. JIMMY detects that the given arguments are invalid

	* 3a1. JIMMY shows an error message

    Use case resumes at step 2.

* 3b. The list is empty.

	Use case ends.

**Use case: Find a customer**

**MSS**

1. User requests to list customers
2. JJIMY shows a list of customers
3. User requests to find customers based on keywords.
4. JJIMY returns a list of matching customers for the keywords.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given keywords do not match any customers.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.

**Use case: Add a menu item**

**MSS**

1.  User requests to add a menu item
2.  JJIMY adds the menu item

    Use case ends.

**Extensions**

* 1a. JIMMY detects duplicate

	* 1a1. JIMMY shows an error message

    Use case ends.

* 1b. JIMMY detects that the given arguments are invalid

	* 1b1. JIMMY shows an error message

    Use case ends.

**Use case: List all menu items**

**MSS**

1.  User requests to list menu items
2.  JJIMY shows a list of menu items

    Use case ends.

**Use case: Delete a menu item from the menu**

**MSS**

1.  User requests to list menu items
2.  JJIMY shows a list of menu items
3.  User requests to delete a specific menu item in the list
4.  JJIMY deletes the menu item

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.
    
* 3b. The given menu item has pending orders

    * 3b1. JJIMY prompts the user to append `-f` to the end of his command to confirm the cascading cancelation of pending 
      orders that contain the menu item

      User case resumes at step 2.

**Use case: Edit a menu item**

**MSS**

1.  User requests to list menu items
2.  JJIMY shows a list of menu items
3.  User requests to edit a menu item
4.  JJIMY edits the details of the relevant menu item

    Use case ends.

**Extensions**

* 3a. JJIMY detects that the given arguments are invalid

	* 3a1. JJIMY shows an error message

    Use case resumes at step 2.

* 3b. The list is empty.

	Use case ends.

* 3c. JJIMY detects that with this change, the pending orders that contain the edited item can no longer be fulfilled with current inventory
    
    * 3c1. JJIMY shows an error message
    
    Use case resumes at step 2.


**Use case: Find a menu item**

**MSS**

1. User requests to list menu items
2. JJIMY shows a list of menu items
3. User requests to find menu items based on keywords.
4. JJIMY returns a list of matching menu items for the keywords.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given keywords do not match any menu item.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.

**Use case: Add an order**

**MSS**

1.  User requests to add an order
2.  JJIMY adds the order

    Use case ends.

**Extensions**

* 1a. JIMMY detects duplicate

	* 1a1. JIMMY shows an error message

    Use case ends.

**Use case: List all orders**

**MSS**

1.  User requests to list orders
2.  JJIMY shows a list of orders

    Use case ends.

**Use case: Delete an order**

**MSS**

1.  User requests to list orders
2.  JJIMY shows a list of orders
3.  User requests to delete a specific order in the list
4.  JJIMY deletes the order

    Use case ends.
    
**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.
    
**Use case: Complete an order**

1. User requests to list orders
2. JJIMY shows a list of orders
3. User requests to mark a specific order as completed
4. JJIMY moves the order from order list to order history and marks the order as completed.

**Extensions**

* 2a. The list is empty.

  Use case ends.
  
* 3a. The given index is invalid.

	* 3a1. JJIMY shows an error message.

	Use case resumes at step 2.

**Use case: Edit an order**

**MSS**

1.  User requests to list orders
2.  JJIMY shows a list of orders
3.  User requests to edit an order
4.  JJIMY edits the details of the relevant order

    Use case ends.

**Extensions**

* 3a. JJIMY detects that the given arguments are invalid

	* 3a1. JJIMY shows an error message

    Use case resumes at step 2.

* 3b. The list is empty.

	Use case ends.

* 3c. JJIMY detects that there are insufficient ingredients to fulfil the order given this edit
    
    Use case resumes at step 2.

**Use case: Show order history***

1. User requests to see order history
2. History of orders is shown. Orders are either completed or cancelled.

**Extensions**

* 1a. The history is empty.

  Use case ends.
  
**Use case: Find an order**

**MSS**

1. User requests to list orders
2. JJIMY shows a list of orders
3. User requests to find orders based on keywords.
4. JJIMY returns a list of matching orders for the keywords.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given keywords do not match any order.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.

**Use case: Add an inventory item**

**MSS**

1.  User requests to add an inventory item
2.  If the quantity is 0, JJIMY adds a new ingredient, otherwise it increments the quantity

    Use case ends.

**Use case: List all inventory items**

**MSS**

1.  User requests to list all inventory items
2.  JJIMY shows a list of all inventory items

    Use case ends.

**Use case: Delete an inventory item**

**MSS**

1.  User requests to list all inventory items
2.  JJIMY shows a list of all inventory items
3.  User requests to delete a specific inventory item in the list
4.  JJIMY deletes the inventory item

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.

* 3b. The given inventory item has pending orders

    * 3b1. JJIMY prompts the user to append `-f` to the end of his command to confirm the cascading cancellation of 
      pending orders that have dishes that use the ingredient and also the deletion of those affected dishes.

      User case resumes at step 2.
    
**Use case: Decrease the quantity of an inventory item**

**MSS**

1. User requests to list all inventory items
2.  JJIMY shows a list of all inventory items
3.  User requests to decrease the quantity of a specific inventory item in the list
4.  JJIMY decreases the quantity inventory item

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.

**Use case: Edit an inventory item**

**MSS**

1.  User requests to list inventory items
2.  JJIMY shows a list of inventory items
3.  User requests to edit an inventory item
4.  JJIMY edits the details of the relevant inventory item

    Use case ends.

**Extensions**

* 3a. JJIMY detects that the given arguments are invalid

	* 3a1. JJIMY shows an error message

    Use case resumes at step 2.

* 3b. The list is empty.

	Use case ends.

**Use case: Find a inventory item**

**MSS**

1. User requests to list all inventory items
2. JJIMY shows a list of all inventory items
3. User requests to find inventory items based on keywords.
4. JJIMY returns a list of matching inventory items for the keywords.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given keywords do not match any inventory item.

    * 3a1. JJIMY shows an error message.

      Use case resumes at step 2.


### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 2000 total items (customers, menu items, inventory stock) without a noticeable sluggishness in performance for typical usage.
3.  Should be able to complete any single request within 200ms.
4.  Should work entirely client-side, without involving a remote server.
5.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

### Glossary

* **Inventory**: A list of necessary food ingredients and their associated stock quantities
* **Mainstream OS**: Windows, Linux, Unix, OS X
* **Private customer detail**: A customer detail that is not meant to be shared with others
