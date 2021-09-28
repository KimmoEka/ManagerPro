package managerpro.Model;

import javax.persistence.*;
import java.util.Objects;
/**
 * A POJO with Annotations for Hibernate. Holds data of an order
 *
 * @author OTP7
 */
@Entity
@Table(name = "Orders", schema = "mydb")
public class OrdersEntity {
    private int id;
    private String orderDetails;
    private int tableNumber;
    private TablesEntity tablesByTableNumber;
    public OrdersEntity(){

    }
    public OrdersEntity(String orderDetails, int tableNumber){
        this.orderDetails = orderDetails;
        this.tableNumber = tableNumber;
    }
    public  OrdersEntity(String orderDetails, TablesEntity tablesByTableNumber){
        this.orderDetails = orderDetails;
        this.tablesByTableNumber = tablesByTableNumber;
    }
    @Id
    @SequenceGenerator(name = "order_id_generator", sequenceName = "order_id_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "order_id_seq")
    @Column(name = "ID", updatable = false, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Order_details")
    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Basic
    @Column(name = "Table_number")
    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdersEntity that = (OrdersEntity) o;
        return id == that.id &&
                tableNumber == that.tableNumber &&
                Objects.equals(orderDetails, that.orderDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderDetails, tableNumber);
    }

    @ManyToOne
    @JoinColumn(name = "Table_number", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    public TablesEntity getTablesByTableNumber() {
        return tablesByTableNumber;
    }

    public void setTablesByTableNumber(TablesEntity tablesByTableNumber) {
        this.tablesByTableNumber = tablesByTableNumber;
    }
    @Override
    public String toString()
    {
        return "ID: "+this.getId()+ ", Order Details: " + this.getOrderDetails()+ ", Table Number: "+this.getTableNumber();
    }
}
