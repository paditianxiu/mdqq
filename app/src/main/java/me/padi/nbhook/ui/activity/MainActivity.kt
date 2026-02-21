@file:Suppress("SetTextI18n")

package me.padi.nbhook.ui.activity

import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.text.util.Linkify
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import me.padi.nbhook.BuildConfig
import me.padi.nbhook.R
import com.highcapable.betterandroid.system.extension.component.disableComponent
import com.highcapable.betterandroid.system.extension.component.enableComponent
import com.highcapable.betterandroid.system.extension.component.isComponentEnabled
import com.highcapable.betterandroid.ui.component.activity.AppViewsActivity
import com.highcapable.betterandroid.ui.extension.component.base.getThemeAttrsDrawable
import com.highcapable.betterandroid.ui.extension.view.textColor
import com.highcapable.betterandroid.ui.extension.view.updateMargins
import com.highcapable.betterandroid.ui.extension.view.updatePadding
import com.highcapable.betterandroid.ui.extension.view.updateTypeface
import com.highcapable.hikage.core.base.Hikageable
import com.highcapable.hikage.extension.setContentView
import com.highcapable.hikage.widget.android.widget.ImageView
import com.highcapable.hikage.widget.android.widget.LinearLayout
import com.highcapable.hikage.widget.android.widget.Space
import com.highcapable.hikage.widget.android.widget.TextView
import com.highcapable.hikage.widget.androidx.core.widget.NestedScrollView
import com.highcapable.hikage.widget.me.padi.nbhook.ui.view.MaterialSwitch
import com.highcapable.yukihookapi.YukiHookAPI
import android.R as Android_R

class MainActivity : AppViewsActivity() {

    private val homeComponent by lazy { ComponentName(packageName, "${BuildConfig.APPLICATION_ID}.Home") } 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Base activity background
        findViewById<View>(Android_R.id.content).setBackgroundResource(R.color.colorThemeBackground)

