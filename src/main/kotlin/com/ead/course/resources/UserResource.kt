package com.ead.course.resources

import com.ead.course.core.extensions.end
import com.ead.course.core.extensions.makeLogged
import com.ead.course.core.extensions.start
import com.ead.course.dtos.SubscriptionDTO
import com.ead.course.dtos.UserDTO
import com.ead.course.entities.Course
import com.ead.course.entities.User
import com.ead.course.services.CourseService
import com.ead.course.services.UserService
import mu.KLogging
import net.kaczmarzyk.spring.data.jpa.domain.Equal
import net.kaczmarzyk.spring.data.jpa.domain.Like
import net.kaczmarzyk.spring.data.jpa.web.annotation.And
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID
import javax.validation.Valid

@RestController
@CrossOrigin(origins = ["*"], maxAge = 3600)
class UserResource(
    private val service: CourseService,
    private val userService: UserService,
) {

    @GetMapping("courses/{courseId}/users")
    fun findAllBy(
        @And(
            Spec(path = "email", spec = Like::class),
            Spec(path = "fullName", spec = Like::class),
            Spec(path = "status", spec = Equal::class),
            Spec(path = "type", spec = Equal::class)
        ) spec: Specification<User>?,
        @PathVariable("courseId") courseId: UUID,
        @PageableDefault(sort = ["id"], direction = ASC) pageable: Pageable
    ): ResponseEntity<Page<UserDTO>> = logger.makeLogged(this::findAllBy, parameters = arrayOf(courseId)) {

        service.findById(courseId)

        val response = userService.findAll(spec, courseId, pageable)

        ResponseEntity.ok(response)

    }

    @PostMapping("courses/{courseId}/users/subscription")
    fun subscribeUserInCourse(
        @PathVariable("courseId") courseId: UUID,
        @RequestBody @Valid dto: SubscriptionDTO
    ): ResponseEntity<Any> {

        logger.start(this::subscribeUserInCourse, body = dto, parameters = arrayOf(courseId))

        val course = service.findById(courseId)
        //verificação state transfer
        val uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(UUID.randomUUID())
            .toUri()

        logger.end(this::subscribeUserInCourse)

        return ResponseEntity.created(uri).body(listOf<Any>())

    }

    companion object : KLogging()
}