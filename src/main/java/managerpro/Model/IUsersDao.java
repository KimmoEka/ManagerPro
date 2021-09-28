package managerpro.Model;

import java.util.List;

public interface IUsersDao {
    public List<UsersEntity> getAllUsers();
    public UsersEntity getUser(int userId);
    public void createUser(UsersEntity user);
    public void updateUser(UsersEntity user);
    public void deleteUser(UsersEntity user);
    public UsersEntity getUserByUsername(String username);
}
