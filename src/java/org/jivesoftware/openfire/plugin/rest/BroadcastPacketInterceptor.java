package org.jivesoftware.openfire.plugin.rest;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.plugin.rest.utils.ServerUtils;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class BroadcastPacketInterceptor implements PacketInterceptor {

    private final JID serverAddress;

    private final Logger logger = LoggerFactory.getLogger(BroadcastPacketInterceptor.class);

    public BroadcastPacketInterceptor() {
        serverAddress = ServerUtils.getServerAddress();
    }

    @Override
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
        if (!processed && !incoming) {
            JID toJID = packet.getTo();
            if (serverAddress.equals(packet.getFrom()) && toJID != null) {
                if (toJID.getResource() != null) {
                    packet.setTo(toJID.toBareJID());
                }
            }
        }
    }
}
