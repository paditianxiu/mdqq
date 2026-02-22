package me.padi.nbhook.hook.navigationbar

import android.graphics.Color
import android.transition.ChangeBounds
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme
import me.padi.nbhook.R
import me.padi.nbhook.api.GuildApi
import me.padi.nbhook.hook.base.Plugin
import top.sacz.xphelper.dexkit.DexFinder

/**
 * QQ主页导航栏相关插件
 */
object QQTabLayout: Plugin(isEnabledDefault = true) {
    override fun onHook() {

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