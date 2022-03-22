package com.ead.course.services

import com.ead.course.core.extensions.end
import com.ead.course.core.extensions.start
import com.ead.course.dtos.UserDTO
import com.ead.course.entities.Course
import com.ead.course.entities.User
import com.ead.course.mappers.toDTO
import com.ead.course.repositories.UserRepository
import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Root

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun findAll(
        defaultSpec: Specification<User>?,
        courseId: UUID,
        pageable: Pageable,
    ): Page<UserDTO> {

        logger.start(this::findAll)

        val spec = Specification { root: Root<User>, query: CriteriaQuery<*>, cb: CriteriaBuilder ->
            query.distinct(true)
            val course: Root<Course> = query.from(Course::class.java)
            val coursesUsers: Expression<Collection<User>> = course.get("users")
            cb.and(cb.equal(course.get<Root<User>>("courseId"), courseId), cb.isMember(root, coursesUsers))
        }.and(defaultSpec)

        logger.end(this::findAll)

        return userRepository.findAll(spec, pageable).map { it.toDTO() }

    }


    companion object : KLogging()
}