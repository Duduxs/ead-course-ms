package com.ead.course.services

import com.ead.course.core.exceptions.NotFoundHttpException
import com.ead.course.core.extensions.end
import com.ead.course.core.extensions.start
import com.ead.course.domains.Course
import com.ead.course.domains.Module
import com.ead.course.dtos.CourseDTO
import com.ead.course.mappers.toDTO
import com.ead.course.mappers.toDomain
import com.ead.course.mappers.updateEntity
import com.ead.course.repositories.CourseRepository
import com.ead.course.repositories.LessonRepository
import com.ead.course.repositories.ModuleRepository
import mu.KLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val moduleRepository: ModuleRepository,
    private val lessonRepository: LessonRepository,
    private val logger: KLogger,
) {

    @Transactional(readOnly = true)
    fun findById(id: UUID) = courseRepository.findById(id)
        .orElseThrow { NotFoundHttpException("Course with id $id not found") }
        .toDTO()

    @Transactional(readOnly = true)
    fun findAll(): Collection<CourseDTO> = courseRepository.findAll().map { it.toDTO() }

    @Transactional
    fun save(dto: CourseDTO): CourseDTO {

        logger.start(this::save, dto)

        val entity = courseRepository.save(dto.toDomain()).toDTO()

        logger.end(this::save, entity)

        return entity
    }

    @Transactional
    fun update(id: UUID, dto: CourseDTO): CourseDTO {

        logger.start(this::update, dto, parameters = arrayOf(id))

        val course = courseRepository.findById(id).orElseThrow { NotFoundHttpException("Course with id $id not found") }

        val courseUpdated = updateEntity(course, dto)

        courseRepository.save(courseUpdated)

        logger.end(this::update)

        return courseUpdated.toDTO()
    }

    @Transactional
    fun deleteById(id: UUID) {

        logger.start(this::deleteById, parameters = arrayOf(id))

        val course = courseRepository.findById(id).orElseThrow { NotFoundHttpException("Course with id $id not found") }

        delete(course)

        logger.end(this::deleteById)
    }

    @Transactional
    fun delete(course: Course) {

        logger.start(this::delete, parameters = arrayOf(course.id))

        val modules: Collection<Module> = moduleRepository.findAllModulesBy(course.id)

        logger.start(this::delete, message = "modules size in course ${modules.size}")

        if (modules.isNotEmpty()) {
            for (module in modules) {
                val lessons = lessonRepository.findAllLessonsBy(module.id)

                logger.start(this::delete, message = "lessons size in module id ${module.id} ${lessons.size}")

                if (lessons.isNotEmpty()) lessonRepository.deleteAll(lessons)
            }
            moduleRepository.deleteAll(modules)
        }

        courseRepository.delete(course)

        logger.end(this::delete)
    }
}
