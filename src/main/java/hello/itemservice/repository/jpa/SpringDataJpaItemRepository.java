package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item,Long> {

    List<Item> findByItemNameContaining(String itemName);

    List<Item> findByItemNameLike(String itemName);
    List<Item>findByPriceLessThanEqual(Integer price);

//    query method
    List<Item>getByItemNameContainingAndPriceLessThanEqual(String itemName, Integer price);
    List<Item>getByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);
//    direct query
//  @Query("select i from Item i where i.itemName like %:itemName% and i.price <= :price")
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);

}
