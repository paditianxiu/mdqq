package me.padi.nbhook.hook.drawer

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.highcapable.betterandroid.ui.extension.view.setMargins
import com.highcapable.betterandroid.ui.extension.view.textColor
import com.highcapable.hikage.core.base.Hikageable
import com.highcapable.hikage.widget.android.widget.ImageView
import com.highcapable.hikage.widget.android.widget.LinearLayout
import com.highcapable.hikage.widget.android.widget.TextView
import com.highcapable.hikage.widget.androidx.cardview.widget.CardView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme
import me.padi.nbhook.R
import me.padi.nbhook.api.QQEnvApi
import me.padi.nbhook.hook.base.Plugin

object QQDrawerFrameLayout: Plugin(isEnabledDefault = true) {
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
                                val hikage = Hikageable {
                                    LinearLayout(
                                        lparams = LayoutParams(matchParent = true), init = {
                                            orientation = LinearLayout.VERTICAL
                                            background = ContextCompat.getDrawable(
                                                context, R.drawable.bg
                                            )

                                        }) {

                                        LinearLayout(
                                            lparams = LayoutParams(
                                                widthMatchParent = true
                                            ) {
                                                topMargin = 80.dp
                                            }, init = {
                                                orientation = LinearLayout.VERTICAL
                                                gravity = Gravity.CENTER_HORIZONTAL
                                            }) {
                                            CardView(init = {
                                                radius = 999.toFloat()
                                                setOnClickListener {

                                                }
                                            }) {
                                                ImageView(
                                                    id = "headImage",
                                                    lparams = LayoutParams {
                                                        width = 120.dp
                                                        height = 120.dp
                                                    }) {}
                                            }
                                            TextView(
                                                lparams = LayoutParams {
                                                    topMargin = 10.dp
                                                }) {
                                                text = QQEnvApi.getCurrentNickName()
                                                textSize = 20.toFloat()
                                                textColor = Color.WHITE
                                                typeface =
                                                    Typeface.defaultFromStyle(Typeface.BOLD)

                                            }
                                        }

                                        LinearLayout(
                                            lparams = LayoutParams(
                                                widthMatchParent = true
                                            ) {
                                                topMargin = 10.dp
                                            }, init = {
                                                orientation = LinearLayout.HORIZONTAL
                                            }) {
                                            CardView(lparams = LayoutParams {
                                                weight = 1f
                                            }, init = {
                                                setCardBackgroundColor(0xB4FFFFFF.toInt())
                                                radius = 20.toFloat()
                                                setMargins(8.dp)

                                                setOnClickListener {

                                                }
                                            }) {
                                                LinearLayout(
                                                    lparams = LayoutParams(
                                                        matchParent = true,
                                                    ), init = {
                                                        setPadding(
                                                            16.dp, 16.dp, 16.dp, 16.dp
                                                        )
                                                        gravity = Gravity.CENTER_VERTICAL
                                                        orientation = LinearLayout.VERTICAL
                                                    }) {

                                                    ImageView {
                                                        setImageResource(R.drawable.account_balance_wallet_24)

                                                    }

                                                    TextView {
                                                        text = "钱包"
                                                        textColor = Color.BLACK
                                                        textSize = 16.toFloat()
                                                        typeface =
                                                            Typeface.defaultFromStyle(
                                                                Typeface.BOLD
                                                            )
                                                    }

                                                }

                                            }
                                            CardView(lparams = LayoutParams {
                                                weight = 1f
                                            }, init = {
                                                setCardBackgroundColor(0xB4FFFFFF.toInt())
                                                radius = 20.toFloat()
                                                setMargins(8.dp)

                                                setOnClickListener {

                                                }
                                            }) {
                                                LinearLayout(
                                                    lparams = LayoutParams(
                                                        matchParent = true,
                                                    ), init = {
                                                        setPadding(
                                                            16.dp, 16.dp, 16.dp, 16.dp
                                                        )
                                                        gravity = Gravity.CENTER_VERTICAL
                                                        orientation = LinearLayout.VERTICAL
                                                    }) {

                                                    ImageView {
                                                        setImageResource(R.drawable.book_4_24)

                                                    }

                                                    TextView {
                                                        text = "收藏"
                                                        textColor = Color.BLACK
                                                        textSize = 16.toFloat()
                                                        typeface =
                                                            Typeface.defaultFromStyle(
                                                                Typeface.BOLD
                                                            )
                                                    }

                                                }

                                            }

                                            CardView(lparams = LayoutParams {
                                                weight = 1f
                                            }, init = {
                                                setCardBackgroundColor(0xB4FFFFFF.toInt())
                                                radius = 20.toFloat()
                                                setMargins(8.dp)

                                                setOnClickListener {

                                                }
                                            }) {
                                                LinearLayout(
                                                    lparams = LayoutParams(
                                                        matchParent = true,
                                                    ), init = {
                                                        setPadding(
                                                            16.dp, 16.dp, 16.dp, 16.dp
                                                        )
                                                        gravity = Gravity.CENTER_VERTICAL
                                                        orientation = LinearLayout.VERTICAL
                                                    }) {

                                                    ImageView {
                                                        setImageResource(R.drawable.folder_24)

                                                    }

                                                    TextView {
                                                        text = "文件"
                                                        textColor = Color.BLACK
                                                        textSize = 16.toFloat()
                                                        typeface =
                                                            Typeface.defaultFromStyle(
                                                                Typeface.BOLD
                                                            )
                                                    }

                                                }

                                            }


                                        }

                                        LinearLayout(
                                            lparams = LayoutParams(
                                                widthMatchParent = true

                                            ) {}, init = {
                                                orientation = LinearLayout.HORIZONTAL
                                            }) {

                                            CardView(lparams = LayoutParams {
                                                weight = 1f
                                            }, init = {
                                                setCardBackgroundColor(0xB4FFFFFF.toInt())
                                                radius = 20.toFloat()
                                                setMargins(8.dp)

                                                setOnClickListener {

                                                }
                                            }) {
                                                LinearLayout(
                                                    lparams = LayoutParams(
                                                        matchParent = true,
                                                    ), init = {
                                                        setPadding(
                                                            16.dp, 16.dp, 16.dp, 16.dp
                                                        )
                                                        gravity = Gravity.CENTER_VERTICAL
                                                        orientation = LinearLayout.VERTICAL
                                                    }) {

                                                    ImageView {
                                                        setImageResource(R.drawable.wallpaper_24)
                                                    }
                                                    TextView {
                                                        text = "相册"
                                                        textColor = Color.BLACK
                                                        textSize = 16.toFloat()
                                                        typeface =
                                                            Typeface.defaultFromStyle(
                                                                Typeface.BOLD
                                                            )
                                                    }
                                                }
                                            }
                                            CardView(lparams = LayoutParams {
                                                weight = 1f
                                            }, init = {
                                                setCardBackgroundColor(0xB4FFFFFF.toInt())
                                                radius = 20.toFloat()
                                                setMargins(8.dp)

                                                setOnClickListener {

                                                }
                                            }) {
                                                LinearLayout(
                                                    lparams = LayoutParams(
                                                        matchParent = true,
                                                    ), init = {
                                                        setPadding(
                                                            16.dp, 16.dp, 16.dp, 16.dp
                                                        )
                                                        gravity = Gravity.CENTER_VERTICAL
                                                        orientation = LinearLayout.VERTICAL
                                                    }) {

                                                    ImageView {
                                                        setImageResource(R.drawable.apparel_24)
                                                    }
                                                    TextView {
                                                        text = "装扮"
                                                        textColor = Color.BLACK
                                                        textSize = 16.toFloat()
                                                        typeface =
                                                            Typeface.defaultFromStyle(
                                                                Typeface.BOLD
                                                            )
                                                    }
                                                }
                                            }
                                        }

                                        CardView(
                                            lparams = LayoutParams(widthMatchParent = true),
                                            init = {
                                                setCardBackgroundColor(0xB4FFFFFF.toInt())
                                                radius = 20.toFloat()
                                                setMargins(8.dp)
                                                setOnClickListener {

                                                }
                                            }) {
                                            LinearLayout(
                                                lparams = LayoutParams(
                                                    matchParent = true,
                                                ), init = {
                                                    setPadding(
                                                        16.dp, 16.dp, 16.dp, 16.dp
                                                    )
                                                    gravity = Gravity.CENTER_VERTICAL
                                                    orientation = LinearLayout.VERTICAL
                                                }) {

                                                ImageView {
                                                    setImageResource(R.drawable.settings_24)
                                                }
                                                TextView {
                                                    text = "模块设置"
                                                    textColor = Color.BLACK
                                                    textSize = 16.toFloat()
                                                    typeface =
                                                        Typeface.defaultFromStyle(Typeface.BOLD)
                                                }

                                            }

                                            setOnClickListener {

                                            }

                                        }

                                    }
                                }.create(fixContext)

                                group.addView(hikage.root())

                                Glide.with(fixContext)
                                    .load("http://q.qlogo.cn/headimg_dl?dst_uin=${QQEnvApi.getCurrentUin()}&spec=640&img_type=jpg")
                                    .into(hikage.get<ImageView>("headImage"))
                            }
                        })
                    }
                }
            }

    }
}