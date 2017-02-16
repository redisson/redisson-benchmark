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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class Benchmark {

    private final int[] threads = {1, 2, 4, 8, 16, 32, 64, 128, 256};
    private MetricRegistry metrics;
    private ConsoleReporter reporter;
    
    private Bench<Object> bench;
    
    public Benchmark(Bench<?> bench) {
        super();
        this.bench = (Bench<Object>) bench;
    }

    public void run(String[] args) throws InterruptedException {
        int threads = 64;
        int iteration = 100000;
        int connections = 10;
        String host = "127.0.0.1:6379";
        if (args.length > 0) {
            threads = Integer.valueOf(args[0]); //64;
            iteration = Integer.valueOf(args[1]);//100;
            connections = Integer.valueOf(args[2]);//10
            host = args[3];
        }
        run(threads, iteration, connections, host);
    }
    
    public void run(int threads, final int iterations, int connections, String host) throws InterruptedException {
        
        for (final int threadsAmount : this.threads) {
            final Object client = bench.createInstance(connections, host);
            ExecutorService e = Executors.newFixedThreadPool(threadsAmount);
            
            metrics = SharedMetricRegistries.getOrCreate("redisson");
            
            reporter = ConsoleReporter.forRegistry(metrics)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MICROSECONDS)
                    .build();
            
            final CountDownLatch l = new CountDownLatch(threadsAmount);
            final AtomicBoolean print = new AtomicBoolean();
            final int len = String.valueOf(iterations).length();
            for (int i = 0; i < threadsAmount; i++) {
                final int threadNumber = i;
                e.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < iterations; j++) {
                            String data = String.format("%1$" + len + "s", j);
                            bench.executeOperation(data, client, threadNumber, j, metrics);
                        }
                        if (print.compareAndSet(false, true)) {
                            System.out.println("Threads: " + threadsAmount);
                            reporter.report();
                        }
                        
                        l.countDown();
                    }
                    
                });
            }
            l.await();
            
            SharedMetricRegistries.remove("redisson");
            
            e.shutdown();
            bench.shutdown(client);
        }
        
//        for (final int threadsAmount : this.threads) {
//            String[] parts = host.split(":");
//            
//            JedisPool jedisPool = new JedisPool(parts[0], Integer.valueOf(parts[1]));
//            ExecutorService e = Executors.newFixedThreadPool(threadsAmount);
//            
//            metrics = SharedMetricRegistries.getOrCreate("jedis");
//            
//            reporter = ConsoleReporter.forRegistry(metrics)
//                    .convertRatesTo(TimeUnit.SECONDS)
//                    .convertDurationsTo(TimeUnit.MICROSECONDS)
//                    .build();
//            
//            
//            final CountDownLatch l = new CountDownLatch(threadsAmount);
//            final AtomicBoolean print = new AtomicBoolean();
//            final int len = String.valueOf(iterations).length();
//            for (int i = 0; i < threadsAmount; i++) {
//                final int threadNumber = i;
//                e.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int j = 0; j < iterations; j++) {
//                            Jedis jedis = jedisPool.getResource();
//                            String data = String.format("%1$" + len + "s", j);
//                            executeOperation(data, jedis, threadNumber, j);
//                            jedis.close();
//                        }
//                        if (print.compareAndSet(false, true)) {
//                            System.out.println("Jedis threads: " + threadsAmount);
//                            reporter.report();
//                        }
//                        
//                        l.countDown();
//                    }
//                    
//                });
//            }
//            l.await();
//            
//            SharedMetricRegistries.remove("jedis");
//            
//            e.shutdown();
//            jedisPool.close();
//        }
    }
    
}
