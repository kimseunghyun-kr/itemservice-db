# JPA Introduction
Spring and JPA are the main technologies in the Java Enterprise market. While Spring provides various functionalities throughout the application, including the Dependency Injection (DI) container, JPA offers Object-Relational Mapping (ORM) technology for data access.

JPA is as extensive as Spring, and there is a lot to learn. However, once you master it, you can greatly increase productivity in data access. Unlike SQL mapper technologies like JdbcTemplate or MyBatis, where developers need to write SQL queries themselves, JPA can generate and execute SQL queries on behalf of developers. In practice, Spring Data JPA and Querydsl are commonly used together to make JPA even more convenient. But remember, JPA is the core technology. Spring Data JPA and Querydsl are tools that make it more convenient to use JPA.

When using the spring-boot-starter-data-jpa library, you can easily integrate JPA and Spring Data JPA with Spring Boot by adding minimal configuration.

The following libraries will be added:

hibernate-core: The Hibernate library, which is the JPA implementation.
jakarta.persistence-api: The JPA interface.
spring-data-jpa: The Spring Data JPA library.
In the application.properties file of the main or test directory, you can add the following configuration:

```
# JPA log
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```
With Spring Boot 3.0 and above, which uses Hibernate version 6, the log configuration is slightly different:

```
# JPA log
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```
@Entity: This annotation indicates that the class is an object used by JPA (Java Persistence API). It is essential for JPA to recognize this annotation, and a class marked with @Entity is referred to as an entity in JPA.

@Id: This annotation maps the field to the primary key (PK) of the table.

@GeneratedValue(strategy = GenerationType.IDENTITY): This annotation is used with @Id and indicates the strategy for generating the PK value as an IDENTITY type, typically used with databases like MySQL's auto-increment feature.

@Column: This annotation maps the field of the object to a column in the table.

name = "item_name": In this case, the object has a field named "itemName," but the corresponding table column is named "item_name." This annotation provides the mapping between them.

length = 10: In JPA, this mapping information is used for generating DDL (create table) statements. It specifies the length of the column, e.g., (varchar 10).

If @Column is omitted, the field's name is used as the table column name. Additionally, when using Spring Boot in combination, the field name in the object (camel case) is automatically converted to the table column name (underscore), so you can omit @Column in such cases.

JPA requires a public or protected default constructor for entities. Ensure that you include this constructor in the class.

``` java
public Item() {}
```
With this constructor, the basic mapping is all set.

private final EntityManager em: Looking at the constructor, you can observe that the EntityManager is injected through Spring. All operations in JPA are performed using the EntityManager. It holds the data source internally and can access the database.

@Transactional: All data modifications (create, update, delete) in JPA must be done within a transaction. However, querying can be done without a transaction. For data modification, it is typical to start a transaction at the service layer, so there were no issues with omitting the transaction on the service layer in this example. But in general, it's a good practice to have transactions at the service layer, where business logic starts. Keep in mind that JPA requires a transaction when performing data modifications, so we've added the transaction to the repository in this example.

Note: Configuring JPA involves various settings, including EntityManagerFactory, JpaTransactionManager, and data sources. Spring Boot automates this entire process. To see how to set up JPA from the beginning, you can refer to the JPA basics section. For Spring Boot's automatic configuration, you can check JpaBaseConfiguration.

# JpaItemRepositoryV1 Analysis
save() - Save:

``` java
public Item save(Item item) {
em.persist(item);
return item;
}
```
The em.persist(item) line is used in JPA to save an object to the database. JPA generates and executes the following SQL based on this operation:

``` sql
insert into item (id, item_name, price, quantity) values (null, ?, ?, ?)
```
or

``` sql
insert into item (id, item_name, price, quantity) values (default, ?, ?, ?)
```

```
insert into item (item_name, price, quantity) values (?, ?, ?)
```

Notice that the id value is missing in the SQL query because the primary key generation strategy is set to IDENTITY. JPA generates the query this way, but after the INSERT SQL execution, JPA populates the id field of the Item object with the primary key value generated by the database.

PK (Primary Key) Mapping Reference:

``` java

@Entity
public class Item {
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
}
```
update() - Update:

``` java

public void update(Long itemId, ItemUpdateDto updateParam) {
Item findItem = em.find(Item.class, itemId);
findItem.setItemName(updateParam.getItemName());
findItem.setPrice(updateParam.getPrice());
findItem.setQuantity(updateParam.getQuantity());
}
```
The em.update() method is not invoked explicitly. However, JPA still executes the UPDATE SQL statement when the transaction is committed because it detects changes in the entity objects. JPA keeps track of the changes to entities using a concept called the "persistence context" (also known as the "entity manager's cache"). When the transaction is committed, JPA checks for changes in the persistence context and automatically generates the corresponding SQL statements, in this case:


