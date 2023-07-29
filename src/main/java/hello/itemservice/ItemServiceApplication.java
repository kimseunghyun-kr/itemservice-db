package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


//@Import(MemoryConfig.class)
@Import(V2Config.class)
@Slf4j
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

//	@Bean
//	@Profile("test")    //only for test profile
//	public DataSource dataSource() {  //use my own datasource
//		log.info("initialising memory database");
//		DriverManagerDataSource dataSource = new DriverManagerDataSource(); //driverManagerDatasource
//		dataSource.setDriverClassName("org.h2.Driver");    //define what driver is to be used
//		dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");   //mem:db defines that in-memory(embedded) db within JVM runtime is to be used
//																 //DB_CLOSE_DELAY = -1 is used to prevent the database from closing when all connection is terminated
//		dataSource.setUsername("sa");  //uname
//		dataSource.setPassword("");   //pw
//		return dataSource;
//	}

}
