package com.nwq.opencv.hsv




open class HSVRule(
    var minH: Int=0,
    var maxH: Int=180,
    var minS: Int=0,
    var maxS: Int=255,
    var minV: Int=0,
    var maxV: Int=255
) {


    companion object {
        val allHSVRule = listOf(
            StandardWhiteHSV(),
            StandardGrayHSV(),
            StandardBlackHSV(),
            StandardRedHSV(),
            StandardRed2HSV(),
            StandardOrangeHSV(),
            StandardYellowHSV(),
            StandardGreenHSV(),
            StandardBlueHSV(),
            StandardQingHSV(),
            StandardPurpleHSV(),
            StandardPinkHSV()
        )


        fun getSimple(
            h: Int,
            s: Int,
            v: Int,
        ): HSVRule {
            return HSVRule(h, h, s, s, v, v)
        }

    }

    fun verificationRule(h: Int, s: Int, v: Int): Boolean {
        return h in minH..maxH && s in minS..maxS && v in minV..maxV
    }

    // 这个字段将不会被序列化和反序列化
    @Transient
    private var mSelected = false

    fun getIsSelected(): Boolean {
        return mSelected
    }

     fun setIsSelected(isSelected: Boolean) {
        mSelected = isSelected
    }

    override fun toString(): String {
        return "HSVRule(minH=$minH, maxH=$maxH, minS=$minS, maxS=$maxS, minV=$minV, maxV=$maxV)"
    }

    fun toStringSimple(): String {
        return "HSVRule(nH=$minH, xH=$maxH, nS=$minS, xS=$maxS, nV=$minV, xV=$maxV)"
    }
}