package com.freegang.douyin

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.freegang.base.BaseHook
import com.freegang.config.Config
import com.freegang.xpler.utils.view.KViewUtils
import com.freegang.xpler.xp.hook
import com.freegang.xpler.xp.hookClass
import com.ss.android.ugc.aweme.homepage.ui.view.MainFlippableViewPager
import com.ss.android.ugc.aweme.homepage.ui.view.MainTabStrip
import com.ss.android.ugc.aweme.homepage.ui.view.MainTabStripScrollView
import com.ss.android.ugc.aweme.main.MainFragment
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HMainFragment(
    lpparam: XC_LoadPackage.LoadPackageParam,
) : BaseHook(lpparam) {
    override fun onHook() {
        val config = Config.get()

        lpparam.hookClass(MainFragment::class.java)
            .method("onViewCreated", View::class.java, Bundle::class.java) {
                onAfter {
                    if (!config.isHideTab) return@onAfter
                    val viewGroup = args[0] as ViewGroup
                    hideTabItem(viewGroup, config)
                }
            }

        //禁止ViewPager左右滑动
        hookFlippableViewPager(lpparam, config)
    }

    /**
     * 禁止首页左右滑动
     *
     * 不同版本抖音把触摸拦截写在不同的类里:
     * - 24.x: 写在 MainFlippableViewPager 自身
     * - 16.4.15: 挪到父类(混淆类名, 不可硬编码), 本类只剩 dispatchTouchEvent
     *
     * 所以这里从 MainFlippableViewPager 出发, 运行时向上找第一个声明了触摸方法的类再去挂。
     *
     * 注意: 只拦 onInterceptTouchEvent / onTouchEvent, 不能拦 dispatchTouchEvent。
     * 前者返回 false 表示"我不拦截", 手势照常交给子 View(视频仍然能上下滑、能点赞);
     * 后者如果直接返回 false 而不调用原方法, 子 View 收不到任何事件, 整个首页会变成不可点。
     */
    private fun hookFlippableViewPager(lpparam: XC_LoadPackage.LoadPackageParam, config: Config) {
        var clazz: Class<*>? = try {
            XposedHelpers.findClass(MainFlippableViewPager::class.java.name, lpparam.classLoader)
        } catch (e: Throwable) {
            return
        }

        var depth = 0
        while (clazz != null && clazz != Any::class.java && depth < 8) {
            val touchMethods = clazz.declaredMethods.filter { it.name in TOUCH_METHOD_NAMES }
            if (touchMethods.isNotEmpty()) {
                touchMethods.forEach { method ->
                    method.isAccessible = true
                    method.hook {
                        onBefore {
                            if (config.isHideTab) result = false
                        }
                    }
                }
                return
            }
            clazz = clazz.superclass
            depth++
        }
    }

    //隐藏tab
    private fun hideTabItem(viewGroup: ViewGroup, config: Config) {
        val tabStrip = findTabStrip(viewGroup) ?: return

        val keywordRegex = config.hideTabKeyword
            .replace("\\s".toRegex(), "")
            .replace(",|，".toRegex(), "|")
            .toRegex()

        //按 contentDescription 匹配
        KViewUtils.findViewsByDesc(tabStrip, View::class.java, keywordRegex).forEach { v ->
            v.isVisible = false
        }

        //兜底: 部分版本的 Tab 没有设置 contentDescription, 改为按显示文本匹配
        KViewUtils.findViews(tabStrip, TextView::class.java).forEach { v ->
            val text = v.text?.toString() ?: return@forEach
            if (text.contains(keywordRegex)) v.isVisible = false
        }
    }

    /**
     * 找顶部 Tab 容器
     *
     * - 24.x: MainTabStripScrollView (HorizontalScrollView)
     * - 16.4.15: MainTabStrip (RelativeLayout)
     */
    private fun findTabStrip(viewGroup: ViewGroup): ViewGroup? {
        val scrollViews = KViewUtils.findViews(viewGroup, MainTabStripScrollView::class.java)
        if (scrollViews.isNotEmpty()) return scrollViews.first()

        val tabStrips = KViewUtils.findViews(viewGroup, MainTabStrip::class.java)
        if (tabStrips.isNotEmpty()) return tabStrips.first()

        return null
    }

    companion object {
        private val TOUCH_METHOD_NAMES = setOf("onInterceptTouchEvent", "onTouchEvent", "dispatchHoverEvent")
    }
}
