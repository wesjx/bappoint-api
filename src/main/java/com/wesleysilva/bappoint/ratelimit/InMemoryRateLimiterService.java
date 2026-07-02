package com.wesleysilva.bappoint.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryRateLimiterService implements RateLimiterService {
    private static final int CAPACITY = 10;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);
    private static final Duration EXPIRATION = Duration.ofMinutes(10);

    private final Map<String, BucketEntry> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean tryConsume(String key) {
        BucketEntry entry = buckets.compute(key, (k, existing) -> {
            if (existing == null || isExpired(existing)) {
                return new BucketEntry(newBucket(), Instant.now());
            }
            existing.lastUsed = Instant.now();
            return existing;
        });

        return entry.bucket.tryConsume(1);
    }

    private boolean isExpired(BucketEntry entry) {
        return entry.lastUsed.plus(EXPIRATION).isBefore(Instant.now());
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(CAPACITY, Refill.greedy(CAPACITY, REFILL_PERIOD));
        return Bucket.builder().addLimit(limit).build();
    }

    private static class BucketEntry {
        private final Bucket bucket;
        private Instant lastUsed;

        private BucketEntry(Bucket bucket, Instant lastUsed) {
            this.bucket = bucket;
            this.lastUsed = lastUsed;
        }
    }
}
