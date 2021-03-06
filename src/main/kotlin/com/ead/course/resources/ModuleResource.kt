package com.ead.course.resources

import com.ead.course.core.extensions.end
import com.ead.course.core.extensions.makeLogged
import com.ead.course.core.extensions.start
import com.ead.course.dtos.ModuleDTO
import com.ead.course.entities.Module
import com.ead.course.services.ModuleService
import mu.KLogger
import net.kaczmarzyk.spring.data.jpa.domain.Like
import net.kaczmarzyk.spring.data.jpa.web.annotation.And
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID
import javax.validation.Valid

@RestController
@CrossOrigin("*", maxAge = 3600)
class ModuleResource(
    private val service: ModuleService,
    private val logger: KLogger
) {

    @GetMapping("modules/{moduleId}")
    @PreAuthorize("hasAnyRole('STUDENT')")
    fun findById(@PathVariable moduleId: UUID): ResponseEntity<ModuleDTO> =
        logger.makeLogged(function = this::findById, parameters = arrayOf(moduleId)) {

            val entity = service.findById(moduleId)

            ResponseEntity.ok(entity)

        }
    @GetMapping("/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('STUDENT')")
    fun findAllInCourse(
        @And(
            Spec(path = "title", spec = Like::class)
        ) spec: Specification<Module>?,
        @PathVariable courseId: UUID,
        @PageableDefault(direction = ASC) pageable: Pageable,
    ): ResponseEntity<Page<ModuleDTO>> {

        logger.start(this::findAllInCourse)

        val entities = service.findAll(courseId, spec, pageable)

        logger.end(this::findAllInCourse)

        return ResponseEntity.ok(entities)

    }

    @PostMapping("courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    fun create(@PathVariable courseId: UUID, @Valid @RequestBody dto: ModuleDTO): ResponseEntity<ModuleDTO> {

        logger.start(this::create, dto, parameters = arrayOf(courseId))

        val entity = service.save(courseId, dto)

        val uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(entity.id)
            .toUri()

        logger.end(this::create)

        return ResponseEntity.created(uri).body(entity)

    }

    @PutMapping("modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    fun update(@PathVariable moduleId: UUID, @Valid @RequestBody dto: ModuleDTO): ResponseEntity<ModuleDTO> {

        logger.start(this::update, body = dto, parameters = arrayOf(moduleId))

        val updatedEntity = service.update(moduleId, dto)

        logger.end(this::update, updatedEntity)

        return ResponseEntity.ok(updatedEntity)

    }

    @DeleteMapping("modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    fun delete(@PathVariable moduleId: UUID): ResponseEntity<Void> =
        logger.makeLogged(function = this::delete, parameters = arrayOf(moduleId)) {

            service.deleteById(moduleId)

            ResponseEntity.noContent().build()

        }
}