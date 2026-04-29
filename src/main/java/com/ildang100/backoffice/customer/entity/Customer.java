package com.ildang100.backoffice.customer.entity;

import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "customers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Email
    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 30)
    private String tele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    /**
     * 고객 기본 정보를 수정합니다.
     *
     * <p>{@code null}로 전달된 값은 수정하지 않고 기존 값을 유지합니다.</p>
     *
     * @param name 변경할 고객 이름
     * @param email 변경할 고객 이메일
     * @param tele 변경할 고객 전화번호
     */
    public void updateInfo(String name, String email, String tele) {
        if (name != null) {
            this.name = name;
        }

        if (email != null) {
            this.email = email;
        }

        if (tele != null) {
            this.tele = tele;
        }
    }

    /**
     * 고객 상태를 수정합니다.
     *
     * @param status 변경할 고객 상태
     */
    public void updateStatus(CustomerStatus status) {
        this.status = status;
    }

    /**
     * 고객을 탈퇴 처리합니다.
     *
     * <p>고객 데이터를 삭제하지 않고 상태를 {@code INACTIVE}로 변경합니다.</p>
     *
     * @throws ServiceException 이미 비활성 상태인 고객인 경우
     */
    public void withdraw() {
        if (status == CustomerStatus.INACTIVE) {
            throw new ServiceException(ErrorCode.CUSTOMER_DELETE_NOT_ALLOWED);
        }

        this.status = CustomerStatus.INACTIVE;
    }
}
