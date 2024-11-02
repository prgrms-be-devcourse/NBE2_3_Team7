package com.hunmin.domain

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
class Nbe23Team7Application

fun main(args: Array<String>) {
	runApplication<Nbe23Team7Application>(*args)
}
