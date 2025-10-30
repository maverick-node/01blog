package com._blog.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._blog.myblog.model.SubscriptionStruct;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<SubscriptionStruct, Integer> {
    List<SubscriptionStruct> findBySubscriberId(Integer subscriberId);
    List<SubscriptionStruct> findByTargetId(Integer targetId);
    boolean existsBySubscriberIdAndTargetId(Integer subscriberId, Integer targetId);
    void deleteBySubscriberIdAndTargetId(Integer subscriberId, Integer targetId);
}
