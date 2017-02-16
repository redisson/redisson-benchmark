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
import org.redisson.benchmark.Benchmark;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class MapFastPutBenchmark {

    public static void main(String[] args) throws InterruptedException {
        Bench<JedisPool> bench = new JedisBench() {
            @Override
            public void executeOperation(String data, JedisPool benchInstance, int threadNumber, int iteration,
                    MetricRegistry metrics) {
                Jedis jedis = benchInstance.getResource();

                Timer.Context time = metrics.timer("map").time();
                jedis.hset("map_" + threadNumber, data, data);
                time.stop();

                jedis.close();
            }
        };
        
        Benchmark benchmark = new Benchmark(bench);
        benchmark.run(args);
    }
    
}
