package ch.frankel.blog.springfu.migrationdemo

import org.springframework.boot.WebApplicationType.SERVLET
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.time.LocalDate
import javax.persistence.*

fun main(args: Array<String>) {
    application(SERVLET) {
        beans {
            bean<PersonRepository>()
            bean<PersonHandler>()
        }
        webMvc {
            router {
                "/person".nest {
                    GET("/{id}", ref<PersonHandler>()::readOne)
                    GET("/") { ref<PersonHandler>().readAll() }
                }
            }
            converters {
                string()
                jackson()
            }
        }
    }.run(args)
}

class PersonHandler(private val personRepository: PersonRepository) {
    fun readAll() = ServerResponse.ok().body(personRepository.findAll())
    fun readOne(request: ServerRequest) = ServerResponse.ok().body(personRepository.findById(request.pathVariable("id").toLong()))
}

@Entity
class Person(@Id val id: Long, val firstName: String, val lastName: String, val birthdate: LocalDate? = null)

interface PersonRepository : PagingAndSortingRepository<Person, Long>