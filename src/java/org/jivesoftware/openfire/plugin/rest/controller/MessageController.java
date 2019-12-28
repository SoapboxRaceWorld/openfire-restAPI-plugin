package org.jivesoftware.openfire.plugin.rest.controller;

import javax.ws.rs.core.Response;

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
        if (messageEntity != null && !messageEntity.isEmpty()) {
            Message message = new Message();
            message.setFrom(serverAddress);
            message.setBody(messageEntity);
            message.setID("JN_1234567");

            for (ClientSession c : SessionManager.getInstance().getSessions()) {
                if (c.getStatus() == Session.STATUS_AUTHENTICATED) {
                    Presence presence = c.getPresence();

                    if (presence != null && presence.isAvailable()) {
                        String bareJID = c.getAddress().toBareJID();
                        message.setTo(bareJID);
                        message.setSubject(Long.toString(SubjectCalc.calculateHash(bareJID.toCharArray(), messageEntity.toCharArray())));

                        try {
                            SessionManager.getInstance().userBroadcast(c.getUsername(), message);
                        } catch (UserNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
//            SessionManager.getInstance().broadcast(message);
        } else {
            throw new ServiceException("Message content/body is null or empty", "",
                ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
                Response.Status.BAD_REQUEST);
        }
    }
}
