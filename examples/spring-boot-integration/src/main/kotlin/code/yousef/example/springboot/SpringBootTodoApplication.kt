package code.yousef.example.springboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class SpringBootTodoApplication

fun main(args: Array<String>) {
    runApplication<SpringBootTodoApplication>(*args)
}