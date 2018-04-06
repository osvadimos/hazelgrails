package system.utils

import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.LogFactory

/**
 * 
 * Created by tim on 02/08/2015.
 */
class SystemUtils {

    private static final log = LogFactory.getLog(this)

    private static String hostname = null

    static String getHostname(){
        return hostname
    }

    /**
     *  should be called by bootStrap at startup
     */
    static boolean setupHostname(){
        hostname = lookupHostname()
        return hostname as boolean
    }

    /**
     *
     *  1) try InetAddress.LocalHost first
     *  2) try environment properties.
     *    a) COMPUTERNAME
     *    b) HOSTNAME
     *  3) null if undetermined
     */
    static String lookupHostname(){
        //
//      NOTE -- InetAddress.getLocalHost().getHostName() will not work in certain environments.
        try {
            String result = InetAddress.getLocalHost().getHostName()
            if (StringUtils.isNotEmpty( result))
                return result
        } catch (UnknownHostException e) {
            // failed  try alternate means.
            log.warn "failed to get hostname from InetAddress: "+e.message
        }

        String host = System.getenv("COMPUTERNAME")
        if (host != null)
            return host
        host = System.getenv("HOSTNAME")
        if (host != null)
            return host

        log.error "unable to determine hostname by ANY means"
        return null
    }
}

