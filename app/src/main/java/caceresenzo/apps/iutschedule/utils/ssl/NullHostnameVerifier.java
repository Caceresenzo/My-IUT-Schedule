package caceresenzo.apps.iutschedule.utils.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public enum NullHostnameVerifier implements HostnameVerifier {

    INSTANCE;

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }

}