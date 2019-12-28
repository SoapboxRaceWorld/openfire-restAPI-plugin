package org.jivesoftware.openfire.plugin.rest.controller;

import javax.ws.rs.core.Response;

import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.rest.BroadcastPacketInterceptor;
import org.jivesoftware.openfire.plugin.rest.entity.MessageEntity;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

/**
 * The Class MessageController.
 */
public class MessageController {
    /** The Constant INSTANCE. */
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
        String serverName = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
        serverAddress = new JID("sbrw.engine.engine", serverName, "");
    }

    /**
     * Send broadcast message.
     *
     * @param messageEntity
     *            the message entity
     * @throws ServiceException
     *             the service exception
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
     * @param messageEntity
     *              the message entity
     * @throws ServiceException
     *              when an empty message is provided
     */
    public void sendGameBroadcastMessage(MessageEntity messageEntity) throws ServiceException {
        if (messageEntity.getBody() != null && !messageEntity.getBody().isEmpty()) {
            Message message = new Message();
            message.setFrom(serverAddress);
            message.setBody(messageEntity.getBody());
            message.setID("JN_1234567");

            SessionManager.getInstance().broadcast(message);
        } else {
            throw new ServiceException("Message content/body is null or empty", "",
                ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
                Response.Status.BAD_REQUEST);
        }
    }
}
