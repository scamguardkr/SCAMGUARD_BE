package kr.cse.scamguard.common.security.jwt.refresh.repository

import org.springframework.data.repository.CrudRepository
import kr.cse.scamguard.common.security.jwt.refresh.model.RefreshTokenEntity

interface RefreshTokenRepository : RefreshTokenCustomRepository, CrudRepository<RefreshTokenEntity, String>
