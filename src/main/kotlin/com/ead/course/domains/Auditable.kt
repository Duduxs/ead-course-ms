package com.ead.course.domains

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable {

    @field:CreatedDate
    @field:JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @field:Column(nullable = false)
    private var creationDate: LocalDateTime = LocalDateTime.now()

    @field:LastModifiedDate
    @field:JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @field:Column(nullable = false)
    private val lastUpdateDate: LocalDateTime? = null
}