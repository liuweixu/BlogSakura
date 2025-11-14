package org.example.blogsakura.websocket.disruptor;

import lombok.Data;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.websocket.model.PictureEditRequestMessage;
import org.springframework.web.socket.WebSocketSession;

@Data
public class PictureEditEvent {

    /**
     * 消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 当前用户的 session
     */
    private WebSocketSession session;

    /**
     * 当前用户
     */
    private User user;

    /**
     * 图片 id
     */
    private Long pictureId;

}
