package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long>, JpaSpecificationExecutor<SearchHistory> {
}