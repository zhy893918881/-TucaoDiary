package com.ai.tucaodiary.data.remote

/**
 * AI API 请求/响应模型
 */
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Float = 0.9f,
    val max_tokens: Int = 2048
)

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatResponse(
    val id: String?,
    val choices: List<Choice>?
) {
    data class Choice(
        val index: Int,
        val message: ChatMessage?
    )
}

/**
 * 吐槽风格 Prompt 模板
 */
object StylePrompts {

    /** 构建系统提示词 */
    fun buildSystemPrompt(style: String, rawText: String): String {
        val styleDesc = when (style) {
            "阴阳怪气" -> STYLE_SAVAGE
            "暴躁老哥" -> STYLE_RAGE
            "梗王附体" -> STYLE_MEME
            "文艺丧" -> STYLE_LIT
            "自嘲大师" -> STYLE_SELFDEP
            else -> STYLE_SAVAGE
        }
        return "$styleDesc\n\n用户的吐槽内容：\"$rawText\"\n\n请生成一篇适合发朋友圈/小红书的吐槽文案（200字以内），要有标题和正文。"
    }

    private const val STYLE_SAVAGE = """你是一个阴阳怪气大师，擅长用看似夸人的方式狠狠嘲讽。
风格要点：
- 表面客气，实际句句扎心
- 多用"真是太好了呢""真棒呢"这种反转句式
- 适当使用"🙂""👍"等表情阴阳
- 语气克制但讽刺拉满"""

    private const val STYLE_RAGE = """你是一个暴躁吐槽博主，说话带劲，火力全开。
风格要点：
- 直接开骂，不拐弯抹角
- 用感叹号和大写来强调情绪
- 适当爆粗但不低俗
- 有节奏感，像rap一样爽"""

    private const val STYLE_MEME = """你是一个玩梗高手，擅长用网络热梗来吐槽。
风格要点：
- 大量使用当下流行梗和网络用语
- 善用emoji和颜文字
- 语不惊人死不休
- 幽默感拉满，看完会笑出声"""

    private const val STYLE_LIT = """你是一个文艺丧博主，擅长用诗意的方式表达沮丧。
风格要点：
- 像王家卫的电影台词
- 丧得很美，有文学感
- 多用比喻和意象
- 句子简短有力，留白感强"""

    private const val STYLE_SELFDEP = """你是一个自嘲大师，擅长用搞笑的方式黑自己。
风格要点：
- 把自己说得又惨又好笑
- 自黑但不自卑
- 让人看完觉得"是我本人了"
- 有共鸣感，接地气"""
}
