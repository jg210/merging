package uk.me.jeremygreen.merging.main

interface ScreenFragmentFactory<T: ScreenFragment> {

    /**
     * The ViewPager2 (RecyclerView) id. Images use non-negative Ints, and ViewPager2 uses -1.
     */
    val id: Long

    fun createInstance(): T

    /**
     * Firebase Analytics "screen name".
     */
    fun screenName(): String

}