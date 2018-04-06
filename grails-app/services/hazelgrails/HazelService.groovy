package hazelgrails

import com.hazelcast.aws.AwsDiscoveryStrategyFactory
import com.hazelcast.config.Config
import com.hazelcast.config.DiscoveryStrategyConfig
import com.hazelcast.config.JoinConfig
import com.hazelcast.core.IExecutorService
import com.hazelcast.core.Member
import system.utils.SystemUtils

import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.locks.Lock

import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ITopic
import grails.transaction.Transactional

import javax.annotation.PostConstruct

@Transactional
class HazelService {

    static transactional = false

    HazelcastInstance instance
    def grailsApplication

    @PostConstruct
    void init() {
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
        properties.put("host-header","ec2.amazonaws.com")
        properties.put("tagKey", "hazelcluster")
        properties.put("tag-key", "hazelcluster")
        properties.put("tagValue", "hazelcluster")
        properties.put("tag-value", "hazelcluster")
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(awsDiscoveryStrategyFactory, properties)
        joinConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig)
        config.setInstanceName(SystemUtils.hostname)
        instance = Hazelcast.newHazelcastInstance()
    }

    Map map(String mapName) {
        instance.getMap(mapName)
    }



    Queue queue(String queueName) {
        instance.getQueue(queueName)
    }

    ITopic topic(String topicName) {
        instance.getTopic(topicName)
    }

    Set hashset(String setName) {
        instance.getSet(setName)
    }

    List list(String listName) {
        instance.getList(listName)
    }

    IExecutorService executorService(String executorName) {
        instance.getExecutorService(executorName)
    }

    Lock lock(Object lock) {
        instance.getLock(lock)
    }

    Map<Member, Future> executeOnAllMembers(Callable callable) {
        Map<Member, Future> result = instance.getExecutorService("default").submitToAllMembers(callable)
        return result
    }

    Object executeOnSomewhere(Callable callable) {
        return instance.getExecutorService("default").submit(callable).get()
    }

    Object executeOnMemberOwningTheKey(Callable callable, Object key) {
        return instance.getExecutorService("default").submitToKeyOwner(callable, key).get()
    }
}
