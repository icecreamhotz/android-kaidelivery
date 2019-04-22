package app.icecreamhot.kaidelivery.model.Chat

class ChatMessage(val fromId: Int, val message: String, val timestamp: Long, val toId: Int, val read: Boolean) {
    constructor() : this(0, "", -1, 0, false)
}