package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.StickerPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StickerPackRepository extends JpaRepository<StickerPack, Long>, JpaSpecificationExecutor<StickerPack> {
}