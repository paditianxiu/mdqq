package me.padi.nbhook.api

import com.highcapable.kavaref.KavaRef.Companion.asResolver

/**
 * 频道相关
 */
object GuildApi {
    private val guildRouterApi = Class.forName("com.tencent.mobileqq.guild.api.IQQGuildRouterApi")

    /**
     * 导航栏是否正在展示频道Tab
     * @return Boolean
     */
    fun isShowGuildTab(): Boolean = QRoute.api(guildRouterApi).asResolver().firstMethod {
        name = "isShowGuildTab"
    }.invoke() as Boolean
}