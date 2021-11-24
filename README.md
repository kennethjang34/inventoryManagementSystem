# Inventory management system

## A system that manages remaining stocks of a business as well as many functions that can facilitate business owners running their companies.





This **inventory management system** is built taking into account various situations that can occur in businesses especially in E-Commerce field.
The program will help not just control the number of an item in stock as a whole, but check and control individual products belonging to those items.
Along with its basic features for managing inventory, the program will also make sure only certain people with registered login accounts access the application.

To briefly mention why I chose this project, I was looking for a project that can be helpful in real life. And also, the fact that the designs of this type of programs 
require object-oriented programming as an inventory management system needs to take care of quantities of items in stock, while relying on other objects 
that take care of  individual products, is another factor that interested me in this project.








## User story:
As a user, I want to add a product to the item list of the inventory.

As a user, I want to check the quantities I have of  particular items in stock.

As a user, I want to check stocks of a certain item by location.

As a user, I want the application to create a sku automatically.

As a user, I want to check a product's best before date.

As a user, I want to be able to check the price a product was bought for.

As a user, I want to keep a record of change in quantity in inventory.

As a user, I want to make sure only a certain number of people who have login accounts can access the application.

As a user, I want to locate each product, as well as all the products belonging to a certain item category.

As a user, I want to be able to retrieve my login account password with my information.

As a user, I want to be able to save my current inventory manager state on my decision

As a user, I want to be able to load the previously saved status of the application when I want

As a user, I want to check the changes in stocks of products that happened on a particular date.


##Phase 4:Task 2

Mon Nov 22 19:26:19 PST 2021
new category FRUIT is created
Mon Nov 22 19:26:30 PST 2021
new item with ID: APPLE and name APPLE is created in category FRUIT
Mon Nov 22 19:26:44 PST 2021
100 new products belonging to ID: APP added at F123
Mon Nov 22 19:26:52 PST 2021
new category MEAT is created
Mon Nov 22 19:27:03 PST 2021
new item with ID: PORK and name PORK is created in category MEAT
Mon Nov 22 19:27:32 PST 2021
100 new products belonging to ID: PRK added at NOT SPECIFIED
Mon Nov 22 19:27:38 PST 2021
Product with SKU: APP3 removed
Mon Nov 22 19:27:38 PST 2021
Product with SKU: APP4 removed
Mon Nov 22 19:27:38 PST 2021
Product with SKU: APP5 removed
Mon Nov 22 19:27:57 PST 2021
Product with SKU: PRK98 removed
Mon Nov 22 19:27:57 PST 2021
Product with SKU: PRK97 removed
Mon Nov 22 19:27:57 PST 2021
Product with SKU: PRK96 removed

Process finished with exit code 0


##Phase 4:Task 3

- As Tables and their models have almost the same components and they are tightly coupled to their data source(Inventory, Ledger), if I had more time, I would make an abstract class
such as AbstractButtonTable, so I can reuse the codes for the tables and decouple them by adding interface such as TableDataSource so no matter what actual data model is used, the table can perform its own functionality.
- When Observable.update() is called, as there is no parameter, those tables implementing the interface needs to draw every cell again because it doesn't know at which cell a data has changed.
Therefore, if I had more time, I would modify the Observer interface so the notified observers can identify a particular change precisely
- Even if StockButtonTableModel is decoupled from Inventory class, it needs to call methods of its data source every time getValueat(int,int) method is called.
this is because the StockButtonTableModel doesn't store the table cell data in its own, but obtain it everytime it needs, which is highly inefficient.
Therefore, If I had more time, I would make the class store table cell data on its own, so when a particular location of the table model gets updated,
through Observer's new interface, it could make a minimal change to the existing model
- Even though they are not drawn in the UML diagram since they were private nested classes,
there are two classes named RegisterPanel and RetrievePanel used by LoginPanel and AdminPanel.
They are exactly the same in both LoginPanel and AdminPanel, but since I made them private, and
they were tightly coupled to their outer class as they get necessary information directly from the outer class member fields.
Therefore, If I had more time I would make those classes outside their outer classes and decouple their functionality from them.
- InventoryManagementSystemApplication class was meant to make a GUI using several other panel classes, but it has also taken the responsibility to record stock change events by calling Ledger.addAccount() method.
As this reduces cohesion of the class, if I had more time, I would register Ledger class to Inventory as an Observer,
so it can automatically record each stock change event, without needing outside classes calling its own addAccount method.
- Moreover, InventoryManagementSystemApplication extends JFrame (not shown in the UML diagram since it's from Java library).
This also reduces the cohesion of the class since the class itself is not considered a frame. It is a creator of the frame.
Therefore, if I had more time, I would make it have a member field for JFrame, rather than make it one of the Frames itself.

