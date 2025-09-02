package com.gustate.appbar.classic

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.updateLayoutParams
import com.gustate.appbar.R
import com.gustate.appbar.Utils.dpToPx
import com.gustate.appbar.databinding.LayoutHeaderBarBinding
import eightbitlab.com.blurview.BlurTarget
import kotlin.math.roundToInt

/**
 * 自定义应用顶栏
 * 普通样式 (非折叠版)
 */
class ClassicHeaderBar(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    // HeaderBar 私有变量
    // 视图绑定
    private val binding = LayoutHeaderBarBinding
        .inflate(LayoutInflater.from(context), this, true)
    // 系统栏参数
    private var statusBarHeight = 0f
    private var leftSystemBarWidth = 0f
    private var rightSystemBarWidth = 0f
    private var isYieldSystemBars = true
    // 基本参数
    private var headerBarHeight = 0f
    // 背景参数
    private var bkgResId = 0
    private var isBkgBlur = true
    private var bkgBlurRadius = 0f
    private var bkgBlurOverlay = 0
    private var bkgBlurOverlayAlpha = 0f
    private var bkgBlurTargetId = -1
    // 左侧按钮参数
    private var isBtnLeft = true
    private var btnLeftSrcId = 0
    private var btnLeftTint = 0
    private var btnLeftMarginStart = 0f
    // 标题参数
    private var title = ""
    private var titleGravity = ChbTitleGravity.CENTER
    private var titleMarginStart = 0f
    private var titleMarginEnd = 0f
    // 右侧按钮参数
    private var isBtnRight = false
    private var btnRightSrcId = 0
    private var btnRightTint = 0
    private var btnRightMarginEnd = 0f

    // 初始化
    init {
        context.withStyledAttributes(attrs, R.styleable.ClassicHeaderBar) {
            getAttrs()
        }
        initView()
    }

    /**
     * 获取 XML 内参数
     * @see TypedArray
     */
    private fun TypedArray.getAttrs() {
        getHeaderBarAttrs()
        getBasicAttrs()
        getBackgroundAttrs()
        getLeftBtnAttrs()
        getTitleAttrs()
        getRightBtnAttrs()
    }

    private fun initView() {
        initSystemBarsPadding()
        initHeaderBarHeight()
        initHeaderBarBackground()
        initBackgroundBlur()
        initLeftButton()
        initTitle()
        initRightButton()
    }

    /**
     * 获取系统栏参数
     * @see TypedArray
     */
    private fun TypedArray.getHeaderBarAttrs() {
        statusBarHeight = getDimension(
            R.styleable.ClassicHeaderBar_chb_status_bar_height, 0f)
        leftSystemBarWidth = getDimension(
            R.styleable.ClassicHeaderBar_chb_left_system_bar_width, 0f)
        rightSystemBarWidth = getDimension(
            R.styleable.ClassicHeaderBar_chb_right_system_bar_width, 0f)
        isYieldSystemBars = getBoolean(
            R.styleable.ClassicHeaderBar_chb_system_bars_yield_enable, true)
    }

    /**
     * 获取基本参数
     * @see TypedArray
     */
    private fun TypedArray.getBasicAttrs() {
        headerBarHeight = getDimension(
            R.styleable.ClassicHeaderBar_chb_header_bar_height, 60f.dpToPx(context))
    }

    /**
     * 获取背景参数
     * @see TypedArray
     */
    private fun TypedArray.getBackgroundAttrs() {
        bkgResId = getResourceId(
            R.styleable.ClassicHeaderBar_chb_bkg, 0)
        isBkgBlur = getBoolean(
            R.styleable.ClassicHeaderBar_chb_bkg_blur_enable, true)
        bkgBlurRadius = getFloat(
            R.styleable.ClassicHeaderBar_chb_bkg_blur_radius, 25f)
        bkgBlurOverlay = getColor(
            R.styleable.ClassicHeaderBar_chb_bkg_blur_overlay, 0)
        bkgBlurOverlayAlpha = getFloat(
            R.styleable.ClassicHeaderBar_chb_bkg_blur_overlay_alpha, 0f)
        bkgBlurTargetId = getResourceId(
            R.styleable.ClassicHeaderBar_chb_bkg_blur_target, -1)
    }

    /**
     * 获取左侧按钮参数
     * @see TypedArray
     */
    private fun TypedArray.getLeftBtnAttrs() {
        isBtnLeft = getBoolean(
            R.styleable.ClassicHeaderBar_chb_left_btn_enable, true)
        btnLeftSrcId = getResourceId(
            R.styleable.ClassicHeaderBar_chb_left_btn_src, 0)
        btnLeftTint = getColor(
            R.styleable.ClassicHeaderBar_chb_left_btn_tint, 0)
        btnLeftMarginStart = getDimension(
            R.styleable.ClassicHeaderBar_chb_left_btn_margin_start, 0f)
    }

    /**
     * 获取标题参数
     * @see TypedArray
     */
    private fun TypedArray.getTitleAttrs() {
        title = getString(R.styleable.ClassicHeaderBar_chb_title) ?: ""
        val gravityValue = getInt(R.styleable.ClassicHeaderBar_chb_title_gravity, 1)
        titleGravity = when (gravityValue) {
            0 -> ChbTitleGravity.START_WITH_LEFT_BTN
            1 -> ChbTitleGravity.CENTER
            2 -> ChbTitleGravity.IN_FRONT_OF_RIGHT_BTN
            else -> ChbTitleGravity.CENTER
        }
        titleMarginStart = getDimension(
            R.styleable.ClassicHeaderBar_chb_title_margin_start, 0f)
        titleMarginEnd = getDimension(
            R.styleable.ClassicHeaderBar_chb_title_margin_end, 0f)
    }

    /**
     * 获取右侧按钮参数
     * @see TypedArray
     */
    private fun TypedArray.getRightBtnAttrs() {
        isBtnRight = getBoolean(
            R.styleable.ClassicHeaderBar_chb_right_btn_enable, true)
        btnRightSrcId = getResourceId(
            R.styleable.ClassicHeaderBar_chb_right_btn_src, 0)
        btnRightTint = getColor(
            R.styleable.ClassicHeaderBar_chb_right_btn_tint, 0)
        btnRightMarginEnd = getDimension(
            R.styleable.ClassicHeaderBar_chb_right_btn_margin_end, 0f)
    }

    /**
     * 配置 XML 系统栏参数
     * 注：这段 XML 仅供 Android Studio 编辑器演示效果
     * 请在 Activity/Fragment 中使用 setSystemBarsPadding 动态获取并设置
     */
    private fun initSystemBarsPadding() {
        if (!isYieldSystemBars) {
            if (leftSystemBarWidth != 0f ||
                statusBarHeight != 0f ||
                rightSystemBarWidth != 0f) {
                Log.i(
                    context.getString(R.string.app_name),
                    context.getString(R.string.log_no_enable_yield_system_bars_xml))
            }
            return
        }
        binding.blurRoot.setPadding(
            leftSystemBarWidth.roundToInt(),
            statusBarHeight.roundToInt(),
            rightSystemBarWidth.roundToInt(), 0)
    }

    /**
     * 代码动态设置系统栏参数
     * @param nIsYieldStatusBar 是否开启避让状态栏
     * @param nStatusBarHeight 系统状态栏高度
     * @param nLeftSystemBarWidth 系统栏左侧宽度
     * @param nRightSystemBarWidth 系统栏右侧宽度
     * @see androidx.core.view.ViewCompat 去看看这里的实现
     */
    fun setSystemBarsPadding(
        nIsYieldStatusBar: Boolean,
        nStatusBarHeight: Int,
        nLeftSystemBarWidth: Int,
        nRightSystemBarWidth: Int
    ) {
        isYieldSystemBars = nIsYieldStatusBar
        if (!isYieldSystemBars) {
            Log.i(
                context.getString(R.string.app_name),
                context.getString(R.string.log_no_enable_yield_system_bars_xml_function))
            return
        }
        binding.blurRoot.setPadding(
            nLeftSystemBarWidth,
            nStatusBarHeight,
            nRightSystemBarWidth, 0)
    }

    /**
     * 配置 XML 应用栏部分高度
     * 若 isYieldStatusBar == false || chb_status_bar_yield_enable="false"
     * 则你设置的高度 = 状态栏高度 + 应用栏高度 （且不避让）
     * 反之你设置的高度 = 应用栏高度 - 状态栏高度 （且避让）
     * 默认为 60dp
     */
    private fun initHeaderBarHeight() {
        binding.layoutHeader.updateLayoutParams {
            height = headerBarHeight.roundToInt()
        }
    }

    /**
     * 代码动态设置应用栏部分高度
     * @param nHeaderBarHeight 软件栏高度
     */
    fun setHeaderBarHeight(nHeaderBarHeight: Int) {
        headerBarHeight = nHeaderBarHeight.toFloat()
        binding.layoutHeader.updateLayoutParams {
            height = nHeaderBarHeight
        }
    }

    /**
     * 可以对 Header 整体布局可见度进行修改
     * 通常是设置滚动监听时可以控制何时显示 Header
     * @param function 暴露原 alpha 并返回要修改的 alpha
     */
    fun updateHeaderAlpha(function: (Float) -> Float) {
        val headerAlpha = function(binding.layoutRoot.alpha)
        binding.layoutRoot.alpha = headerAlpha
    }

    /**
     * 可以对 HeaderBlur 可见度进行修改
     * 通常是设置滚动监听时可以控制何时显示 HeaderBlur
     * @param function 暴露原 alpha 并返回要修改的 alpha
     */
    fun updateHeaderBlurAlpha(function: (Float) -> Float) {
        val headerAlpha = function(binding.blurRoot.alpha)
        binding.blurRoot.alpha = headerAlpha
    }

    /**
     * 可以对 TitleView 进行修改
     * @param function 暴露 tvTitle
     */
    fun updateHeaderTitleView(function: (TextView) -> Unit) {
        function(binding.tvTitle)
    }

    /**
     * 配置 XML 应用栏部分背景
     * 注：仅当背景模糊被禁用时可用
     */
    private fun initHeaderBarBackground() {
        if (isBkgBlur) return
        val resourceTypeName = context.resources.getResourceTypeName(bkgResId)
        when (resourceTypeName) {
            "drawable", "mipmap" -> {
                val drawable = ContextCompat.getDrawable(context, bkgResId)
                binding.layoutRoot.background = drawable
            }
            "color" -> {
                val color = ContextCompat.getColor(context, bkgResId)
                binding.layoutRoot.setBackgroundColor(color)
            }
            else -> {
                Log.i(
                    context.getString(R.string.app_name),
                    "You have not set background in XML"
                )
            }
        }
    }

    /**
     * 代码动态设置背景
     * @param drawable 背景 Drawable
     */
    fun setHeaderBarBackground(drawable: Drawable) {
        if (isBkgBlur) return
        binding.layoutRoot.background = drawable
    }

    /**
     * 代码动态设置背景
     * @param color 背景 Color
     */
    fun setHeaderBarBackground(color: Color) {
        if (isBkgBlur) return
        binding.layoutRoot.setBackgroundColor(color.toArgb())
    }

    /**
     * 代码动态开启模糊效果
     * @param enable 是否开启
     */
    fun enableBlurEffect(enable: Boolean) {
        isBkgBlur = enable
        binding.blurRoot.setBlurEnabled(enable)
    }

    /**
     * 配置 XML 应用栏部分背景模糊
     * 注：仅当背景模糊被启用时可用
     */
    private fun initBackgroundBlur() {
        if (!isBkgBlur) return
        binding.layoutRoot.setBackgroundColor(0)
        val alphaInt = (bkgBlurOverlayAlpha * 255).roundToInt()
        val overlayColor = (alphaInt shl 24) or (bkgBlurOverlay and 0x00FFFFFF)
        if (bkgBlurTargetId != -1) {
            post {
                val blurTarget = rootView.findViewById<BlurTarget>(bkgBlurTargetId)
                binding.blurRoot
                    .setupWith(blurTarget)
                    .setBlurRadius(bkgBlurRadius)
                    .setOverlayColor(overlayColor)
            }
        }
    }

    /**
     * 代码动态设置应用栏部分背景模糊
     * 注：仅当背景模糊被启用时可用
     * @param nIsBkgBlur 是否开启模糊
     * @param nBlurTarget 需要模糊的 BlurTarget
     * @see BlurTarget
     * @param nBlurRadius 模糊度
     * @param nOverlayColor 模糊遮罩颜色
     * ？什么，您想让我在这里为您提供设置纯色的透明度？？？ 您可真懒！！
     */
    fun setBackgroundBlur(
        nIsBkgBlur: Boolean,
        nBlurTarget: BlurTarget,
        nBlurRadius: Float,
        nOverlayColor: Color
    ) {
        isBkgBlur = nIsBkgBlur
        if (!nIsBkgBlur) return
        binding.layoutRoot.setBackgroundColor(0)
        binding.blurRoot
            .setupWith(nBlurTarget)
            .setBlurRadius(nBlurRadius)
            .setOverlayColor(nOverlayColor.toArgb())
    }

    /**
     * 配置 XML 应用栏左侧按钮
     */
    private fun initLeftButton() {
        if (!isBtnLeft) return
        binding.btnLeft.setImageResource(btnLeftSrcId)
        binding.btnLeft.imageTintList = ColorStateList.valueOf(btnLeftTint)
        binding.btnLeft.updateLayoutParams<MarginLayoutParams> {
            marginStart = btnLeftMarginStart.roundToInt()
        }
    }

    /**
     * 代码动态设置左侧按钮
     * @param nIsBtnLeft 是否开启左侧按钮
     * @param nBtnLeftSrcId 左侧按钮资源
     * @param nBtnLeftTint 左侧按钮填充色
     * @param nBtnLeftMarginStart 左侧按钮左外边距
     */
    fun setLeftButton(
        nIsBtnLeft: Boolean,
        @ColorRes nBtnLeftSrcId: Int,
        @ColorInt nBtnLeftTint: Int,
        nBtnLeftMarginStart: Int
    ) {
        isBtnLeft = nIsBtnLeft
        if (!nIsBtnLeft) return
        binding.btnLeft.setImageResource(nBtnLeftSrcId)
        binding.btnLeft.imageTintList = ColorStateList.valueOf(nBtnLeftTint)
        binding.btnLeft.updateLayoutParams<MarginLayoutParams> {
            marginStart = nBtnLeftMarginStart
        }
    }

    /**
     * 配置 XML 应用栏标题
     */
    private fun initTitle() {
        binding.tvTitle.text = title
        val set = ConstraintSet()
        set.clone(binding.layoutRoot)
        when (titleGravity) {
            ChbTitleGravity.START_WITH_LEFT_BTN -> {
                if (isBtnLeft) {
                    set.connect(
                        binding.tvTitle.id, ConstraintSet.START,
                        binding.btnLeft.id, ConstraintSet.END,
                        btnLeftMarginStart.roundToInt()
                    )
                } else {
                    set.connect(
                        binding.tvTitle.id, ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START,
                        btnLeftMarginStart.roundToInt()
                    )
                }
            }
            ChbTitleGravity.CENTER -> {
                set.centerHorizontally(binding.tvTitle.id, ConstraintSet.PARENT_ID)
                set.centerVertically(binding.tvTitle.id, ConstraintSet.PARENT_ID)
            }
            ChbTitleGravity.IN_FRONT_OF_RIGHT_BTN -> {
                if (isBtnRight) {
                    set.connect(
                        binding.tvTitle.id, ConstraintSet.END,
                        binding.btnRight.id, ConstraintSet.START,
                        btnRightMarginEnd.roundToInt()
                    )
                } else {
                    set.connect(
                        binding.tvTitle.id, ConstraintSet.END,
                        ConstraintSet.PARENT_ID, ConstraintSet.END,
                        btnRightMarginEnd.roundToInt()
                    )
                }
            }
        }
        set.applyTo(binding.layoutRoot)
    }

    /**
     * 代码动态设置标题布局
     * @param nTitleGravity 标题重力
     * @param nStartOrEndMargin 边距
     * @see ChbTitleGravity
     */
    fun setTitleGravity(
        nTitleGravity: ChbTitleGravity,
        nStartOrEndMargin: Int
    ) {
        val set = ConstraintSet()
        set.clone(binding.layoutRoot)
        when (nTitleGravity) {
            ChbTitleGravity.START_WITH_LEFT_BTN -> {
                if (isBtnLeft) {
                    set.connect(
                        binding.tvTitle.id, ConstraintSet.START,
                        binding.btnLeft.id, ConstraintSet.END,
                        nStartOrEndMargin
                    )
                } else {
                    set.connect(
                        binding.tvTitle.id, ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START,
                        nStartOrEndMargin
                    )
                }
            }
            ChbTitleGravity.CENTER -> {
                set.centerHorizontally(binding.tvTitle.id, ConstraintSet.PARENT_ID)
                set.centerVertically(binding.tvTitle.id, ConstraintSet.PARENT_ID)
            }
            ChbTitleGravity.IN_FRONT_OF_RIGHT_BTN -> {
                if (isBtnRight) {
                    set.connect(
                        binding.tvTitle.id, ConstraintSet.END,
                        binding.btnRight.id, ConstraintSet.START,
                        nStartOrEndMargin
                    )
                } else {
                    set.connect(
                        binding.tvTitle.id, ConstraintSet.END,
                        ConstraintSet.PARENT_ID, ConstraintSet.END,
                        nStartOrEndMargin
                    )
                }
            }
        }
        set.applyTo(binding.layoutRoot)
    }

    /**
     * 设置标题
     * @param newTitle 所设置的标题
     */
    fun setTitle(newTitle: String) {
        title = newTitle
        binding.tvTitle.text = newTitle
    }

    /**
     * 获取标题
     */
    fun getTitle() = title

    /**
     * 配置 XML 应用栏右侧按钮
     */
    private fun initRightButton() {
        if (!isBtnRight) return
        binding.btnRight.setImageResource(btnRightSrcId)
        binding.btnRight.imageTintList = ColorStateList.valueOf(btnRightTint)
        binding.btnRight.updateLayoutParams<MarginLayoutParams> {
            marginEnd = btnRightMarginEnd.roundToInt()
        }
    }

    /**
     * 代码动态设置右侧按钮
     * @param nIsBtnRight 是否开启右侧按钮
     * @param nBtnRightSrcId 右侧按钮资源
     * @param nBtnRightTint 右侧按钮填充色
     * @param nBtnRightMarginStart 右侧按钮右外边距
     */
    fun setRightButton(
        nIsBtnRight: Boolean,
        @ColorRes nBtnRightSrcId: Int,
        @ColorInt nBtnRightTint: Int,
        nBtnRightMarginStart: Int
    ) {
        isBtnRight = nIsBtnRight
        if (!nIsBtnRight) return
        binding.btnRight.setImageResource(nBtnRightSrcId)
        binding.btnRight.imageTintList = ColorStateList.valueOf(nBtnRightTint)
        binding.btnRight.updateLayoutParams<MarginLayoutParams> {
            marginStart = nBtnRightMarginStart
        }
    }
}