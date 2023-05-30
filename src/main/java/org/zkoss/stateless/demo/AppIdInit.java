package org.zkoss.stateless.demo;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * Assign an unique application ID to differentiate each node on the cloud
 */
public class AppIdInit implements WebAppInit {
    public static final String APP_ID = java.util.UUID.randomUUID().toString();
    @Override
    public void init(WebApp wapp) throws Exception {
        System.out.println("app ID: " + APP_ID + " started");
    }
}
