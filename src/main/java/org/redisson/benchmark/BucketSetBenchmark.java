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

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class BucketSetBenchmark {

    public static void main(String[] args) throws InterruptedException {
        Bench<RedissonClient> bench = new RedissonBench() {
            @Override
            public void executeOperation(String data, RedissonClient benchInstance, int threadNumber, int iteration,
                    MetricRegistry metrics) {
                RBucket<String> bucket = benchInstance.getBucket("bucket_" + threadNumber + "_" + iteration);
                Timer.Context time = metrics.timer("bucket").time();
                bucket.set(data);
                time.stop();
            }
        };
        
        Benchmark benchmark = new Benchmark(bench);
        benchmark.run(args);
    }
    
}
