package org.jivesoftware.openfire.plugin.rest.controller;

import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.rest.entity.MessageEntity;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.jivesoftware.openfire.plugin.rest.utils.ServerUtils;
import org.xmpp.packet.Message;

import javax.ws.rs.core.Response;

/**
 * The Class MessageController.
 */
public class MessageController {
    /**
     * The Constant INSTANCE.
     */
    public static final MessageController INSTANCE = new MessageController();

    /**
     * Gets the single instance of MessageController.
     *
     * @return single instance of MessageController
     */
    public static MessageController getInstance() {
        return INSTANCE;
    }

    /**
     * Send broadcast message.
     *
     * @param messageEntity the message entity
     * @throws ServiceException the service exception
     */
    public void sendBroadcastMessage(MessageEntity messageEntity) throws ServiceException {
        if (messageEntity.getBody() != null && !messageEntity.getBody().isEmpty()) {
            SessionManager.getInstance().sendServerMessage(null, messageEntity.getBody());
        } else {
            throw new ServiceException("Message content/body is null or empty", "",
                ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
                Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Send client-compatible broadcast message.
     *
     * @param systemMessageBody the message to send
     */
    public void sendGameBroadcastMessage(String systemMessageBody) {
        // Build the system message XML
        String systemMsg = String.format("<response status='1' ticket='0'>\n" +
            "<ChatBroadcast>\n" +
            "<ChatBlob>\n" +
            "<FromName>System</FromName>\n" +
            "<FromPersonaId>0</FromPersonaId>\n" +
            "<FromUserId>0</FromUserId>\n" +
            "<Message>%s</Message>\n" +
            "<ToId>0</ToId>\n" +
            "<Type>2</Type>\n" +
            "</ChatBlob>\n" +
            "</ChatBroadcast>\n" +
            "</response>", systemMessageBody);

        Message xmppMessage = new Message();
        xmppMessage.setSubject("1337733113377331");
        xmppMessage.setFrom(ServerUtils.getServerAddress());
        xmppMessage.setID("JN_1234567");
        xmppMessage.setBody(systemMsg);

        XMPPServer.getInstance().getSessionManager().broadcast(xmppMessage);
    }
}
