package me.padi.nbhook.hook.drawer

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme
import me.padi.nbhook.R
import me.padi.nbhook.api.QQEnvApi
import me.padi.nbhook.hook.base.Plugin
import me.padi.nbhook.util.getStatusBarHeight

object QQDrawerFrameLayout : Plugin(isEnabledDefault = true) {
    override fun onHook() {
        "com.tencent.mobileqq.activity.recent.DrawerFrame".toClass(loader).resolve()
            .firstConstructor {
                parameterCount = 6
            }.hook {
                after {
                    val leftDrawer = args(2).cast<ViewGroup>()
                    val fixContext =
                        leftDrawer?.context?.applyModuleTheme(R.style.Theme_AppDefault)
                            ?: return@after
                    leftDrawer.apply {
                        addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                            override fun onLayoutChange(
                                v: View?,
                                left: Int,
                                top: Int,
                                right: Int,
                                bottom: Int,
                                oldLeft: Int,
                                oldTop: Int,
                                oldRight: Int,
                                oldBottom: Int
                            ) {
                                v?.removeOnLayoutChangeListener(this)
                                val group =
                                    (v as? ViewGroup)?.getChildAt(0) as? ViewGroup ?: return
                                group.removeAllViews()

                                val newView = LayoutInflater.from(fixContext)
                                    .inflate(R.layout.drawer_layout, null)
                                newView.layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                group.addView(
                                    newView
                                )

                                val activity = context as Activity
                                val headImage =
                                    activity.findViewById<ImageView>(R.id.head_image)

                                val mainDrawer =
                                    activity.findViewById<LinearLayout>(R.id.main_drawer)
                                val nickName =
                                    activity.findViewById<TextView>(R.id.nick_name)


                                mainDrawer.background =
                                    ContextCompat.getDrawable(fixContext, R.drawable.bg)
                                nickName.text = QQEnvApi.getCurrentNickName()
                                mainDrawer.setPadding(
                                    0, getStatusBarHeight(context), 0, 0
                                )

                                Glide.with(context)
                                    .load("http://q.qlogo.cn/headimg_dl?dst_uin=${QQEnvApi.getCurrentUin()}&spec=640&img_type=jpg")
                                    .into(headImage)
                            }
                        })
                    }
                }
            }

    }
}