// ReceivedGoodsRepository.java
package com.example.demo.repository;

import com.example.demo.models.ReceivedGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivedGoodsRepository extends JpaRepository<ReceivedGoods, Long> {
}