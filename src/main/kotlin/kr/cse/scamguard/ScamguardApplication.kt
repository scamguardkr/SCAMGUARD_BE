package kr.cse.scamguard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class ScamguardApplication

fun main(args: Array<String>) {
	runApplication<ScamguardApplication>(*args)
}
