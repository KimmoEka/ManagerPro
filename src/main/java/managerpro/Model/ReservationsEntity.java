package managerpro.Model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Objects;
/**
 * A POJO with Annotations for Hibernate. Holds data of an reservation
 *
 * @author OTP7
 */
@Entity
@Table(name = "Reservations", schema = "mydb")
public class ReservationsEntity {
    private int id;
    private String clientName;
    private String clientPhone;
    private Date reservationDate;
    private Integer tableNumber;
    private TablesEntity tablesByTableNumber;
    private Collection<TablesEntity> tablesById;
    private Time startTime;
    private Time endTime;

    public ReservationsEntity() {
    }

    public ReservationsEntity(String clientName, String clientPhone, Date reservationDate, int tableNumber, Time startTime, Time endTime) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.reservationDate = new Date(reservationDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        this.tableNumber = tableNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Client_name")
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Basic
    @Column(name = "Client_phone")
    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    @Basic
    @Column(name = "Reservation_date")
    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = new Date(reservationDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Basic
    @Column(name = "startTime")
    public Time getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Time start_time) {
        this.startTime = start_time;
    }

    @Basic
    @Column(name = "endTime")
    public Time getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Time end_time) {
        this.endTime = end_time;
    }

    @Basic
    @Column(name = "Table_number")
    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationsEntity that = (ReservationsEntity) o;
        return id == that.id &&
                Objects.equals(clientName, that.clientName) &&
                Objects.equals(clientPhone, that.clientPhone) &&
                Objects.equals(reservationDate, that.reservationDate) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(tableNumber, that.tableNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientName, clientPhone, reservationDate, tableNumber, startTime, endTime);
    }

    @ManyToOne
    @JoinColumn(name = "Table_number", referencedColumnName = "ID", insertable = false, updatable = false)
    public TablesEntity getTablesByTableNumber() {
        return tablesByTableNumber;
    }

    public void setTablesByTableNumber(TablesEntity tablesByTableNumber) {
        this.tablesByTableNumber = tablesByTableNumber;
    }

    @OneToMany(mappedBy = "reservationsByReservationId")
    public Collection<TablesEntity> getTablesById() {
        return tablesById;
    }

    public void setTablesById(Collection<TablesEntity> tablesById) {
        this.tablesById = tablesById;
    }

    @Override
    public String toString() {
        return "ReservationsEntity{" +
                "id=" + id +
                ", clientName='" + clientName + '\'' +
                ", clientPhone=" + clientPhone +
                ", tableNumber=" + tableNumber +
                ", reservationDate=" + reservationDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
