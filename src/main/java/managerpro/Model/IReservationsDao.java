package managerpro.Model;

import java.util.List;

public interface IReservationsDao {
    public List<ReservationsEntity> getAllReservations();
    public ReservationsEntity getReservation(int reservationId);
    public void createReservation(ReservationsEntity reservation);
    public void updateReservation(ReservationsEntity reservation);
    public void deleteReservation(ReservationsEntity reservation);
}
