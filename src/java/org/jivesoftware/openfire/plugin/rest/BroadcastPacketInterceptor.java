package org.jivesoftware.openfire.plugin.rest;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class BroadcastPacketInterceptor implements PacketInterceptor {

    private final String serverName;
    private final XMPPServer xmppServer;
    private final JID serverAddress;

    private final Logger logger = LoggerFactory.getLogger(BroadcastPacketInterceptor.class);

    public BroadcastPacketInterceptor() {
        xmppServer = XMPPServer.getInstance();
        serverName = xmppServer.getServerInfo().getXMPPDomain();
        serverAddress = new JID(serverName);

        logger.info("INFO");
        logger.debug("DEBUG");
    }

    @Override
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
//        System.out.println(packet.toXML());
        logger.info(packet.toXML());
        if (serverAddress.equals(packet.getFrom())) {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                logger.info(message.getType().name());
            }
        }
    }
}
