package com.hcmute.shopfee.service.elasticsearch;

import com.hcmute.shopfee.entity.elasticsearch.TrackingUserProductIndex;
import com.hcmute.shopfee.repository.elasticsearch.TrackingUserClickProductESRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingUserProductESService {
    private final TrackingUserClickProductESRepository trackingUserClickProductESRepository;
    public Page<TrackingUserProductIndex> getProductByUserId(String userId, int size) {
        Sort sort = Sort.by(Sort.Order.desc("lastSeen"));
        Pageable pageable = PageRequest.of(0, size, sort);
        return trackingUserClickProductESRepository.findByUserId(userId, pageable);
    }
}
