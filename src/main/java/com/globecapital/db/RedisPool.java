    package com.globecapital.db;

import java.io.IOException;
import java.time.Duration;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.msf.log.Logger;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

	private static Logger log = Logger.getLogger(RedisPool.class);

    private static JedisCluster cluster;

	public RedisPool() {

	}

	static {
		try {
			initialiseCluster();
		}catch(Exception e) {
			log.error(e);
		}
    }

	private static void initialiseCluster() {
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        nodes.add(new HostAndPort("21.21.21.38", 7700));
        nodes.add(new HostAndPort("21.21.21.37", 7700));
        nodes.add(new HostAndPort("21.21.21.41", 7700));
        final JedisPoolConfig config = buildPoolConfig();

        cluster = new JedisCluster(nodes, BinaryJedisCluster.DEFAULT_TIMEOUT, BinaryJedisCluster.DEFAULT_TIMEOUT, BinaryJedisCluster.DEFAULT_MAX_ATTEMPTS, "gL0be321", config);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (cluster != null) {
                    	log.error("Closing jedis connections");
						cluster.close();
                }
            }
        });
	}
	
	public static JedisCluster getCluster() {
		return cluster;
	}
	
	private static JedisPoolConfig buildPoolConfig() {
		final JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(512);
		poolConfig.setMaxIdle(512);
		poolConfig.setMinIdle(64);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
		poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
		poolConfig.setNumTestsPerEvictionRun(3);
		poolConfig.setBlockWhenExhausted(true);
		return poolConfig;
	}

	public JedisCluster getConnection() {
		return cluster;
	}

	public void releaseConnection() {
		if (Objects.nonNull(cluster))
			cluster.close();
	}

	public void setValues(String key, String value) {
		cluster.set(key, value);

		Calendar calendar = Calendar.getInstance();
		long expiryTime;

		if (calendar.get(Calendar.HOUR_OF_DAY) <= 07 && calendar.get(Calendar.MINUTE) <= 30) {
			calendar.set(Calendar.HOUR_OF_DAY, 07);
			calendar.set(Calendar.MINUTE, 30);
			expiryTime = calendar.getTimeInMillis() / 1000L;
		} else {
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
			calendar.set(Calendar.HOUR_OF_DAY, 07);
			calendar.set(Calendar.MINUTE, 30);
			expiryTime = calendar.getTimeInMillis() / 1000L;
		}
		cluster.expireAt(key, expiryTime);
	}

	public String getValue(String key) {
		return cluster.get(key);
	}

	public boolean isExists(String key) {
		return cluster.exists(key);
	}
	
	public static void main(String[] args) {
		RedisPool redisPool = new RedisPool();
//		JedisCluster redis = redisPool.getCluster();
		redisPool.isExists("x");
	}
}
