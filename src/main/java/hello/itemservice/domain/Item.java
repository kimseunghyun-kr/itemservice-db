package hello.itemservice.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    without column, jpa auto detects and uses the field name as the table column name.
//    jpa spring boot starter provides an automatic mapper from camel case to underscore format
    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
