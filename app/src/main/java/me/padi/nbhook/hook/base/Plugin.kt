package me.padi.nbhook.hook.base

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import me.padi.nbhook.hook.HookEntry

/**
 * YukiBaseHooker的功能过于简单，所以进行拓展
 * 实现此类以创建插件
 */
abstract class Plugin(val isEnabledDefault: Boolean = false): YukiBaseHooker() {
    val mTAG = this::class.java.simpleName
    val loader: ClassLoader by lazy { HookEntry.mLoader }
    val mErrors = ArrayList<Throwable>()
    open fun onUnHook() {} // 卸载插件时调用，卸载插件暂未实现
}

interface DexAnalysis {
    fun dexFind()
}