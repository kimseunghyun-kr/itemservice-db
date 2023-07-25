package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

//-Mybatis is an sql mapper like JDBCTEMPLATE but better
//allows for easier dynamic sql queries through usage of xml
//though java orm took over so it is not being used that much nowadays
@Mapper
public interface ItemMapper {

//    for methods with singular params, @Param can be truncated but for methods with 2 or more params
//    @Param must be explicitly stated

//    the @Mapper interface is needed for Mybatis to scan this Interface

//    an xml will be linked to this interface to perform mapped SQL actions
//    the default settings of mybatis states that
//    the xml file needs to have the same directory as the java file under resources

//    to customise the storage location of the xml file, the application.properties setting
//    can be modified as such as the following in applications.properties
//    mybatis.mapper-locations=classpath:mapper/**/*.xml
//    this allows spring to identify whatever files under the resources/mapper to be a xml mapping file.
//    should this be the case, the name of the xml file can be freely be chosen
//    application.properties for test package also needs to be set up in the following to work


    public void save(Item item);

    public void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);

    public Optional<Item> findById(Long id);

    public List<Item> findAll(ItemSearchCond itemSearchCond);


}