        // UI view based on Hikage DSL
        // See: https://github.com/BetterAndroid/Hikage
        // Don't like it or want to switch back to XML writing? Can refer to res/layout/activity_main.xml
        // 不喜欢或者想切换回 XML 写法？可以参考 res/layout/activity_main.xml
        setContentView {
            LinearLayout(
                lparams = LayoutParams(matchParent = true),
                init = {
                    orientation = LinearLayout.VERTICAL
                }
            ) {
                LinearLayout(
                    lparams = LayoutParams(widthMatchParent = true),
                    init = {
                        gravity = Gravity.CENTER or Gravity.START
                        updatePadding(horizontal = 15.dp)
                        updatePadding(top = 13.dp, bottom = 5.dp)
                    }
                ) {
                    TextView(
                        lparams = LayoutParams {
                            weight = 1f
                        }
                    ) {
                        isSingleLine = true
                        text = getString(R.string.app_name)
                        textColor = colorResource(R.color.colorTextGray)
                        textSize = 25f
                        updateTypeface(Typeface.BOLD)
                    }
                    ImageView(
                        lparams = LayoutParams(27.dp, 27.dp) {
                            marginEnd = 5.dp
                        }
                    ) {
                        background = getThemeAttrsDrawable(Android_R.attr.selectableItemBackgroundBorderless)
                        alpha = 0.85f
                        setImageResource(R.mipmap.ic_github)
                        imageTintList = stateColorResource(R.color.colorTextGray)
                        setOnClickListener {
                            // Do something, e.g., open GitHub page
                            // 做点什么，比如打开 GitHub 页面
                        }
                    }
                }
                LinearLayout(
                    lparams = LayoutParams(widthMatchParent = true) {
                        updateMargins(horizontal = 15.dp)
                        updateMargins(top = 10.dp, bottom = 5.dp)
                    },
                    init = {
                        gravity = Gravity.CENTER or Gravity.START
                        setBackgroundResource(when {
                            YukiHookAPI.Status.isXposedModuleActive -> R.drawable.bg_green_round
                            else -> R.drawable.bg_dark_round
                        })
                    }
                ) {
                    ImageView(
                        lparams = LayoutParams(25.dp, 25.dp) {
                            marginStart = 25.dp
                            marginEnd = 5.dp
                        }
                    ) {
                        setImageResource(when {
                            YukiHookAPI.Status.isXposedModuleActive -> R.mipmap.ic_success
                            else -> R.mipmap.ic_warn
                        })
                        imageTintList = stateColorResource(R.color.white)
                    }
                    LinearLayout(
                        lparams = LayoutParams(widthMatchParent = true),
                        init = {
                            orientation = LinearLayout.VERTICAL
                            updatePadding(horizontal = 20.dp, vertical = 10.dp)
                        }
                    ) {
                        TextView(
                            lparams = LayoutParams { 
                                bottomMargin = 5.dp
                            }
                        ) { 
                            isSingleLine = true
                            ellipsize = TextUtils.TruncateAt.END
                            textColor = colorResource(R.color.white)
                            textSize = 18f
                            text = stringResource(when {
                                YukiHookAPI.Status.isXposedModuleActive -> R.string.module_is_activated
                                else -> R.string.module_not_activated
                            })
                        }
                        LinearLayout(
                            lparams = LayoutParams {
                                bottomMargin = 5.dp
                            },
                            init = {
                                gravity = Gravity.CENTER or Gravity.START
                            }
                        ) { 
                            TextView {
                                alpha = 0.8f
                                isSingleLine = true
                                ellipsize = TextUtils.TruncateAt.END
                                textColor = colorResource(R.color.white)
                                textSize = 13f
                                text = stringResource(R.string.module_version, BuildConfig.VERSION_NAME)
                            }
                            TextView(
                                lparams = LayoutParams { 
                                    leftMargin = 5.dp
                                }
                            ) {
                                setBackgroundResource(R.drawable.bg_orange_round)
                                updatePadding(horizontal = 5.dp, vertical = 2.dp)
                                isSingleLine = true
                                ellipsize = TextUtils.TruncateAt.END
                                textColor = colorResource(R.color.white)
                                textSize = 11f
                                isVisible = false
                            }
                        }
                        TextView {
                            alpha = 0.8f
                            isSingleLine = true
                            ellipsize = TextUtils.TruncateAt.END
                            textColor = colorResource(R.color.white)
                            textSize = 13f
                            text = "- Your custom text here -"
                        }
                        TextView(
                            lparams = LayoutParams { 
                                topMargin = 5.dp
                            }
                        ) {
                            alpha = 0.6f
                            isSingleLine = true
                            ellipsize = TextUtils.TruncateAt.END
                            textColor = colorResource(R.color.white)
                            textSize = 11f
                            text = if (YukiHookAPI.Status.Executor.apiLevel > 0)
                                "Activated by ${YukiHookAPI.Status.Executor.name} API ${YukiHookAPI.Status.Executor.apiLevel}"
                            else "Activated by ${YukiHookAPI.Status.Executor.name}"
                            isVisible = YukiHookAPI.Status.isXposedModuleActive
                        }
                    }
                }
                NestedScrollView(
                    lparams = LayoutParams(matchParent = true) {
                        updateMargins(vertical = 10.dp)
                    },
                    init = {
                        isFillViewport = true
                        isVerticalFadingEdgeEnabled = true
                    }
                ) {
                    LinearLayout(
                        lparams = LayoutParams(widthMatchParent = true),
                        init = {
                            orientation = LinearLayout.VERTICAL
                        }
                    ) {
                        LinearLayout(
                            lparams = LayoutParams(widthMatchParent = true) {
                                updateMargins(horizontal = 15.dp)
                            },
                            init = {
                                orientation = LinearLayout.VERTICAL
                                gravity = Gravity.CENTER or Gravity.START
                                setBackgroundResource(R.drawable.bg_permotion_round)
                                updatePadding(left = 15.dp, top = 15.dp, right = 15.dp)
                            }
                        ) {
                            LinearLayout(
                                lparams = LayoutParams(widthMatchParent = true),
                                init = {
                                    gravity = Gravity.CENTER or Gravity.START
                                }
                            ) {
                                ImageView(
                                    lparams = LayoutParams(15.dp, 15.dp) {
                                        marginEnd = 10.dp
                                    }
                                ) {
                                    setImageResource(R.mipmap.ic_home)
                                }
                                TextView(
                                    lparams = LayoutParams(widthMatchParent = true)
                                ) {
                                    alpha = 0.85f
                                    isSingleLine = true
                                    text = stringResource(R.string.display_settings)
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 12f
                                }
                            }
                            MaterialSwitch(
                                lparams = LayoutParams(widthMatchParent = true)
                            ) {
                                text = stringResource(R.string.hide_app_icon_on_launcher)
                                isAllCaps = false
                                textColor = colorResource(R.color.colorTextGray)
                                textSize = 15f
                                isChecked = !isLauncherIconShowing
                                setOnCheckedChangeListener { button, isChecked ->
                                    if (button.isPressed) hideOrShowLauncherIcon(!isChecked)
                                }
                            }
                            TextView(
                                lparams = LayoutParams(widthMatchParent = true) {
                                    bottomMargin = 10.dp
                                }
                            ) {
                                alpha = 0.6f
                                setLineSpacing(6f, 1f)
                                text = stringResource(R.string.hide_app_icon_on_launcher_tip)
                                textColor = colorResource(R.color.colorTextDark)
                                textSize = 12f
                            }
                            TextView(
                                lparams = LayoutParams(widthMatchParent = true) {
                                    bottomMargin = 10.dp
                                }
                            ) {
                                alpha = 0.6f
                                setLineSpacing(6f, 1f)
                                text = stringResource(R.string.hide_app_icon_on_launcher_notice)
                                textColor = 0xFFFF5722.toInt()
                                textSize = 12f
                            }
                        }
                        Space(lparams = LayoutParams(height = 10.dp))
                        Layout(createPromotionItem(R.string.about_module, R.mipmap.ic_yukihookapi))
                        Space(lparams = LayoutParams(height = 10.dp))
                        Layout(createPromotionItem(R.string.about_module_extension, R.mipmap.ic_kavaref))
                    }
                }
            }
        }
    }

    private fun createPromotionItem(
        @StringRes stringResource: Int,
        @DrawableRes imageResource: Int
    ) = Hikageable<MarginLayoutParams> {
        LinearLayout(
            lparams = LayoutParams(widthMatchParent = true) {
                updateMargins(left = 15.dp, right = 15.dp)
            },
            init = {
                gravity = Gravity.CENTER or Gravity.START
                setBackgroundResource(R.drawable.bg_permotion_round)
                setPadding(10.dp)
            }
        ) {
            ImageView(
                lparams = LayoutParams(35.dp, 35.dp) {
                    marginEnd = 10.dp
                }
            ) {
                setImageResource(imageResource)
            }
            TextView(
                lparams = LayoutParams(widthMatchParent = true)
            ) {
                autoLinkMask = Linkify.WEB_URLS
                ellipsize = TextUtils.TruncateAt.END
                maxLines = 2
                setLineSpacing(6f, 1f)
                text = stringResource(stringResource)
                textColor = colorResource(R.color.colorTextGray)
                textSize = 11f
            }
        }
    }

    /**
     * Hide or show launcher icons
     *
     * - You may need the latest version of LSPosed to enable the function of hiding launcher
     *   icons in higher version systems
     *
     * 隐藏或显示启动器图标
     *
     * - 你可能需要 LSPosed 的最新版本以开启高版本系统中隐藏 APP 桌面图标功能
     * @param isShow whether to display / 是否显示
     */
    private fun hideOrShowLauncherIcon(isShow: Boolean) {
        if (isShow)
            packageManager?.enableComponent(homeComponent, PackageManager.DONT_KILL_APP)
        else packageManager?.disableComponent(homeComponent, PackageManager.DONT_KILL_APP)
    }

    /**
     * Get launcher icon state
     *
     * 获取启动器图标状态
     * @return [Boolean] whether to display / 是否显示
     */
    private val isLauncherIconShowing
        get() = packageManager?.isComponentEnabled(homeComponent) == true
}