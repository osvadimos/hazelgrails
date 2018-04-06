import com.hazelcast.aws.AwsDiscoveryStrategyFactory
import com.hazelcast.config.Config
import com.hazelcast.config.DiscoveryConfig
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
        config.getProperties().setProperty("hazelcast.discovery.enabled", "true")
        JoinConfig joinConfig = config.getNetworkConfig().getJoin()

*/
        String prefix = grailsApplication.config.awshazel.prefix
        Config config = new Config()
        config.getProperties().setProperty("hazelcast.discovery.enabled", "true")
        JoinConfig joinConfig = config.getNetworkConfig().getJoin()
        joinConfig.getTcpIpConfig().setEnabled(false)
        joinConfig.getMulticastConfig().setEnabled(false)
        joinConfig.getAwsConfig().setEnabled(false)
        AwsDiscoveryStrategyFactory awsDiscoveryStrategyFactory = new AwsDiscoveryStrategyFactory()
        Map<String, Comparable> properties = new HashMap<String, Comparable>()
        properties.put("access-key", grailsApplication.config.awshazel.accessHazelKey.toString())
        properties.put("secret-key", grailsApplication.config.awshazel.secretHazelKey.toString())
        properties.put("region", grailsApplication.config.awshazel.region.toString())
        properties.put("host-header", "ec2.amazonaws.com")
        properties.put("tagKey", "hazelcluster")
        properties.put("tag-key", "hazelcluster")
        properties.put("tagValue", "hazelcluster")
        properties.put("tag-value", "hazelcluster")
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(awsDiscoveryStrategyFactory, properties)
        joinConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig)
        DiscoveryConfig discoveryConfig = new DiscoveryConfig()
        discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig)
        joinConfig.setDiscoveryConfig(discoveryConfig)
        config.networkConfig.setJoin(joinConfig)
/*        //Config config = new Config()
        JoinConfig joinConfig = config.getNetworkConfig().getJoin()
        joinConfig.getTcpIpConfig().setEnabled(false)
        joinConfig.getMulticastConfig().setEnabled(false)
        joinConfig.getAwsConfig().setEnabled(true)
        AwsDiscoveryStrategyFactory awsDiscoveryStrategyFactory = new AwsDiscoveryStrategyFactory()
        Map<String, Comparable> properties = new HashMap<String, Comparable>()
        properties.put("access-key", grailsApplication.config.awshazel.accessHazelKey.toString())
        properties.put("secret-key", grailsApplication.config.awshazel.secretHazelKey.toString())
        properties.put("region", grailsApplication.config.awshazel.region.toString())
        properties.put("host-header", "ec2.amazonaws.com")
        properties.put("tagKey", grailsApplication.config.awshazel.hazelTagKey.toString())
        properties.put("tagValue", prefix + grailsApplication.config.awshazel.hazelTagKey.toString())
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(awsDiscoveryStrategyFactory, properties)
        log.debug "${discoveryStrategyConfig.getProperties()}"
        joinConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig)

        //if you want to configure multiple discovery strategies at once
        ArrayList<DiscoveryStrategyConfig> discoveryStrategyConfigs = new ArrayList<DiscoveryStrategyConfig>()
        joinConfig.getDiscoveryConfig().setDiscoveryStrategyConfigs(discoveryStrategyConfigs)*/
        log.info "B setting up hazel:${config.toString()}"
        hazelService.instance = Hazelcast.newHazelcastInstance(config)

    }
    def destroy = {
    }
}
