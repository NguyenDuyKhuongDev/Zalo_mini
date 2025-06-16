package com.devmam.zalomini.repositories;

import com.devmam.zalomini.entities.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StickerRepository extends JpaRepository<Sticker, Long>, JpaSpecificationExecutor<Sticker> {
}