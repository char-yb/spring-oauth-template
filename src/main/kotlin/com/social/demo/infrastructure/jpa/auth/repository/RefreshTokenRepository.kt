package com.social.demo.infrastructure.jpa.auth.repository

import com.social.demo.infrastructure.jpa.auth.entity.RefreshToken
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, String>
