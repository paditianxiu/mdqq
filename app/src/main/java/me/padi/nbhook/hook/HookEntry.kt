package me.padi.nbhook.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import top.sacz.xphelper.XpHelper

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

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
                loadHooker(MainHook)
            }
        }

        loadZygote {}

    }
}