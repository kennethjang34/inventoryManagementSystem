# Inventory management system

## A system that manages remaining stocks of a business as well as many functions that can facilitate business owners running their companies.

Demo Video: https://youtu.be/Ky5jwdRnsoU


_This project was started as a school project of UBC CS 210 course (2021 Winter). Except for those specified for marking purpose (ex. Storing data in JSON format, displaying at least one graphical component when started, etc.),
All the features of the application were never specified by anyone else other than the author. Apart from loadFile method of FileLoader class and writer class' JSONObject.toString(indentFactor) line in persistence package, 
which were provided by the instructor, the author implements the features of the application and write their codes._



**This inventory management system is built taking into account various situations that can occur in businesses especially in E-Commerce field.
The program will help not just control the number of an item in stock as a whole, but check and control individual products belonging to those items.
Along with its basic features for managing inventory, the program will also make sure only certain people with registered login accounts access the application.**



## Main features of this application

The application allows the user to create new categories and items belonging to those categories.

The application allows the user to create new products with intuitive interface. 
The user can create new products with "Add" button or on a pop-up menu that shows up when the user right-clicks the entry that represents the item the user has created.

The application allows for removing products in a highly efficient way.
The user can also remove products from the inventory with "Remove" button or on a pop-up menu that shows up when the user right-clicks the entry that represents the item the user has created.

The application supports changing data of items or products.
the user can change any of the following data entries of items: category, name, description, list price, special note.
the user can change any of the following data entries of products: location, price, cost, best-before date, id.


The application allows the user to make changes in several items with tables.

The stock table, which shows items in the inventory, is located at the bottom of the inventory panel right below two combo boxes that can control the display of the existing items.
By selecting the category or item, the user can directly find the item data he/she was looking for.

The product table, which shows products in the inventory as its entries, is located at the top of the inventory panel. 
This table is synchronized with the inventory state, which means removing products from the inventory will remove them from the product table as well if they have been added to the table.

The user can sort both tables by clicking the column header

Besides the two tables, the user can also find items and products using search panel. 
After clicking "search" menu item at the bottom of the file menu, the user will be shown an interface that will prompt the user to enter item id or product sku he/she is looking for.
When given the input, the application will come up to the user with search results.

The application records change in stocks of items whenever the user performs an action that affects the quantities of items in the inventory.
The user can check those records in ledger panel by clicking "Ledger" tab at the top of the application display.

The application supports sorting records by dates and filtering them by whether there was any change in stocks of the item the user has selected.

The application allows the user to check individual accounts that contain information of each one of stock changes by double-clicking the ledger entry.

The application allows for managing the list of people that can access the data of the inventory.

The application allows for managing the list of people who can manage the list of those that can access the data.

The application can distinguish between regular members and admin members. Only admin members will be allowed to access Admin panel by clicking "Admin" tab at the top of the application display.

To save the changes of the inventory, the user needs to manually click "Save" option in file menu. 
However, whenever a new login account is created, the application will save the new account automatically right away.

The user can click retrieve button on login panel to retrieve the password, providing id, name, birthday, and personal number that was registered in the login account when it was created.
## Reasons behind choice of this project
To briefly mention why I chose this project, I was looking for a project to which I can apply various design pattern/architectures such as observer pattern or MVC architecture 
that can also be directly used in real life. Along the way of finding a good project to start with, I found out that applications such as to-do list program and inventory management software
require those skills I was intending to apply and improve. Since this type of project involves creating several entries(products, items, categories, dates, etc.) and
editing and recording those entries as well as presenting them in the easy-to-use graphical user interface, the development of this application has been the perfect project 
where I could practice applying several design patterns including observer pattern and singleton pattern to a real application and 
learn how MVC architecture helped decouples model, view and controller, making codes more re-usable.

## Review
There were many things to be improved that I couldn't deal with due to lack of time.
As this project was done in a limited time period, I couldn't make the best GUI design I can make. 
GUI design is still very intuitive and easy to use, but each panel might look simple as I only used basic design of Java Swing components.

Documentations also remain to be improved.
As GUI parts were developed intensively for a short time of period, some methods lack documentations.


In terms of the design of the application, I wish I had more time to work on refactoring RowDataChangeSupport class and DataViewer interface.
RowDataChangeSupport and DataViewer were first created to implement observer pattern among model, view, controller. 
They together indeed provided a good interface for the communication between model and controller and model and view, which led enabling decoupling them from each other more effectively.
However, as the development of this application moves forward, extra methods had to be added, resulting in tight coupling between RowDataChangeSupport and DataViewer.
If I had had extra time to spare for this project, I would have gone ahead and decoupling them
by creating a new interface between RowDataChangeSupport and DataViewer, which would most likely be a class that can represent change events of the models.
Even though I didn't have enough time to refine the design, the application is more or less towards the completion as almost all the main features I was intending to realize with this software have been realized.
I am hoping to come back and make improvements at one point in the future.




