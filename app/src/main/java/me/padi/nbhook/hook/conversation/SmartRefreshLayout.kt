package me.padi.nbhook.hook.conversation

import me.padi.nbhook.hook.base.DexAnalysis
import me.padi.nbhook.hook.base.Plugin
import top.sacz.xphelper.dexkit.DexFinder
import java.lang.reflect.Method

object SmartRefreshLayout: Plugin(isEnabledDefault = true), DexAnalysis {
    lateinit var dismissSmartRefreshLayout: Method

    override fun onHook() {
        dismissSmartRefreshLayout.hook {
            before {
                if (args().first().cast<Boolean>() == true) {
                    args(index = 0).set(false)
                }
            }
        }
    }

    override fun dexFind() {
        DexFinder.findMethod {
            declaredClass =
                "com.qqnt.widget.smartrefreshlayout.layout.SmartRefreshLayout".toClass(
                    loader
                )
            parameters = arrayOf(
                Boolean::class.java
            )
            usedFields = arrayOf(
                DexFinder.findField {
                    readMethods = arrayOf(DexFinder.findMethod {
                        methodName = "computeScroll"
                    }, DexFinder.findMethod {
                        methodName = "dispatchTouchEvent"
                    }, DexFinder.findMethod {
                        methodName = "drawChild"
                    }, DexFinder.findMethod {
                        methodName = "isNestedScrollingEnabled"
                    })
                })
        }.find().forEach { method ->
            if (method.name.length < 5) {
                dismissSmartRefreshLayout = method
            }
        }
    }
}