import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment : DialogFragment() {


    private lateinit var mRootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(getLayoutId(), container, false)
        // 使用自定义的布局
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view) // 初始化视图，设置监听器等
    }

    // 返回布局ID，子类必须实现
    abstract fun getLayoutId(): Int

    // 设置视图，子类可以重写这个方法来初始化视图
    open fun setupView(view: View) {
    }

    fun <T : View> findViewById(id: Int): T {
        return mRootView.findViewById<T>(id)
    }

    // 显示对话框
    fun showDialog(fragmentManager: FragmentManager, tag: String = this::class.java.simpleName) {
        if (!isAdded) {
            show(fragmentManager, tag)
        }
    }

    // 关闭对话框
    fun dismissDialog() {
        if (isAdded) {
            dismiss()
        }
    }

    // 设置对话框样式和动画
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(isCancelableOutside())
        dialog.window?.attributes?.windowAnimations = getDialogAnimation()
        return dialog
    }

    // 是否允许点击外部取消对话框
    open fun isCancelableOutside(): Boolean = true

    // 获取对话框动画资源，默认无动画
    open fun getDialogAnimation(): Int = 0
}
