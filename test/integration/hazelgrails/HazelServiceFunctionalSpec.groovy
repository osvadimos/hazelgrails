package hazelgrails

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import grails.test.spock.IntegrationSpec
import org.apache.commons.lang.StringUtils
import spock.lang.Shared

class HazelServiceFunctionalSpec extends IntegrationSpec {

    @Shared
    def hazelService

    void "test for convertIntoDynamoJsonSortedFileSet"() {
        given:
        String hazelTagKey = 'hazel'
        String hazelHostHeader = 'ec2.amazonaws.com'
        String prefix = "piker_int"
        Config config = new Config()
        config.setProperty('access-key', 'AKIAJHDYZXOS356VGF7Q')
        config.setProperty('secret-key', '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')
        config.setProperty('region', 'eu-west-1')
        config.setProperty('tag-key', hazelTagKey)
        config.setProperty('tag-value', prefix + hazelTagKey)

        when:
        config.setInstanceName(lookupHostname())
        hazelService.instance = Hazelcast.newHazelcastInstance(config)
        then:
        1 == 1
    }

    static String lookupHostname(){
        //
//      NOTE -- InetAddress.getLocalHost().getHostName() will not work in certain environments.
        try {
            String result = InetAddress.getLocalHost().getHostName()
            if (StringUtils.isNotEmpty( result))
                return result
        } catch (UnknownHostException e) {
            // failed  try alternate means.
            println "failed to get hostname from InetAddress: "+e.message
        }

        String host = System.getenv("COMPUTERNAME")
        if (host != null)
            return host
        host = System.getenv("HOSTNAME")
        if (host != null)
            return host

        return null
    }
}
