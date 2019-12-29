package org.jivesoftware.openfire.plugin.rest.utils;

import org.jivesoftware.openfire.XMPPServer;
import org.xmpp.packet.JID;

public class ServerUtils {
    public static JID getServerAddress() {
        String serverName = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
        return new JID("sbrw.engine.engine", serverName, "EA_Chat");
    }
}
