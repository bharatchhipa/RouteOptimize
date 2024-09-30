package com.celebal.route.repository;

import com.celebal.route.entity.ApiReqResp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiReqRespRepository extends JpaRepository<ApiReqResp, Long> {
}
