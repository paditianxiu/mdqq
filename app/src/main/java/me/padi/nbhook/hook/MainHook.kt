package me.padi.nbhook.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.SystemClock
import android.transition.*
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme
import de.robv.android.xposed.XposedBridge
import me.padi.nbhook.R
import me.padi.nbhook.api.GuildApi
import me.padi.nbhook.library.FloatingActionButton.FloatingActionButton
import me.padi.nbhook.library.FloatingActionButton.FloatingActionMenu
import me.padi.nbhook.util.HybridClassLoader
import top.sacz.xphelper.XpHelper
import top.sacz.xphelper.dexkit.DexFinder
import java.lang.reflect.Field
import kotlin.math.roundToInt

object MainHook : YukiBaseHooker() {
    private var sQQAppInterface: Any? = null

    @SuppressLint("RestrictedApi", "InflateParams", "ResourceType")
    override fun onHook() {
        Application::class.java.resolve().firstMethod {
            name = "attach"
            parameterCount = 1
        }.hook {
            after {
                val appContext = instance<Context>()
                XpHelper.initContext(appContext)
                val loader = appContext.classLoader
                HybridClassLoader.hostClassLoader = loader
                injectClassLoader()
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
                        method.hook {
                            before {
                                if (args().first().cast<Boolean>() == true) {
                                    args(index = 0).set(false)
                                }
                            }
                        }
                    }
                }


                "com.tencent.mobileqq.app.QQAppInterface".toClass(loader).resolve().firstMethod {
                    name = "onCreateQQMessageFacade"
                }.hook {
                    after {
                        sQQAppInterface = instance

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
                                        Toast.makeText(context, "设置", Toast.LENGTH_SHORT).show()
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



                "com.tencent.mobileqq.widget.QQTabLayout".toClass(loader).resolve()
                    .firstConstructor {
                        parameterCount = 2
                    }.hook {
                        after {
                            val tabLayout = instance<ViewGroup>()
                            tabLayout.removeAllViews()
                            tabLayout.setBackgroundColor(Color.TRANSPARENT)
                            val parent =
                                LayoutInflater.from(tabLayout.context.applyModuleTheme(R.style.Theme_AppDefault))
                                    .inflate(R.layout.material_tab, null)

                            tabLayout.post {
                                val rootView = (tabLayout.parent as ViewGroup)
                                val homeTabViewTag = "home_tab_view"
                                if (rootView.findViewWithTag<View>(homeTabViewTag) != null) {
                                    return@post
                                }
                                rootView.addView(parent)
                                val roundLayout =
                                    parent.findViewById<LinearLayout>(R.id.roundlayout)
                                roundLayout.setTag(homeTabViewTag)
                                val tab1 = parent.findViewById<LinearLayout>(R.id.tab1)
                                val tab2 = parent.findViewById<LinearLayout>(R.id.tab2)
                                val tab3 = parent.findViewById<LinearLayout>(R.id.tab3)
                                val tab4 = parent.findViewById<LinearLayout>(R.id.tab4)
                                // 若未启用频道选项 则不显示此Tab
                                if (!GuildApi.isShowGuildTab()) tab3.visibility = View.GONE

                                val tabs = listOf(tab1, tab2, tab3, tab4)
                                val text1 = tab1.findViewById<TextView>(R.id.tab1_text)
                                val text2 = tab2.findViewById<TextView>(R.id.tab2_text)
                                val text3 = tab3.findViewById<TextView>(R.id.tab3_text)
                                val text4 = tab4.findViewById<TextView>(R.id.tab4_text)
                                val customTransition: Transition = TransitionSet().apply {
                                    ordering = TransitionSet.ORDERING_TOGETHER
                                    addTransition(ChangeBounds().apply {
                                        interpolator = FastOutSlowInInterpolator()
                                        duration = 300
                                    })
                                    addTransition(Fade().apply {
                                        duration = 200
                                    })
                                }.addListener(object : Transition.TransitionListener {
                                    override fun onTransitionStart(transition: Transition) {
                                        tabs.forEach { it.isClickable = false }
                                    }

                                    override fun onTransitionEnd(transition: Transition) {
                                        tabs.forEach { it.isClickable = true }
                                    }

                                    override fun onTransitionCancel(transition: Transition) {
                                        tabs.forEach { it.isClickable = true }
                                    }

                                    override fun onTransitionPause(transition: Transition) {}
                                    override fun onTransitionResume(transition: Transition) {}
                                })

                                var isInitialSetupComplete = false

                                tab1.isSelected = true
                                text1.visibility = View.VISIBLE

                                tab1.setOnClickListener {
                                    if (isInitialSetupComplete && !tab1.isSelected) {
                                        TransitionManager.beginDelayedTransition(
                                            roundLayout, customTransition
                                        )
                                    }

                                    tab1.isSelected = true
                                    tab2.isSelected = false
                                    tab3.isSelected = false
                                    tab4.isSelected = false
                                    text1.visibility = View.VISIBLE
                                    text2.visibility = View.GONE
                                    text3.visibility = View.GONE
                                    text4.visibility = View.GONE
                                    tabLayout.asResolver().firstMethod {
                                        name = "setCurrentTab"
                                    }.invoke(
                                        0
                                    )

                                }
                                tab2.setOnClickListener {
                                    if (isInitialSetupComplete && !tab2.isSelected) {
                                        TransitionManager.beginDelayedTransition(
                                            roundLayout, customTransition
                                        )
                                    }

                                    tab1.isSelected = false
                                    tab2.isSelected = true
                                    tab3.isSelected = false
                                    tab4.isSelected = false
                                    text1.visibility = View.GONE
                                    text2.visibility = View.VISIBLE
                                    text3.visibility = View.GONE
                                    text4.visibility = View.GONE

                                    tabLayout.asResolver().firstMethod {
                                        name = "setCurrentTab"
                                    }.invoke(
                                        if (GuildApi.isShowGuildTab()) 2 else 1
                                    )

                                }
                                tab3.setOnClickListener {
                                    if (isInitialSetupComplete && !tab3.isSelected) {
                                        TransitionManager.beginDelayedTransition(
                                            roundLayout, customTransition
                                        )
                                    }

                                    tab1.isSelected = false
                                    tab2.isSelected = false
                                    tab3.isSelected = true
                                    tab4.isSelected = false
                                    text1.visibility = View.GONE
                                    text2.visibility = View.GONE
                                    text3.visibility = View.VISIBLE
                                    text4.visibility = View.GONE

                                    tabLayout.asResolver().firstMethod {
                                        name = "setCurrentTab"
                                    }.invoke(
                                        1
                                    )

                                }
                                tab4.setOnClickListener {
                                    if (isInitialSetupComplete && !tab4.isSelected) {
                                        TransitionManager.beginDelayedTransition(
                                            roundLayout, customTransition
                                        )
                                    }
                                    tab1.isSelected = false
                                    tab2.isSelected = false
                                    tab3.isSelected = false
                                    tab4.isSelected = true
                                    text1.visibility = View.GONE
                                    text2.visibility = View.GONE
                                    text3.visibility = View.GONE
                                    text4.visibility = View.VISIBLE

                                    tabLayout.asResolver().firstMethod {
                                        name = "setCurrentTab"
                                    }.invoke(
                                        if (GuildApi.isShowGuildTab()) 3 else 2
                                    )
                                }

                                tabLayout.post { isInitialSetupComplete = true }
                            }
                        }
                    }


            }
        }
    }

    fun getCurrentAccountUin(): String {
        sQQAppInterface?.asResolver()?.firstMethod {
            name = "getCurrentAccountUin"
        }?.invoke<String>()?.let {
            return it
        }
        return ""
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

    @SuppressLint("DiscouragedPrivateApi")
    @Throws(Exception::class)
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

fun Context.startUri(uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}

data class FabMenu(
    val title: String, val icon: Int, val onClick: () -> Unit
)


fun Context.dp2px(dp: Int): Int {
    val scale = this.getResources().getDisplayMetrics().density
    return (dp * scale).roundToInt()
}

