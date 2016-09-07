# atpitem-batch-processor
Sample Spring Boot application to import a flat csv file into a database.
This application uses two DataSources. For the batch processing a embedded database (HSQLDB) is used, 
for the application data a relational database (MySQL) is used. 

The batch configuration is done via Java instead of XML. 
