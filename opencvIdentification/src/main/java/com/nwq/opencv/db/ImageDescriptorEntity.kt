import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_descriptors")
data class ImageDescriptorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val keyTag: String,
    val detectionType: String,
    var checkNumber: Int,
    var passNumber: Int,
    var errorNumber: Int,
    val matCols: Int,
    val matRows: Int,
    val descriptors: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ImageDescriptorEntity
        if (id != other.id) return false
        if (keyTag != other.keyTag) return false
        if (detectionType != other.detectionType) return false
        if (matCols != other.matCols) return false
        if (matRows != other.matRows) return false
        if (!descriptors.contentEquals(other.descriptors)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + keyTag.hashCode()
        result = 31 * result + detectionType.hashCode()
        result = 31 * result + matCols
        result = 31 * result + matRows
        result = 31 * result + descriptors.contentHashCode()
        return result
    }

}
