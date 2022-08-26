package sit.int221.oasipservice.entities;

import sit.int221.oasipservice.EnumRole;
import sit.int221.oasipservice.annotation.ValidateEnum;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private Integer id;

    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be blank")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email cannot be blank")
    @Email(message = "Please insert a valid email format.")
    @Column(name = "email", nullable = false, length = 50)
    private String email;

//    @ValidatedString(values = {"student", "lecturer", "admin"}, message = "There are only student,lecturer and admin roles")
//    @Enumerated(EnumType.STRING)
//    @StringEnumeration(enumClass = EnumRole.class)
    @NotNull(message = "role cannot be null")
    @NotBlank(message = "role cannot be blank")
    @ValidateEnum(targetClassType = EnumRole.class, message = "Invalid role! there are only student,lecturer and admin role")
    @Column(name = "role", nullable = false)
    private String role;

    //    @CreationTimestamp ไม่ work แต่ใกล้เคียง
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @Basic
//    @Temporal(TemporalType.TIMESTAMP)

    @Column(name = "createdOn", nullable = false, insertable = false,unique = true)
    private Instant createdOn;

    //    @UpdateTimestamp
    @Column(name = "updatedOn", nullable = false, updatable = false, insertable = false,unique = true)
    private Instant updatedOn;

    @Column(name = "password", nullable = false, length = 90)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}