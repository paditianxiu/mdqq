package me.padi.nbhook.hook

import android.app.Application
import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.XposedBridge
import me.padi.nbhook.hook.app.AppRuntime
import me.padi.nbhook.hook.base.Plugin
import me.padi.nbhook.hook.base.PluginManager
import me.padi.nbhook.hook.conversation.SmartRefreshLayout
import me.padi.nbhook.hook.drawer.QQDrawerFrameLayout
import me.padi.nbhook.hook.navigationbar.QQTabLayout
import me.padi.nbhook.util.HybridClassLoader
import top.sacz.xphelper.XpHelper
import java.lang.reflect.Field

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    companion object {
        private val mPlugins = arrayOf(AppRuntime, SmartRefreshLayout, QQDrawerFrameLayout, MainHook, QQTabLayout)
        lateinit var mLoader: ClassLoader
    }

    override fun onInit() = configs {
        debugLog {
            tag = "NbHook"
        }
    }

    override fun onHook() = encase {
        onAppLifecycle {
            onCreate {
                injectModuleAppResources()
                XpHelper.moduleApkPath = moduleAppFilePath
            }
        }
        loadApp("com.tencent.mobileqq") {
            withProcess(mainProcessName) {
                Application::class.java.resolve().firstMethod {
                    name = "attach"
                    parameterCount = 1
                }.hook {
                    after {
                        val appContext = instance<Context>()
                        XpHelper.initContext(appContext)

                        mLoader = appContext.classLoader
                        HybridClassLoader.hostClassLoader = mLoader
                        injectClassLoader()

                        // 注册插件
                        PluginManager.sPackageParam = this@withProcess
                        PluginManager.registerPlugins(mPlugins)
                    }
                }
            }
        }
    }

    /**
     * 将合并后的类加载器设为parent
     * 来自QAuxiliary的黑魔法，通过此操作结合stub可在编译阶段 引入/使用 一些QQ的重要类
     */
        private fun injectClassLoader() {
            val fParent: Field = ClassLoader::class.java.getDeclaredField("parent")
            fParent.isAccessible = true
            val mine = MainHook::class.java.classLoader
            var curr: ClassLoader? = fParent.get(mine) as ClassLoader
            if (curr == null) {
                curr = XposedBridge::class.java.classLoader
            }
            if (curr!!.javaClass.name != HybridClassLoader::class.java.getName()) {
                HybridClassLoader.setLoaderParentClassLoader(curr)
                fParent.set(mine, HybridClassLoader.INSTANCE)
            }
        }
}