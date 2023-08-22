import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.wrappers.ChannelWrapper.Companion.guildId
import com.aliucord.wrappers.embeds.MessageEmbedWrapper.Companion.url
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.type
import com.discord.api.channel.Channel
import com.discord.api.message.attachment.MessageAttachment
import com.discord.api.message.attachment.MessageAttachmentType
import com.discord.api.message.embed.EmbedType
import com.discord.api.message.embed.MessageEmbed
import com.discord.api.role.GuildRole
import com.discord.models.member.GuildMember
import com.discord.models.message.Message
import com.discord.stores.StoreMessageState
import com.discord.widgets.chat.list.entries.*

@AliucordPlugin
class Mosaic : Plugin() {
    override fun start(context: Context) {
        // /**
        //  * This patch adds our custom entry type to the list of entries that the adapter can handle
        //  */
        // patcher.instead<WidgetChatListAdapter>(
        //     "onCreateViewHolder",
        //     ViewGroup::class.java,
        //     Int::class.java
        // ) { (param, _: Any, i: Int) ->
        //     if (i == MosaicEntry.TYPE) {
        //         WidgetChatListItems(this)
        //     } else {
        //         XposedBridge.invokeOriginalMethod(
        //             param.method,
        //             param.thisObject,
        //             param.args
        //         )
        //     }
        // }

        /**
         * This patch modifies the default behavior of splitting a messages attachment into separate
         * entries. Instead, we perform some checks to determine if there are any media that should
         * be put in a mosaic layout.
         */
        patcher.instead<ChatListEntry.Companion>(
            "createEmbedEntries",
            Message::class.java,
            StoreMessageState.State::class.java,
            Boolean::class.javaPrimitiveType!!,
            Boolean::class.javaPrimitiveType!!,
            Boolean::class.javaPrimitiveType!!,
            Boolean::class.javaPrimitiveType!!,
            Boolean::class.javaPrimitiveType!!,
            Channel::class.java,
            GuildMember::class.java,
            Map::class.java,
            Map::class.java
        ) { param ->
            val (
                _,
                message: Message,
                state: StoreMessageState.State,
                z2: Boolean,
                z3: Boolean,
                z4: Boolean,
                z5: Boolean,
                z6: Boolean,
                channel: Channel,
                guildMember: GuildMember,
                map: Map<Long, GuildRole>,
                map2: Map<Long, String>
            ) = param

            if (!message.hasAttachments() && !(message.hasEmbeds() && z5)) {
                return@instead emptyList<ChatListEntry>()
            }

            val hashSet = hashSetOf<String>()
            val arrayList = arrayListOf<MessageEmbed?>()

            message.embeds?.forEach { messageEmbed ->
                if (messageEmbed.url !in hashSet) {
                    hashSet += messageEmbed.url!!
                    arrayList += messageEmbed
                } else if (messageEmbed.url == null) {
                    arrayList += messageEmbed
                }
            }

            val attachments = message.attachments

            val size = attachments?.size ?: 0
            val entries = ArrayList<ChatListEntry>(arrayList.size)

            if (size != 0) {
                val (images, others) = attachments.partition { it.type == MessageAttachmentType.IMAGE }

                fun List<MessageAttachment>.populateEntries() {
                    forEachIndexed { index, messageAttachment ->
                        entries += AttachmentEntry(
                            index,
                            channel.guildId,
                            message,
                            state,
                            messageAttachment,
                            z2,
                            z3,
                            z4,
                            z6
                        )
                    }
                }

                others.populateEntries()

                // If there's only image, just display like normal
                // If there's more than one image, display as a mosaic
                if (images.size > 1) {
                    entries += MosaicEntry(images)
                } else {
                    images.populateEntries()
                }
            }

            if (message.type == 24) {
                entries += AutoModSystemMessageEmbedEntry(
                    channel,
                    message,
                    state,
                    arrayList[0],
                    guildMember,
                    z3,
                    map,
                    map2
                )
            }

            var type: Int?

            arrayList.forEachIndexed { index, messageEmbed ->
                if (
                    (messageEmbed!!.k() !== EmbedType.APPLICATION_NEWS) &&
                    ((message.type.also { type = it } == null) || type != 24)
                ) {
                    entries += EmbedEntry(
                        index + size,
                        channel.guildId,
                        message,
                        state,
                        messageEmbed,
                        z2,
                        z3,
                        z4,
                        z6
                    )
                }
            }

            entries
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}

// private class WidgetChatListItems(adapter: WidgetChatListAdapter) :
//     WidgetChatListItem(2131559042, adapter) {
//     private lateinit var recycler: RecyclerView
//     private lateinit var flow: Flow
//     private val embedResourceUtils = EmbedResourceUtils.INSTANCE
//     private val maxImageWidth = embedResourceUtils.computeMaximumImageWidthPx(adapter.context)
//     private val maxImageHeight = embedResourceUtils.maX_IMAGE_VIEW_HEIGHT_PX
//     private val resources = adapter.context.resources
//
//     override fun onConfigure(i: Int, chatListEntry: ChatListEntry?) {
//         super.onConfigure(i, chatListEntry)
//
//         val entry = chatListEntry as? MosaicEntry ?: return
//         val attachments = entry.attachments
//         val context = adapter.context
//
//         if (!::recycler.isInitialized) {
//             recycler = RecyclerView(context).apply {
//                 layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
//
//                 layoutManager = SpannedGridLayoutManager(
//                     orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
//                     spans = 3
//                 ).apply {
//                     spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
//                         attachments[position].let {
//                             SpanSize(it.width!!, it.height!!)
//                         }
//                     }
//                 }
//             }
//
//             (itemView as ViewGroup).addView(recycler)
//         }
//
//         recycler.removeAllViews()
//
//         recycler.adapter = MosaicAdapter(attachments)
//     }
// }

// class MosaicAdapter(
//     private var attachments: List<MessageAttachment>
// ) : RecyclerView.Adapter<MosaicAdapter.ViewHolder>() {
//     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//         return ViewHolder(LinearLayout(parent.context).apply {
//             layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
//         })
//     }
//
//     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//         val attachment = attachments[position]
//
//         holder.mediaView.updateUIWithAttachment(
//             /* p0 = */ attachment,
//             /* p1 = */ attachment.width,
//             /* p2 = */ attachment.height,
//             /* p3 = */ StoreStream.getUserSettings().isAutoPlayGifsEnabled
//         )
//
//         holder.mediaView.setOnClickListener(
//             `WidgetChatListAdapterItemAttachment$configureUI$6`(
//                 attachment
//             )
//         )
//     }
//
//     override fun getItemCount(): Int = attachments.size
//
//     // Initializing the Views
//     class ViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
//         var mediaView: InlineMediaView
//
//         init {
//             mediaView = InlineMediaView(layout.context).apply {
//                 layoutParams = ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
//                     radius = 8.dp.toFloat()
//                 }
//             }
//
//             layout.addView(mediaView)
//         }
//     }
// }

private class MosaicEntry(val attachments: List<MessageAttachment>) : ChatListEntry() {
    override fun getKey(): String = ""
    override fun getType(): Int = TYPE

    companion object {
        const val TYPE = 500
    }
}