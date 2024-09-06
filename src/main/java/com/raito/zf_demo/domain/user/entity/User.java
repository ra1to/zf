package com.raito.zf_demo.domain.user.entity;

import com.raito.zf_demo.infrastructure.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @author raito
 * @since 2024/09/06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Comment("用户信息")
@Entity
@Table(name = "zf_users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Comment("用户名")
    @Column(unique = true)
    private String username;

    @Comment("密码")
    private String password;

    @Comment("邮箱")
    private String email;

    public User encodePassword() {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
        return this;
    }

    public Boolean validatePassword(String password) {
        return BCrypt.checkpw(password, this.password);
    }

}
