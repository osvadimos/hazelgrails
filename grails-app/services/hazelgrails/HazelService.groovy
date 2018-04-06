package hazelgrails

import com.hazelcast.core.IExecutorService
import com.hazelcast.core.Member

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

    @PostConstruct
    void init() {
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
