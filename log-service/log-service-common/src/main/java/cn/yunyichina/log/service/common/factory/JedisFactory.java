package cn.yunyichina.log.service.common.factory;

import cn.yunyichina.log.common.entity.do_.RedisRecordDO;
import org.apache.commons.collections4.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Leo
 * @Blog: http://blog.csdn.net/lc0817
 * @CreateTime: 2017/4/2 15:52
 * @Description:
 */
public abstract class JedisFactory {
    /**
     * key: collectedItemId
     * value: JedisPool
     */
    protected ConcurrentHashMap<Integer, JedisPool> poolMap = new ConcurrentHashMap<>();
    protected ReentrantLock lock = new ReentrantLock();

    /**
     * 请在 @PreDestroy 时调用
     */
    public void closeAllJedisPools() {
        Collection<JedisPool> jedisPools = poolMap.values();
        if (CollectionUtils.isNotEmpty(jedisPools)) {
            for (JedisPool jedisPool : jedisPools) {
                jedisPool.destroy();
            }
        }
    }

    protected Jedis createJedisPoolAndReturnJedis(Integer collectorId, RedisRecordDO redisRecord) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(
                jedisPoolConfig,
                redisRecord.getIp(),
                redisRecord.getPort(),
                Protocol.DEFAULT_TIMEOUT,
                redisRecord.getPassword()
        );
        poolMap.put(collectorId, jedisPool);
        return jedisPool.getResource();
    }

    public static boolean trasactionFailure(List<Object> resultList) {
        if (resultList == null || resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
