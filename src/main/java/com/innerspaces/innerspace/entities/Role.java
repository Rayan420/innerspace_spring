package com.innerspaces.innerspace.entities;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Integer roleId;
    private String authority;

    public Role() {
        super();
    }


    public Role(Integer roleId, String authority)
    {
        super();
        this.roleId = roleId;
        this.authority = authority;

    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", authority='" + authority + '\'' +
                '}';
    }

}
