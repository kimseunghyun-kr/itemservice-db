package hello.itemservice.config;

import hello.itemservice.jdbcTemplate.JdbcTemplateRepositoryV2;
import hello.itemservice.jdbcTemplate.JdbcTemplateRepositoryV3;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV3Config {

    private final DataSource dataSource;
    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateRepositoryV3(dataSource);
    }
}
