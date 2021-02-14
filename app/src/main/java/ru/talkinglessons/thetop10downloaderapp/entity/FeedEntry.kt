package ru.talkinglessons.thetop10downloaderapp.entity

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

    override fun toString(): String {
        return """
            name = $name
            artist = $artist
            releaseData = $releaseDate
            imageURL = $imageURL
            """.trimIndent()
    }
}