```
update item set item_name=?, price=?, quantity=? where id=?
```

Understanding how JPA detects changes in entities requires knowledge of the "persistence context" in JPA's internal mechanism, which is covered in detail in the JPA basics section. For now, it's enough to know that when the transaction is committed, JPA checks for changes in the entity objects and executes the necessary SQL statements.

In testing scenarios, the transaction is usually rolled back at the end of the test, so the UPDATE SQL won't be executed. If you want to verify the UPDATE SQL statement during testing, you can use @Commit to ensure the transaction is committed and the UPDATE SQL is executed.

findById() - Single Entity Retrieval:

``` java

public Optional<Item> findById(Long id) {
Item item = em.find(Item.class, id);
return Optional.ofNullable(item);
}
```
When retrieving an entity object by its primary key (PK), JPA's find() method is used. The find() method takes the entity class and the PK value as parameters. JPA generates and executes the following SELECT SQL statement based on this operation:
```
select
item0_.id as id1_0_0_,
item0_.item_name as item_nam2_0_0_,
item0_.price as price3_0_0_,
item0_.quantity as quantity4_0_0_
from item item0_
where item0_.id=?
```

The SQL query uses an alias (item0_) to avoid conflicts, especially in complex queries with joins or conditions.

findAll - List Retrieval:

``` java

public List<Item> findAll(ItemSearchCond cond) {
String jpql = "select i from Item i";
// Dynamic query omitted
TypedQuery<Item> query = em.createQuery(jpql, Item.class);
return query.getResultList();
}
```
JPQL (Java Persistence Query Language):
JPA provides an object-oriented query language called JPQL (Java Persistence Query Language), which is primarily used for complex data retrieval with various conditions. While SQL operates on database tables, JPQL operates on entity objects, and you can think of it as executing SQL against entities.

When using JPQL, the entity name (Item in this case) is used after the "from" keyword. Note that the case of the entity name and its attributes is important; they should match the case specified in the entity class.

JPQL is quite similar to SQL in terms of syntax, making it easy for developers to adapt to. When JPQL is executed, JPA leverages the mapping information of the included entity objects to generate and execute the underlying SQL.

Executed JPQL:

``` java

select i from Item i
where i.itemName like concat('%',:itemName,'%')
and i.price <= :maxPrice
SQL Executed via JPQL:
``` 
``` java

select
item0_.id as id1_0_,
item0_.item_name as item_nam2_0_,
item0_.price as price3_0_,
item0_.quantity as quantity4_0_
from item item0_
where (item0_.item_name like ('%'||?||'%'))
and item0_.price<=?
```
Parameters in JPQL:
Parameters in JPQL are indicated with a colon followed by the parameter name. For example:

``` java

where price <= :maxPrice
``` 
To bind parameters, you can use the query.setParameter() method:

``` java

query.setParameter("maxPrice", maxPrice)
``` 
# Dynamic Query :
Even with JPA, dynamic queries can still be challenging. To handle dynamic queries more cleanly, developers often use Querydsl, a library specifically designed for this purpose. In real-world scenarios, JPA and Querydsl are frequently used together to overcome dynamic query issues effectively.

# JPA Application 3 - Exception Translation:
In JPA, when an exception occurs, it results in a JPA-specific exception being thrown.

``` java

@Repository
@Transactional
public class JpaItemRepositoryV1 implements ItemRepository {
private final EntityManager em;

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }
}
``` 
The EntityManager is a pure JPA technology and is not related to Spring. Therefore, when an exception occurs, the EntityManager throws JPA-related exceptions.

JPA throws PersistenceException and its sub-exceptions. Additionally, JPA can also throw IllegalStateException and IllegalArgumentException.

Now, the question is how to convert JPA exceptions into Spring's exception abstraction (DataAccessException)? The answer lies within the @Repository annotation.

Before Exception Translation:
@Repository Features:

A class annotated with @Repository becomes a target for component scanning.
A class annotated with @Repository becomes a target for the exception translation AOP.
When using Spring with JPA, Spring automatically registers a JPA exception translator (PersistenceExceptionTranslator).
The exception translation AOP proxy converts JPA-related exceptions to Spring DataAccessException.
After Exception Translation:
As a result, if the repository class has only the @Repository annotation, Spring will create an AOP that handles the exception translation.

Note:
Spring Boot automatically registers the PersistenceExceptionTranslationPostProcessor, which registers an advisor that creates the AOP proxy for @Repository.

Note:
The actual code that converts the JPA exceptions into Spring exceptions is EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible().