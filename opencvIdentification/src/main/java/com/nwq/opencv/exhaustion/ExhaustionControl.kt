package com.nwq.opencv.exhaustion



/**
create by: 86136
create time: 2023/8/31 15:25
Function description:
 */

object ExhaustionControl : BasicExhaustion() {


    private var FULL_STATE_TIME = 30 * SetConstant.MINUTE
    private var SLIGHT_EXHAUSTION_TIME = 2 * SetConstant.Hour + FULL_STATE_TIME
    private var SOME_EXHAUSTION_TIME = 1 * SetConstant.Hour + SLIGHT_EXHAUSTION_TIME
    private var VERY_EXHAUSTING_TIME = 30 * SetConstant.MINUTE + SOME_EXHAUSTION_TIME  //触发这个点的时候就必须要休息
    private val MAXWORKERTIME = 8 * SetConstant.Hour//最大工作时间

    private var Coefficient = 1.0   //每次休息后会越来越容易疲惫

    private var NOW_STATE = FULL_STATE

    private val RestBasicTime = 30 * SetConstant.MINUTE

    private var startTime = System.currentTimeMillis()


    //更新状态
    override fun updateStatus() {
        var nowTime = System.currentTimeMillis()
        when (NOW_STATE) {
            FULL_STATE -> {
                if (nowTime - startTime >= FULL_STATE_TIME) {
                    NOW_STATE = SLIGHT_EXHAUSTION
                }
            }
            SLIGHT_EXHAUSTION -> {
                if (nowTime - startTime >= SLIGHT_EXHAUSTION_TIME) {
                    NOW_STATE = SOME_EXHAUSTION
                }
            }
            SOME_EXHAUSTION -> {
                if (nowTime - startTime >= SOME_EXHAUSTION_TIME) {
                    NOW_STATE = VERY_EXHAUSTING
                }
            }
            VERY_EXHAUSTING -> {
                if (nowTime - startTime >= VERY_EXHAUSTING_TIME) {
                    NOW_STATE = NEED_REST
                }
            }
        }
    }

    override fun onRestComplete() {
        startTime = System.currentTimeMillis()
        val flag = Math.random() * 0.2 + 0.6

        FULL_STATE_TIME = (flag * FULL_STATE_TIME).toLong()
        SLIGHT_EXHAUSTION_TIME = (flag * SLIGHT_EXHAUSTION_TIME).toLong()
        SOME_EXHAUSTION_TIME = (flag * SOME_EXHAUSTION_TIME).toLong()
        VERY_EXHAUSTING_TIME = (flag * VERY_EXHAUSTING_TIME).toLong() //触发这个点的时候就必须要休息

        Coefficient *= flag
    }

    //是否进行休息
    override fun whetherToRest(): Long {
        if (NOW_STATE == FULL_STATE || NOW_STATE == SLIGHT_EXHAUSTION) {
            return 0L
        }
        if (NOW_STATE == NEED_REST) {
            return getRestTime()
        }
        val random = Math.random()
        if (SOME_EXHAUSTION == NOW_STATE && random > 0.8) {
            return getRestTime()
        }
        if (random > 0.4) {
            return getRestTime()
        }
        return 0L
    }

    override fun getRestTime(): Long {
        val random = Math.random()
        return ((1.5 - random * 0.5) * RestBasicTime).toLong()
    }

    override fun getClickDuration(type: Int): Long {
        return when (type) {
            OptDuration.QUICK -> {
                (Math.random() * 60 + 40).toLong()
            }
            OptDuration.SLOW -> {
                (Math.random() * 100 + 30).toLong()
            }
            OptDuration.QUICK_LARGE_RANDOM -> {
                (Math.random() * 50 + 100).toLong()
            }
            OptDuration.SLOW_LARGE_RANDOM -> {
                (Math.random() * 100 + 50).toLong()
            }
            else -> {
                (Math.random() * 60 + 40).toLong()
            }
        }
    }

    override fun getSwipDuration(type: Int): Long {
        return when (type) {
            OptDuration.QUICK -> {
                (Math.random() * 600 + 1000).toLong()
            }
            OptDuration.SLOW -> {
                (Math.random() * 1000 + 1500).toLong()
            }
            OptDuration.QUICK_LARGE_RANDOM -> {
                (Math.random() * 2000 + 600).toLong()
            }
            OptDuration.SLOW_LARGE_RANDOM -> {
                (Math.random() * 2000 + 2000).toLong()
            }
            else -> {
                (Math.random() * 600 + 600).toLong()
            }
        }
    }


    override fun getClickInterval(): Long {
        return when (getOptInterval()) {
            OptInterval.PRECISION -> {
                (Math.random() * 200 + 400).toLong()
            }
            OptInterval.CARELESS -> {
                (Math.random() * 400 + 450).toLong()
            }
            OptInterval.REDUNDANCY -> {
                (Math.random() * 200 + 500).toLong()
            }
            OptInterval.BLUNDER, OptInterval.FAILURE -> {
                (Math.random() * 500 + 300).toLong()
            }
            else -> {
                (Math.random() * 200 + 400).toLong()
            }
        }
    }


