package com.hcmute.shopfee.service.elasticsearch;

import com.hcmute.shopfee.entity.elasticsearch.TrackingUserProductIndex;
import com.hcmute.shopfee.repository.elasticsearch.TrackingUserClickProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingUserProductEService {
    private final TrackingUserClickProductRepository trackingUserClickProductRepository;
    public Page<TrackingUserProductIndex> getProductByUserId(String userId, int size) {
        Sort sort = Sort.by(Sort.Order.desc("lastSeen"));
        Pageable pageable = PageRequest.of(0, size, sort);
        return trackingUserClickProductRepository.findByUserId(userId, pageable);
    }
}
