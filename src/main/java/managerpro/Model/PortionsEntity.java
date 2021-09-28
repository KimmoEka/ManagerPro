package managerpro.Model;

import managerpro.Utils.FormattingUtils;

import javax.persistence.*;
import java.util.Objects;

/**
 * A POJO with Annotations  for Hibernate. Holds data of an OrderedPortion
 *
 * @author OTP7
 */
@Entity
@Table(name = "Portions", schema = "mydb")
public class PortionsEntity {
    private int id;
    private String name;
    private double price;
    public PortionsEntity(){}
    public PortionsEntity(String name,double price){
        this.name = name;
        this.price = price;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "Price")
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortionsEntity that = (PortionsEntity) o;
        return id == that.id &&
                price == that.price &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }

    @Override
    public String toString()
    {
        return "Portion Name: "+this.name+", Portion price: "+ FormattingUtils.formatCurrency(getPrice());
    }
}