    //这里根据状态获取不同操作精度
    override fun getOptInterval(): Int {
        val random = Math.random()
        return when (NOW_STATE) {
            FULL_STATE -> {
                if (random > 0.8) {
                    OptInterval.CARELESS
                } else if (random > 0.99) {
                    OptInterval.REDUNDANCY
                } else {
                    OptInterval.PRECISION
                }
            }
            SLIGHT_EXHAUSTION -> {
                if (random > 0.65) {
                    OptInterval.CARELESS
                } else if (random > 0.95) {
                    OptInterval.REDUNDANCY
                } else if (random > 0.98) {
                    OptInterval.BLUNDER
                } else if (random > 0.995) {
                    OptInterval.FAILURE
                } else {
                    OptInterval.PRECISION
                }
            }
            SOME_EXHAUSTION -> {
                if (random > 0.5) {
                    OptInterval.CARELESS
                } else if (random > 0.9) {
                    OptInterval.REDUNDANCY
                } else if (random > 0.95) {
                    OptInterval.BLUNDER
                } else if (random > 0.99) {
                    OptInterval.FAILURE
                } else {
                    OptInterval.PRECISION
                }
            }
            VERY_EXHAUSTING -> {
                if (random > 0.3) {
                    OptInterval.CARELESS
                } else if (random > 0.8) {
                    OptInterval.REDUNDANCY
                } else if (random > 0.95) {
                    OptInterval.BLUNDER
                } else if (random > 0.99) {
                    OptInterval.FAILURE
                } else {
                    OptInterval.PRECISION
                }
            }
            else -> {
                OptInterval.PRECISION
            }
        }
    }

    fun getClickParameter(): ClickParameter {
        val random = Math.random()
        return ClickParameter(getOptRange(random), getOptSlide(random), getOptDuration(random))
    }

    private fun getOptDuration(random: Double): Int {
        return when (NOW_STATE) {
            FULL_STATE -> {
                if (random > 0.6) {
                    OptDuration.SLOW
                } else if (random > 0.7) {
                    OptDuration.QUICK_LARGE_RANDOM
                } else if (random > 0.9) {
                    OptDuration.SLOW_LARGE_RANDOM
                } else {
                    OptDuration.QUICK
                }
            }
            SLIGHT_EXHAUSTION -> {
                if (random > 0.65) {
                    OptDuration.SLOW
                } else if (random > 0.95) {
                    OptDuration.QUICK_LARGE_RANDOM
                } else if (random > 0.98) {
                    OptDuration.SLOW_LARGE_RANDOM
                } else {
                    OptDuration.QUICK
                }
            }
            SOME_EXHAUSTION -> {
                if (random > 0.5) {
                    OptDuration.SLOW
                } else if (random > 0.9) {
                    OptDuration.QUICK_LARGE_RANDOM
                } else if (random > 0.95) {
                    OptDuration.SLOW_LARGE_RANDOM
                } else {
                    OptDuration.QUICK
                }
            }
            VERY_EXHAUSTING -> {
                if (random > 0.3) {
                    OptDuration.SLOW
                } else if (random > 0.8) {
                    OptDuration.QUICK_LARGE_RANDOM
                } else if (random > 0.95) {
                    OptDuration.SLOW_LARGE_RANDOM
                } else {
                    OptDuration.QUICK
                }
            }
            else -> {
                OptDuration.QUICK
            }
        }
    }


    private fun getOptRange(random: Double): Int {
        return when (NOW_STATE) {
            FULL_STATE -> {
                if (random > 0.6) {
                    OptRange.WIDE_RANGE
                } else if (random > 0.8) {
                    OptRange.FULL_RANGE
                } else if (random > 0.95) {
                    OptRange.ALL_OPT_RANGE
                }else {
                    OptRange.SMALL_PRECISION
                }
            }
            SLIGHT_EXHAUSTION -> {
                if (random > 0.3) {
                    OptRange.WIDE_RANGE
                } else if (random > 0.5) {
                    OptRange.FULL_RANGE
                } else if (random > 0.7) {
                    OptRange.ALL_OPT_RANGE
                }  else {
                    OptRange.SMALL_PRECISION
                }
            }
            SOME_EXHAUSTION -> {
                if (random > 0.2) {
                    OptRange.WIDE_RANGE
                } else if (random > 0.4) {
                    OptRange.FULL_RANGE
                } else if (random > 0.6) {
                    OptRange.ALL_OPT_RANGE
                }else {
                    OptRange.SMALL_PRECISION
                }
            }
            VERY_EXHAUSTING -> {
                if (random > 0.1) {
                    OptRange.WIDE_RANGE
                } else if (random > 0.3) {
                    OptRange.FULL_RANGE
                } else if (random > 0.5) {
                    OptRange.ALL_OPT_RANGE
                }else {
                    OptRange.SMALL_PRECISION
                }
            }
            else -> {
                OptRange.SMALL_PRECISION
            }
        }
    }

    private fun getOptSlide(random: Double): Int {
        return when (NOW_STATE) {
            FULL_STATE -> {
                if (random > 0.95) {
                    OptSlide.SLIDE_ONE
                } else {
                    OptSlide.NOT_SLIDE
                }
            }
            SLIGHT_EXHAUSTION -> {
                if (random > 0.9) {
                    OptSlide.SLIDE_ONE
                } else if (random > 0.99) {
                    OptSlide.SLIDE_TWO
                } else {
                    OptSlide.NOT_SLIDE
                }
            }
            SOME_EXHAUSTION -> {
                if (random > 0.85) {
                    OptSlide.SLIDE_ONE
                } else if (random > 0.98) {
                    OptSlide.SLIDE_TWO
                } else {
                    OptSlide.NOT_SLIDE
                }
            }
            VERY_EXHAUSTING -> {
                if (random > 0.8) {
                    OptSlide.SLIDE_ONE
                } else if (random > 0.97) {
                    OptSlide.SLIDE_TWO
                } else {
                    OptSlide.NOT_SLIDE
                }
            }
            else -> {
                OptRange.SMALL_PRECISION
            }
        }
    }

}