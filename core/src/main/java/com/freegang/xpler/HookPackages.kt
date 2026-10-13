package com.freegang.xpler


/**
 * 你只需要在下面增加需要被hook的app包名,
 * 然后在 `HookMain` 中写你的Hook逻辑
 */
object HookPackages {
    const val appPackageName = "com.freegang.fplus"
    const val corePackageName = "com.freegang.xpler"
    const val douYinPackageName = "com.ss.android.ugc.aweme"

    //抖音 Google Play 版 (可与国内版共存, 包名不同)
    const val douYinGpPackageName = "com.ss.android.ugc.aweme.mobile"


    val packages = listOf(
        appPackageName,
        corePackageName,
        douYinPackageName,
        douYinGpPackageName,
    )
}