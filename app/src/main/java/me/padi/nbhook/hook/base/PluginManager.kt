package me.padi.nbhook.hook.base

import android.util.Log
import com.highcapable.yukihookapi.hook.param.PackageParam
import de.robv.android.xposed.XposedBridge

/**
 * 插件管理
 * 使用前先填充 #sPackageParam 字段
 */
object PluginManager {
    lateinit var sPackageParam: PackageParam
    val runningPlugins = ArrayList<Plugin>()

    /**
     * 注册插件
     * 需要注意的是，并没有实现开关功能，目前只要注册便会加载onHook
     */
    fun registerPlugin(plugin: Plugin) {
        try {
            if (plugin is DexAnalysis) plugin.dexFind()
            sPackageParam.loadHooker(plugin)
        } catch (e: Throwable) {
            plugin.mErrors.add(e)
            XposedBridge.log("[${plugin.mTAG}]${Log.getStackTraceString(e)}")
        } finally {
            runningPlugins.add(plugin)
        }
    }

    fun registerPlugins(plugins: Array<Plugin>) {
        plugins.forEach { registerPlugin(it) }
    }
}