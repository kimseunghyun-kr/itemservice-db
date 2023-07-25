Insert SQL:
To use the Insert SQL, you can use <insert>.
For the id, you should specify the method name 
defined in the mapper interface.
In this case, the method name is save(),
so you can specify it as save.
The parameters can be handled using the #{} syntax,
and you should write the property name of the object 
passed from the mapper.
When using #{} syntax, it corresponds to using a PreparedStatement in JDBC, where ? is replaced with the values.

The useGeneratedKeys is used when the database employs the IDENTITY strategy to generate keys. keyProperty specifies the name of the attribute where the generated key is stored. After the Insert operation, the value of the generated key will be assigned to the id attribute of the item object.

Update SQL:
To use the Update SQL, you can use <update>. In this case, there are two parameters: Long id and ItemUpdateDto updateParam. If there is only one parameter, you don't need to specify @Param, but if there are multiple parameters, you need to use @Param to distinguish them.

Select SQL:
To use the Select SQL, you can use <select>. The resultType specifies the return type, and in this case, the results are mapped to the Item object. Thanks to the configuration in application.properties where mybatis.type-aliases-package=hello.itemservice.domain is specified, you don't have to write the full package name. If not, you would need to specify the complete package names.

Mybatis automatically converts underscores to camel case due to the mybatis.configuration.map-underscore-to-camel-case=true configuration (e.g., item_name becomes itemName).

When returning a single object from Java code, you can use Item or Optional<Item>. If you have multiple objects, you can use collections, usually List. Refer to the following for more details.

Mybatis supports convenient dynamic queries through syntax like <where> and <if>. <if> adds a clause if the condition is met, and <where> appropriately constructs the WHERE statement. In the example, if all <if> conditions fail, Mybatis does not create a WHERE statement. If at least one <if> condition succeeds, the first encountered and is transformed into WHERE.

XML Special Characters:
In the example, when comparing prices, you can observe the use of &lt;=, which represents <=. This is because XML doesn't allow special characters like < and > in data areas. Instead, you need to use their corresponding escape codes.

Here are some of the escape codes:
```
< : &lt;
> : &gt;
& : &amp;
```
Alternatively, you can use CDATA syntax provided by XML to use special characters within it. However, be aware that using CDATA might prevent certain features like <if> and <where> from working as expected.

You can choose the appropriate method (escape codes or CDATA) depending on your specific needs.