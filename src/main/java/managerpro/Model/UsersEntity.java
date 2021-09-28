package managerpro.Model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Objects;
/**
 * A POJO with Annotations for Hibernate. Holds data of a table
 *
 * @author OTP7
 */
@Entity
@Table(name = "Users", schema = "mydb")
public class UsersEntity {
    private int id;

    @NotEmpty(message = "{please_enter_username.users_entity}")
    private String username;

    @NotEmpty(message = "{password_is_required.users_entity}")
    @Size(min = 5, message = "{password_has_to_be_at_least_x_characters_long.users_entity}")
    private String password;

    @NotEmpty(message = "{please_enter_user_level.users_entity}")
    private String userlevel;

    public UsersEntity(){};

    public UsersEntity(String username, String password, String userlevel) {
        this.username = username;
        this.password = password;
        this.userlevel = userlevel;
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
    @Column(name = "Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "Password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "Userlevel")
    public String getUserlevel() {
        return userlevel;
    }

    public void setUserlevel(String userlevel) {
        this.userlevel = userlevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntity that = (UsersEntity) o;
        return id == that.id &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(userlevel, that.userlevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, userlevel);
    }

    @Override
    public String toString() {
        return "UsersEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userlevel='" + userlevel + '\'' +
                '}';
    }
}
