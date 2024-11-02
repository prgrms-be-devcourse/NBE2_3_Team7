package com.hunmin.domain

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = ["com.hunmin.domain.repository"])
@EnableRedisRepositories(basePackages = ["com.hunmin.domain.redis.repository"])
class Nbe23Team7Application

fun main(args: Array<String>) {
	runApplication<Nbe23Team7Application>(*args)
}
