package org.jivesoftware.openfire.plugin.rest.controller;

import javax.ws.rs.core.Response;

import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.rest.BroadcastPacketInterceptor;
import org.jivesoftware.openfire.plugin.rest.entity.MessageEntity;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.jivesoftware.openfire.plugin.rest.utils.ServerUtils;
import org.jivesoftware.openfire.plugin.rest.utils.SubjectCalc;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Presence;

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

    private final JID serverAddress;

    public MessageController() {
        serverAddress = ServerUtils.getServerAddress();
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
     * @param messageEntity the message entity
     * @throws ServiceException when an empty message is provided
     */
    public void sendGameBroadcastMessage(String messageEntity) throws ServiceException {
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
            "</response>", messageEntity);

        Message xmppMessage = new Message();
        xmppMessage.setSubject("1337733113377331");
        xmppMessage.setFrom(ServerUtils.getServerAddress());
        xmppMessage.setID("JN_1234567");
        xmppMessage.setBody(systemMsg);

        XMPPServer.getInstance().getSessionManager().broadcast(xmppMessage);
    }
}
