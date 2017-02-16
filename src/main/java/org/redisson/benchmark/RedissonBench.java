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
package org.redisson.benchmark;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public abstract class RedissonBench implements Bench<RedissonClient> {

    @Override
    public RedissonClient createInstance(int connections, String host) {
        Config c = new Config();
//        c.setUseLinuxNativeEpoll(true);
//        c.useClusterServers()
        c.useSingleServer()
        .setTimeout(10000000)
        .setAddress(host)
        .setConnectionPoolSize(connections).setConnectionMinimumIdleSize(connections);
//        .addNodeAddress(host)
//        .setMasterConnectionPoolSize(connections).setMasterConnectionMinimumIdleSize(connections);
        c.setCodec(StringCodec.INSTANCE);

        RedissonClient r = Redisson.create(c);
        r.getKeys().flushdb();
        return r;
    }

    @Override
    public void shutdown(RedissonClient instance) {
        instance.shutdown();
    }
    
}
