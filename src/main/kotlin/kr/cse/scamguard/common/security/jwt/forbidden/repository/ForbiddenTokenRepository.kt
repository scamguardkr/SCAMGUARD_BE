package kr.cse.scamguard.common.security.jwt.forbidden.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import kr.cse.scamguard.common.security.jwt.forbidden.model.ForbiddenTokenEntity

interface ForbiddenTokenRepository : CrudRepository<ForbiddenTokenEntity, String>
