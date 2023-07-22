package hello.itemservice.jdbcTemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * NamedParameterJdbcTemplate
 *
 * 3 main 'param' candidates
 * SqlParameterSource
 *  * interface
 *  ---impl---
 * - BeanPropertySqlParameterSource
 *  * creates a parameter object through JavaBeans property
 *  * found in findAll() and save()
 * - MapSqlParameterSource
 *  * similar to map, but provides sql support such as sql query typing
 *  * found in update() method
 *
 * Map
 * template.queryForObject(sql, param, itemRowMapper());
 *
 * BeanPropertyRowMapper
 *
 */

@Slf4j
@Repository
public class JdbcTemplateRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item (item_name, price, quantity) values (:itemName, :price, :quantity)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(item);
        KeyHolder keyholder = new GeneratedKeyHolder();
        template.update(sql, param, keyholder);

        long key = Objects.requireNonNull(keyholder.getKey()).longValue();
        item.setId(key);
        return item;

    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name=?:itemName price=:price, quantity=:quantity where id=:id";

        SqlParameterSource param =  new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);
        template.update(sql,param);
    }

        @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = :id";
        try {
            Map<String, Long> param = Map.of("id", id);
            Item item = template.queryForObject(sql, param, itemRowMapper()); //gives EMptyResultDataAccessException if no matching value found
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    //    resultSet to Item mapper
    private RowMapper<Item> itemRowMapper() {
//        return (rs,rowNum) -> {
//            Item item = new Item();
//            item.setId(rs.getLong("id"));
//            item.setItemName(rs.getString("item_name"));
//            item.setPrice(rs.getInt("price"));
//            item.setQuantity(rs.getInt("quantity"));
//            return item;
//        };
        return BeanPropertyRowMapper.newInstance(Item.class);
    }


    //example of dynamic sql query. indeed very ass. MyBatis solves this issue.
    // for n params, results in 2^n cases to handle. Impractical to code so much
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name, price, quantity from item";

        if(StringUtils.hasText(itemName) || maxPrice != null ){
            sql += " where";
        }

        boolean andFlag = false;

        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName ,'%')";
            andFlag = true;
        }


        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += "price <= :maxPrice";
        }
        log.info("sql={}", sql);
        return template.query(sql, param, itemRowMapper());


    }
}