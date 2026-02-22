package me.padi.nbhook.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme
import me.padi.nbhook.R
import me.padi.nbhook.hook.base.Plugin
import me.padi.nbhook.library.FloatingActionButton.FloatingActionButton
import me.padi.nbhook.library.FloatingActionButton.FloatingActionMenu
import top.sacz.xphelper.dexkit.DexFinder
import kotlin.math.roundToInt
@Deprecated(message = "弃用，暂时仍然生效，后续将剩下的Hook细分开")
object MainHook : Plugin(isEnabledDefault = true) {

    @SuppressLint("RestrictedApi", "InflateParams", "ResourceType")
    override fun onHook() {

        DexFinder.findMethod {
            declaredClass =
                "com.tencent.mobileqq.parts.QQSettingMeCoverPartV3".toClass(loader)
            parameters = arrayOf(Boolean::class.java)
            usingNumbers = longArrayOf(0, 8)
        }.firstOrNull().hook().intercept()



        "com.tencent.biz.qui.noticebar.view.VQUINoticeBarLayout".toClass(loader).resolve()
            .firstConstructor {}.hook {
                after {
                    val view = instance<View>()
                    view.post {
                        view.background = Color.TRANSPARENT.toDrawable()
                    }
                }
            }



        "com.tencent.mobileqq.widget.search.QUISearchBar".toClass(loader).resolve()
            .firstConstructor {}.hook {
                after {
                    val view = instance<View>()
                    view.post {
                        view.background = Color.TRANSPARENT.toDrawable()
                    }
                }
            }



        "com.tencent.qqnt.chats.view.ClipSkinnableRecycleView".toClass(loader).resolve()
            .firstConstructor {
                parameterCount = 2
            }.hook {
                after {
                    val recyclerView = instance<View>()
                    recyclerView.post {
                        val context = recyclerView.context
                        val layoutParams = recyclerView.layoutParams
                        if (layoutParams is ViewGroup.MarginLayoutParams) {
                            val marginTopPx = context.dp2px(250)
                            layoutParams.topMargin = marginTopPx
                            layoutParams.bottomMargin = context.dp2px(100)
                            recyclerView.layoutParams = layoutParams
                        }


                        val paddingPx = context.dp2px(25)

                        (recyclerView.parent as ViewGroup).setPadding(
                            paddingPx, paddingPx, paddingPx, paddingPx
                        )


                        val radius = context.dp2px(20)
                        val gradientDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = radius.toFloat()
                            setColor(0xE6FFFFFF.toInt())
                        }
                        recyclerView.background = gradientDrawable

                        (recyclerView.parent as ViewGroup).setBackgroundResource(R.drawable.bg)
                        recyclerView.outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(
                                view: View, outline: Outline
                            ) {
                                outline.setRoundRect(
                                    0, 0, view.width, view.height, radius.toFloat()
                                )
                            }
                        }
                        recyclerView.clipToOutline = true

                        (recyclerView.parent as ViewGroup).clipChildren = true

                        recyclerView.postDelayed({
                            recyclerView.setPadding(
                                0, context.dp2px(1), 0, context.dp2px(100)
                            )
                        }, 100)


                    }
                }
            }




        @SuppressLint("InternalInsetResource")
        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier(
                "status_bar_height", "dimen", "android"
            )
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        "com.tencent.mobileqq.activity.home.Conversation".toClass(loader).resolve()
            .firstMethod {
                name = "initUI"
            }.hook {
                after {
                    instance.asResolver().firstField {
                        name = "mTitleArea"
                    }.get<ViewGroup>()?.apply {
                        removeAllViews()
                        val statusBarHeight = getStatusBarHeight(context)

                        val rootContainer = FrameLayout(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                MATCH_PARENT, MATCH_PARENT
                            )
                            setBackgroundColor("#5A91FF".toColorInt())
                            setPadding(0, statusBarHeight, 0, 0)
                        }

                        val titleText = TextView(context).apply {
                            text = "消息"
                            gravity = Gravity.CENTER
                            textSize = 16f
                            setTextColor(Color.WHITE)
                        }
                        rootContainer.addView(
                            titleText, FrameLayout.LayoutParams(
                                WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER
                            )
                        )

                        val rightIconContainer = LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            layoutParams = FrameLayout.LayoutParams(
                                WRAP_CONTENT,
                                WRAP_CONTENT,
                                Gravity.END or Gravity.CENTER_VERTICAL
                            ).apply {
                                rightMargin = context.dp2px(16)
                            }
                        }

                        val settingsIcon = AppCompatImageView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                context.dp2px(24), context.dp2px(24)
                            ).apply {
                                leftMargin = context.dp2px(16)
                            }

                            setImageResource(R.drawable.baseline_settings_24)

                            scaleType = ImageView.ScaleType.CENTER_INSIDE

                            setOnClickListener {
                                val intent = Intent().apply {
                                    component = ComponentName(
                                        "com.tencent.mobileqq",
                                        "com.tencent.mobileqq.activity.QPublicFragmentActivity"
                                    )
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    putExtra("fling_action_key", 2)
                                    putExtra("preAct", "SplashActivity")
                                    putExtra("leftViewText", "返回")
                                    putExtra("preAct_elapsedRealtime", SystemClock.elapsedRealtime())
                                    putExtra("preAct_time", System.currentTimeMillis())
                                    putExtra(
                                        "public_fragment_class",
                                        "com.tencent.mobileqq.setting.main.MainSettingFragment"
                                    )
                                }
                                context.startActivity(intent)
                            }

                            setColorFilter(Color.WHITE)
                        }

                        val searchIcon = AppCompatImageView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                context.dp2px(24), context.dp2px(24)
                            )

                            setImageResource(R.drawable.baseline_search_24)

                            scaleType = ImageView.ScaleType.CENTER_INSIDE


                            setOnClickListener {
                                val intent = Intent().apply {
                                    setClassName(
                                        "com.tencent.mobileqq",
                                        "com.tencent.mobileqq.search.activity.UniteSearchActivity"
                                    )
                                    putExtras(Bundle().apply {
                                        putStringArrayList(
                                            "home_hint_words", ArrayList(emptyList())
                                        )

                                        putInt("fling_action_key", 2)
                                        putInt("fromType", 1)
                                        putInt("source", 1)
                                        putString("preAct", "SplashActivity")
                                        putString("leftViewText", "消息")

                                        putLong(
                                            "preAct_elapsedRealtime",
                                            SystemClock.elapsedRealtime()
                                        )
                                        putLong(
                                            "preAct_time", System.currentTimeMillis()
                                        )

                                        // 其他参数
                                        putInt("fling_code_key", 150083179)
                                        putString("keyword", null)
                                        putString("home_hot_word", null)
                                        putString("home_gif_info", null)
                                    })
                                }
                                context.startActivity(intent)
                            }

                            setColorFilter(Color.WHITE)
                        }


                        rightIconContainer.addView(searchIcon)
                        rightIconContainer.addView(settingsIcon)


                        rootContainer.addView(rightIconContainer)

                        addView(
                            rootContainer,
                            ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        )
                    }
                }
            }




        "com.tencent.mobileqq.activity.home.Conversation".toClass(loader).resolve()
            .firstMethod {
                name = "initUI"
            }.hook {
                after {
                    instance.asResolver().firstField {
                        name = "mRootView"
                    }.get<ViewGroup>()?.apply {
                        addButtonToScreen(this.context as Activity, this)
                    }
                }
            }


        "com.tencent.mobileqq.activity.home.Conversation".toClass(loader).resolve()
            .firstMethod {
                name = "initOnlineStatusContent"
            }.hook().intercept()


        "com.tencent.qui.quiblurview.QQBlurViewWrapper".toClass(loader).resolve()
            .firstMethod {
                name = "onDestroy"
            }.hook {
                after {
                    val view = instance<View>()
                    val parent = view.parent as? ViewGroup
                    parent?.removeView(view)
                }
            }


        DexFinder.findMethod {
            searchPackages = arrayOf("com.tencent.mobileqq.activity.home.chats.biz")
            usedString = arrayOf(
                "headerRoot",
                "mQQTabWidget",
                "mQQBlurView",
            )
            parameters = arrayOf(Float::class.java)
            usingNumbers = longArrayOf(12)
        }.firstOrNull().hook().intercept()


    }

    fun Context.getResourceId(resourceName: String, resourceType: String): Int {
        return try {
            resources.getIdentifier(resourceName, resourceType, packageName).takeIf { it != 0 }
                ?: throw Resources.NotFoundException("Resource $resourceType/$resourceName not found")
        } catch (e: Exception) {

            resources.getIdentifier(resourceName, resourceType, "android")
        }
    }


    private fun findActivity(context: Context?): Activity? {
        if (context == null) return null

        if (context is Activity) {
            return context
        }

        if (context is ContextWrapper) {
            return findActivity(context.baseContext)
        }

        return null
    }

    private fun addButtonToScreen(activity: Activity, rootView: ViewGroup) {
        val buttonTag = "home_fab_button"
        if (rootView.findViewWithTag<View>(buttonTag) != null) {
            return
        }

        val buttonColor = 0xFF5A91FF.toInt()
        val iconColor = 0xFFFFFFFF.toInt()

        val button = FloatingActionMenu(activity.applyModuleTheme(R.style.Theme_AppDefault)).apply {
            tag = buttonTag
            setMenuIcon(ContextCompat.getDrawable(context, R.drawable.baseline_add_24))
            setMenuButtonColorNormal(buttonColor)
            setMenuButtonColorPressed(buttonColor)
            menuIconView.setColorFilter(iconColor)

            val params = FrameLayout.LayoutParams(
                MATCH_PARENT, MATCH_PARENT, Gravity.BOTTOM or Gravity.END
            )

            params.setMargins(
                context.dp2px(16), context.dp2px(16), context.dp2px(16), context.dp2px(100)
            )

            layoutParams = params
            initMenuButton()

            val menuItems = listOf(
                FabMenu(
                    title = "QQ空间", icon = R.drawable.outline_explore_24, onClick = {
                        val url =
                            "mqqapi://qzone/activefeed?src_type=app&version=1.0&hydtgzh=11&puin="
                        context.startUri(url)
                    }), FabMenu(
                    title = "扫一扫", icon = R.drawable.outline_qr_code_scanner_24, onClick = {
                        val intent = Intent().apply {
                            setClassName(
                                "com.tencent.mobileqq",
                                "com.tencent.mobileqq.qrscan.activity.ScannerActivity"
                            )
                            putExtras(Bundle().apply {
                                putString("selfSet_leftViewText", "返回")
                                putInt("key_entrance_type", 1)
                                putLong("start_time", System.currentTimeMillis())
                                putInt("fling_action_key", 2)
                                putBoolean("from_+", true)

                                val webInvokeParams = Bundle().apply {
                                    putString("H5Source", "0")
                                    putInt("RecoglizeMask", 0)
                                    putInt("PromotionType", 0)
                                }
                                putBundle("web_invoke_params", webInvokeParams)

                                putBoolean("preload_process", true)
                                putString("preAct", "SplashActivity")
                                putLong(
                                    "preAct_elapsedRealtime", SystemClock.elapsedRealtime()
                                )
                                putInt("fling_code_key", 193393859)
                                putString("from", "Conversation")
                                putLong("preAct_time", System.currentTimeMillis())
                            })
                        }

                        context.startActivity(intent)
                    }), FabMenu(
                    title = "加好友", icon = R.drawable.outline_person_add_24, onClick = {
                        val intent = Intent().apply {
                            setClassName(
                                "com.tencent.mobileqq",
                                "com.tencent.mobileqq.activity.contact.addcontact.AddContactsActivity"
                            )

                            putExtras(Bundle().apply {
                                putString("selfSet_leftViewText", "返回")
                                putInt("fling_action_key", 2)
                                putInt("entrance_data_report", 2)
                                putInt("EntranceId", 4)
                                putString("preAct", "SplashActivity")
                                putBoolean("newEntrance", false)
                                putLong(
                                    "preAct_elapsedRealtime", SystemClock.elapsedRealtime()
                                )
                                putLong("preAct_time", System.currentTimeMillis())
                                putInt("fling_code_key", 193393859)
                            })
                        }
                        context.startActivity(intent)
                    }), FabMenu(
                    title = "收付款", icon = R.drawable.outline_savings_24, onClick = {
                        val url =
                            "mqqapi://wallet/open?src_type=web&viewtype=0&version=1&view=10&entry=2&seq=0"
                        context.startUri(url)
                    }), FabMenu(
                    title = "退出QQ", icon = R.drawable.baseline_delete_24, onClick = {
                        (context as Activity).finish()
                    })
            )



            menuItems.forEach { menuItem ->
                addMenuButton(
                    FloatingActionButton(context).apply {
                        setImageDrawable(
                            ContextCompat.getDrawable(context, menuItem.icon)
                        )
                        labelText = menuItem.title
                        colorNormal = buttonColor
                        colorPressed = buttonColor
                        setLabelColors(buttonColor, buttonColor, buttonColor)
                        setIconColor(iconColor)
                    })
            }

            setFloatButtonClickListener { _, index ->
                menuItems.getOrNull(index)?.onClick?.invoke()
                close(true)
            }
        }

        rootView.addView(button)
    }


}

fun Context.startUri(uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}

data class FabMenu(
    val title: String, val icon: Int, val onClick: () -> Unit
)


fun Context.dp2px(dp: Int): Int {
    val scale = this.resources.displayMetrics.density
    return (dp * scale).roundToInt()
}

