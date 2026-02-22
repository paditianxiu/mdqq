package me.padi.nbhook.hook.app

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.tencent.mobileqq.app.QQAppInterface
import me.padi.nbhook.api.QQEnvApi
import me.padi.nbhook.hook.base.Plugin

/**
 * 获取QQ内部接口
 */
object AppRuntime: Plugin(isEnabledDefault = true) {
    override fun onHook() {
        "com.tencent.mobileqq.app.QQAppInterface".toClass(loader).resolve().firstMethod {
            name = "onCreateQQMessageFacade"
        }.hook {
            after {
                QQEnvApi.sQApi = instance as QQAppInterface
            }
        }
    }

}