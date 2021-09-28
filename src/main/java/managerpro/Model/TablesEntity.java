package managerpro.Model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;
/**
 * A POJO with Annotations for Hibernate. Holds data of a table
 *
 * @author OTP7
 */
@Entity
@Table(name = "Tables", schema = "mydb")
public class TablesEntity {
    private int id;
    private Integer seats;
    private Integer reservationId;
    private Collection<OrdersEntity> ordersById;
    private Collection<ReservationsEntity> reservationsById;
    private ReservationsEntity reservationsByReservationId;

    public TablesEntity(){

    }
    public TablesEntity(Integer seats) {
        this.seats = seats;
    }
    @Id
    @SequenceGenerator(name = "table_id_generator", sequenceName = "table_id_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "table_id_seq")
    @Column(name = "ID", updatable = false, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Basic
    @Column(name = "Seats")
    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        if (seats < 0)
        this.seats = 0;
        else
        this.seats = seats;
    }

    @Basic
    @Column(name = "Reservation_id")
    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TablesEntity that = (TablesEntity) o;
        return id == that.id &&
                reservationId == that.reservationId &&
                Objects.equals(seats, that.seats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, seats, reservationId);
    }

    @OneToMany(mappedBy = "tablesByTableNumber")
    public Collection<OrdersEntity> getOrdersById() {
        return ordersById;
    }

    public void setOrdersById(Collection<OrdersEntity> ordersById) {
        this.ordersById = ordersById;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tablesByTableNumber")
    public Collection<ReservationsEntity> getReservationsById() {
        return reservationsById;
    }

    public void setReservationsById(Collection<ReservationsEntity> reservationsById) {
        this.reservationsById = reservationsById;
    }

    @ManyToOne
    @JoinColumn(name = "Reservation_id", referencedColumnName = "ID", nullable = true, insertable = false, updatable = false)
    public ReservationsEntity getReservationsByReservationId() {
        return reservationsByReservationId;
    }

    public void setReservationsByReservationId(ReservationsEntity reservationsByReservationId) {
        this.reservationsByReservationId = reservationsByReservationId;
    }
    @Override
    public String toString()
    {
        String s ="ID: "+ String.valueOf(this.id) + ", Seats: " + String.valueOf(this.seats);

        return s;
    }
}
