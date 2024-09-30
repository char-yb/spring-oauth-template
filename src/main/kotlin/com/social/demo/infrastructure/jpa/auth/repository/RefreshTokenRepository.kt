package com.social.demo.infrastructure.jpa.auth.repository

import com.social.demo.infrastructure.jpa.auth.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, String>
