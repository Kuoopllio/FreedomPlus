package com.freegang.douyin

import android.app.Application
import com.freegang.base.BaseHook
import com.freegang.config.Config
import com.freegang.xpler.utils.app.KAppCrashUtils
import com.freegang.xpler.utils.io.KStorageUtils.hasOperationStorage
import com.freegang.xpler.utils.log.KLogCat
import com.freegang.xpler.xp.hookClass
import com.ss.android.ugc.aweme.app.host.AwemeHostApplication
import de.robv.android.xposed.callbacks.XC_LoadPackage

class DouYinMain(
    lpparam: XC_LoadPackage.LoadPackageParam,
    private val application: Application,
) : BaseHook(lpparam) {
    override fun onHook() {
        appInit()
    }

    private fun appInit() {
        lpparam.hookClass(AwemeHostApplication::class.java)
            .method("onCreate") {
                onBefore {
                    val app = thisObject as Application

                    //日志工具
                    KLogCat.init(app)
                    KLogCat.openStorage()

                    //全局异常捕获工具
                    KAppCrashUtils.instance.init(app, "抖音异常崩溃!")

                    //文件读写权限检查
                    if (!application.hasOperationStorage) {
                        showToast(application, "抖音没有文件读写权限!")
                        return@onBefore
                    }

                    //Hook
                    Config.read(app)
                    //逐个隔离注册: 某个类在新版本不存在时, 只跳过它, 不影响后面的 Hook
                    hookSafely("HMainActivity") { HMainActivity(lpparam, app) }
                    hookSafely("HAbsActivity") { HAbsActivity(lpparam) }
                    hookSafely("HMainFragment") { HMainFragment(lpparam) }
                    //表情包相关
                    //16.4.15 (Google Play 版) 起 emoji.model.Emoji / EmojiDetailDialogNew / EmojiBottomSheetDialog
                    //已从抖音中移除, 注册必定抛 ClassNotFoundError, 故暂时关闭。
                    //hookSafely("HEmojiDetailDialogNew") { HEmojiDetailDialogNew(lpparam) }
                }
            }
    }

    /**
     * 隔离执行单个 Hook 注册, 失败只记录日志, 不中断后续 Hook
     */
    private fun hookSafely(tag: String, block: () -> Unit) {
        try {
            block.invoke()
        } catch (e: Throwable) {
            KLogCat.e("$tag 注册失败: \n${e.stackTraceToString()}")
        }
    }
}
