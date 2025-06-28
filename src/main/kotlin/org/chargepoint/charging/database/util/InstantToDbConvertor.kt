package org.chargepoint.charging.database.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.sql.Timestamp

@Converter(autoApply = true)
class InstantToDbConvertor : AttributeConverter<Instant, Timestamp> {
    override fun convertToDatabaseColumn(attribute: Instant?): Timestamp? {
        return attribute?.toJavaInstant()?.let { Timestamp.from(it) }
    }

    override fun convertToEntityAttribute(dbData: Timestamp?): Instant? {
        return dbData?.toInstant()?.toKotlinInstant()
    }
}