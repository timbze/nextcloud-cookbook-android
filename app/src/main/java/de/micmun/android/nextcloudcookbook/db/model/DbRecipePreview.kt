package de.micmun.android.nextcloudcookbook.db.model

data class DbRecipePreview (
    val id: Long,
    val name: String,
    val description: String = "",
    val thumbImageUrl: String = "",
) {
    companion object{
        const val DbFields = "id, name, description, thumbImageUrl"
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is DbRecipePreview) false else id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
