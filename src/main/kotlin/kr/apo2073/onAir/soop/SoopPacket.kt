package kr.apo2073.onAir.soop

import java.time.LocalDateTime

class SoopPacket(args: List<String>) {
    val command: String
    val dataList: List<String>
    val receivedTime: LocalDateTime = LocalDateTime.now()

    init {
        require(args.isNotEmpty()) { "args cannot be empty" }
        command = args.first().take(4)
        dataList = args.drop(1)
    }

    override fun toString(): String {
        return buildString {
            append("Command: ").append(command).append("\n")
            append("Data: ").append(dataList.joinToString(" "))
        }
    }
}
