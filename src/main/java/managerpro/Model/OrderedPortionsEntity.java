package managerpro.Model;

import javax.persistence.*;
import java.util.Objects;

/**
 * A POJO with Annotations  for Hibernate. Holds data of an OrderedPortion
 *
 * @author OTP7
 */
@Entity
@Table(name = "OrderedPortions", schema = "mydb")
public class OrderedPortionsEntity {
    private int junctionId;
    private int orderId;
    private int portionId;
    private int status;
    private String portionDetails;

    public OrderedPortionsEntity(){}
    public OrderedPortionsEntity(int orderId, int portionId, String portionDetails,int status){
        this.orderId = orderId;
        this.portionId = portionId;
        this.portionDetails = portionDetails;
        if(status>0)
            this.status = 1;
        else
            this.status=0;
    }
    public OrderedPortionsEntity(OrdersEntity order, PortionsEntity portion, String portionDetails, int status){
        this.orderId = order.getId();
        this.portionId = portion.getId();
        this.portionDetails = portionDetails;
        if(status>0)
            this.status = 1;
        else
            this.status=0;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "junction_id")
    public int getJunctionId() {
        return junctionId;
    }

    public void setJunctionId(int junctionId) {
        this.junctionId = junctionId;
    }

    @Basic
    @Column(name = "Order_id")
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Basic
    @Column(name = "Portion_id")
    public int getPortionId() {
        return portionId;
    }

    public void setPortionId(int portionId) {
        this.portionId = portionId;
    }

    @Basic
    @Column(name = "Portion_details")
    public String getPortionDetails() {
        return portionDetails;
    }

    public void setPortionDetails(String portionDetails) {
        this.portionDetails = portionDetails;
    }

    @Basic
    @Column(name = "Status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if(status>0)
        this.status = 1;
        else
            this.status=0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedPortionsEntity that = (OrderedPortionsEntity) o;
        return junctionId == that.junctionId &&
                orderId == that.orderId &&
                portionId == that.portionId &&
                status == that.status &&
                Objects.equals(portionDetails, that.portionDetails);
    }
    public boolean equalsIgnoreId(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedPortionsEntity that = (OrderedPortionsEntity) o;
        return orderId == that.orderId &&
                portionId == that.portionId &&
                Objects.equals(portionDetails, that.portionDetails);
    }
    @Override
    public int hashCode() {
        return Objects.hash(junctionId, orderId, portionId,status, portionDetails);
    }
}
