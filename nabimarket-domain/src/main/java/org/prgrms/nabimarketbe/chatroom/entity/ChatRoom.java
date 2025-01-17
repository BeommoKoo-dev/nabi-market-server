package org.prgrms.nabimarketbe.chatroom.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.prgrms.nabimarketbe.BaseEntity;
import org.prgrms.nabimarketbe.suggestion.entity.Suggestion;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_rooms")
@NoArgsConstructor
@Getter
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @NotBlank
    @Column(nullable = false, name = "firestore_chat_room_path", unique = true)
    private String fireStoreChatRoomPath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "suggestion_id", unique = true)
    private Suggestion suggestion;

    public ChatRoom(
        String fireStoreChatRoomPath,
        Suggestion suggestion
    ) {
        this.fireStoreChatRoomPath = fireStoreChatRoomPath;
        this.suggestion = suggestion;
    }
}
