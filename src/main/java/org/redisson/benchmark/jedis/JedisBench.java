/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.benchmark.jedis;

import org.redisson.benchmark.Bench;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public abstract class JedisBench implements Bench<JedisPool> {

    @Override
    public JedisPool createInstance(int connections, String host) {
        String[] parts = host.split(":");
        JedisPool jp = new JedisPool(parts[0], Integer.valueOf(parts[1]));
        Jedis jedis = jp.getResource();
        jedis.flushDB();
        jedis.close();
        return jp;
    }

    @Override
    public void shutdown(JedisPool instance) {
        instance.close();
    }
    
}
