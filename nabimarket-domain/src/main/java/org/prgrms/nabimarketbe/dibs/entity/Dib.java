package org.prgrms.nabimarketbe.dibs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.prgrms.nabimarketbe.BaseEntity;
import org.prgrms.nabimarketbe.card.entity.Card;
import org.prgrms.nabimarketbe.error.BaseException;
import org.prgrms.nabimarketbe.error.ErrorCode;
import org.prgrms.nabimarketbe.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "dibs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dib extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dib_id", nullable = false)
    private Long dibId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    public Dib(
        User user,
        Card card
    ) {
        if (user == null || card == null) {
            throw new BaseException(ErrorCode.INVALID_REQUEST);
        }

        this.user = user;
        this.card = card;
    }
}
