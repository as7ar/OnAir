package kr.apo2073.onAir.soop

class SoopLiveInfo(
    private val CHDOMAIN: String?,
    private val CHATNO: String?,
    private val FTK: String?,
    private val TITLE: String?,
    private val BJID: String?,
    private val BNO: String?,
    private val CHPT: String?
) {
    fun CHDOMAIN(): String? = this.CHDOMAIN
    fun CHATNO(): String? = this.CHATNO
    fun FTK(): String? = this.FTK
    fun TITLE(): String? = this.TITLE
    fun BJID(): String? = this.BJID
    fun BNO(): String? =this.BNO
    fun CHPT(): String? =this.CHPT
}