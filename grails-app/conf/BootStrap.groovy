import com.hazelcast.aws.AwsDiscoveryStrategyFactory
import com.hazelcast.config.Config
import com.hazelcast.config.DiscoveryStrategyConfig
import com.hazelcast.config.JoinConfig
import com.hazelcast.core.Hazelcast
import system.utils.SystemUtils
import com.hazelcast.spi.discovery.*

class BootStrap {

    def grailsApplication
    def hazelService


    def init = { servletContext ->
        if (SystemUtils.setupHostname()) {
            log.info "hostname setup for piker: " + SystemUtils.hostname
        }//else will log err itself

/*

        Config config = new Config()
        config.setProperty('access-key', grailsApplication.config.grails.plugin.awssdk.accessHazelKey.toString())
        config.setProperty('secret-key', grailsApplication.config.grails.plugin.awssdk.secretHazelKey.toString())
        config.setProperty('region', grailsApplication.config.grails.plugin.awssdk.region.toString())
        config.setProperty('tag-key', grailsApplication.config.grails.plugin.awssdk.hazelTagKey.toString())
        config.setProperty('tag-value', prefix + grailsApplication.config.grails.plugin.awssdk.hazelTagKey.toString())
        config.setInstanceName(SystemUtils.hostname)
*/

        String prefix = grailsApplication.config.io.ansr.piker.properties.aws.dynamo.table.prefix ?: ''
        Config config = new Config()
        config.getProperties().setProperty("hazelcast.discovery.enabled", "true")
        JoinConfig joinConfig = config.getNetworkConfig().getJoin()
        joinConfig.getTcpIpConfig().setEnabled(false)
        joinConfig.getMulticastConfig().setEnabled(false)
        joinConfig.getAwsConfig().setEnabled(true)
        AwsDiscoveryStrategyFactory awsDiscoveryStrategyFactory = new AwsDiscoveryStrategyFactory()
        Map<String, Comparable> properties = new HashMap<String, Comparable>()
        properties.put("access-key", grailsApplication.config.grails.plugin.awssdk.accessHazelKey.toString())
        properties.put("secret-key", grailsApplication.config.grails.plugin.awssdk.secretHazelKey.toString())
        properties.put("region", grailsApplication.config.grails.plugin.awssdk.region.toString())
        properties.put("host-header", "ec2.amazonaws.com")
        properties.put("tag-key", grailsApplication.config.grails.plugin.awssdk.hazelTagKey.toString())
        properties.put("tag-value", prefix + grailsApplication.config.grails.plugin.awssdk.hazelTagKey.toString())
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(awsDiscoveryStrategyFactory, properties)
        joinConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig)

        //if you want to configure multiple discovery strategies at once
        ArrayList<DiscoveryStrategyConfig> discoveryStrategyConfigs = new ArrayList<DiscoveryStrategyConfig>()
        joinConfig.getDiscoveryConfig().setDiscoveryStrategyConfigs(discoveryStrategyConfigs)
        log.info "B setting up hazel:${config.toString()}"
        hazelService.instance = Hazelcast.newHazelcastInstance(config)

    }
    def destroy = {
    }
}
