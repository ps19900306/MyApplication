
package com.nwq.base

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding

/**
 * 一个通用的 DialogFragment 基类，支持 ViewBinding 和自定义对话框样式。
 *
 * 功能：
 * 1. 支持 ViewBinding，简化视图绑定操作。
 * 2. 支持自定义对话框的宽度、高度、位置和动画。
 * 3. 提供默认的对话框显示和关闭方法。
 * 4. 支持点击外部关闭对话框的配置。
 *
 * @param VB 泛型参数，表示 ViewBinding 类型。
 */
abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {

    /**
     * ViewBinding 实例，用于绑定视图。
     * 通过 _binding 实现懒加载，避免内存泄漏。
     */
    private var _binding: VB? = null

    /**
     * 获取 ViewBinding 实例，确保非空。
     */
    protected val binding: VB get() = _binding!!

    /**
     * 创建视图时调用，初始化 ViewBinding。
     *
     * @param inflater LayoutInflater 实例，用于加载布局。
     * @param container 父容器，可能为 null。
     * @param savedInstanceState 保存的实例状态，可能为 null。
     * @return 返回绑定后的视图。
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    /**
     * 抽象方法，子类必须实现，用于创建 ViewBinding 实例。
     *
     * @param inflater LayoutInflater 实例，用于加载布局。
     * @param container 父容器，可能为 null。
     * @return 返回 ViewBinding 实例。
     */
    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * 视图创建完成后调用，初始化数据和监听器。
     *
     * @param view 创建的视图。
     * @param savedInstanceState 保存的实例状态，可能为 null。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData() // 初始化视图，设置监听器等
    }

    /**
     * 在对话框启动时调用，设置对话框的宽度和高度。
     */
    override fun onStart() {
        super.onStart()

        // 获取屏幕宽度
        val displayMetrics = requireActivity().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // 设置对话框宽度为屏幕宽度的百分比
        val width = (screenWidth * getDialogWidthPercent()).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    /**
     * 获取对话框宽度占屏幕宽度的百分比。
     * 默认竖屏为 70%，横屏为 80%。
     *
     * @return 返回宽度百分比。
     */
    open fun getDialogWidthPercent(): Float {
        // 判断是否是横屏
        return if (resources.configuration.orientation == 2) {
            0.8F
        } else {
            0.7F
        }
    }

    /**
     * 获取对话框的位置。
     * 默认居中显示。
     *
     * @return 返回对话框的位置，如 Gravity.CENTER 或 Gravity.BOTTOM。
     */
    open fun getDialogGravity(): Int {
        return Gravity.CENTER
    }

    /**
     * 初始化数据，子类可以重写此方法。
     */
    abstract fun initData()

    /**
     * 通过 ID 查找视图。
     *
     * @param id 视图的 ID。
     * @return 返回找到的视图。
     */
    fun <T : View> findViewById(id: Int): T {
        return binding.root.findViewById<T>(id)
    }

    /**
     * 显示对话框。
     *
     * @param fragmentManager FragmentManager 实例。
     * @param tag 对话框的标签，默认为类名。
     */
    fun showDialog(fragmentManager: FragmentManager, tag: String = this::class.java.simpleName) {
        if (!isAdded) {
            show(fragmentManager, tag)
        }
    }

    /**
     * 关闭对话框。
     */
    fun dismissDialog() {
        if (isAdded) {
            dismiss()
        }
    }

    /**
     * 创建对话框时调用，设置对话框的样式和动画。
     *
     * @param savedInstanceState 保存的实例状态，可能为 null。
     * @return 返回创建的 Dialog 实例。
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(isCancelableOutside())
        dialog.window?.attributes?.windowAnimations = getDialogAnimation()
        if (getDialogGravity() == Gravity.BOTTOM) {
            dialog.window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent) // 设置背景透明
                setGravity(Gravity.BOTTOM) // 设置对话框在底部显示
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                ) // 设置宽度为全屏，高度自适应
            }
        }
        return dialog
    }

    /**
     * 是否允许点击外部取消对话框。
     * 默认允许。
     *
     * @return 返回 true 表示允许，false 表示不允许。
     */
    open fun isCancelableOutside(): Boolean {
        return getDialogGravity() == Gravity.BOTTOM
    }

    /**
     * 获取对话框的动画资源。
     * 默认无动画。
     *
     * @return 返回动画资源 ID。
     */
    open fun getDialogAnimation(): Int = 0
}