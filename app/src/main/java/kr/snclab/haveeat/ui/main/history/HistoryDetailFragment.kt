package kr.snclab.haveeat.ui.main.history

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.GlideApp
import kr.snclab.haveeat.R
import kr.snclab.haveeat.api.diets.VisionData
import kr.snclab.haveeat.api.diets.VisionResponse
import kr.snclab.haveeat.databinding.FragmentHistoryDetailBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.extension.toCacheFile
import kr.snclab.haveeat.model.Food
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.Dialog
import kr.snclab.haveeat.ui.dialog.TimeDialog
import kr.snclab.haveeat.ui.dialog.TwoButtonDialog
import kr.snclab.haveeat.ui.intake.IntakeFragment
import kr.snclab.haveeat.ui.view.FoodCellType
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class HistoryDetailFragment : BaseFragment<FragmentHistoryDetailBinding>() {

    companion object {
        const val ARG_ADD_FOOD = "addFood"
    }

    private val viewModel by viewModels<HistoryDetailViewModel>()
    private val args: HistoryDetailFragmentArgs by navArgs()
    private val addMode: Boolean by lazy {
        args.diets.id == -1
    }

    private lateinit var visonsList : Array<VisionData>

    override fun getLayoutResId() = R.layout.fragment_history_detail

    override fun initFragment() {
        setTitle()
        Timber.d("initFragment ${viewModel.diets.value} addMode:${addMode}")

        viewModel.visionResponse.observe(viewLifecycleOwner) {
            setImage(it)
        }

        viewModel.diets.observe(viewLifecycleOwner) {

            Timber.d("observe diets ${it.foods?.size}")
            bind.viewHistoryDetailTime.text = it.timeString
            getAdapter()?.apply {
                reset()
                notifyDataSetChanged()
            }
            setData()
        }

        if (addMode) {
            //식사 기록 모드
            viewModel.foodCellType.postValue(FoodCellType.HistoryDetailEdit)

            viewModel.diets.value?.let { diets ->
                bind.viewHistoryDetailTime.text = diets.timeString
                getAdapter()?.apply {
                    reset()
                    notifyDataSetChanged()
                }
                setData()
            } ?: run {
                viewModel.diets.postValue(args.diets)
            }

            if (args.diets.tempImage != null && viewModel.visionResponse.value == null) {
                lifecycleScope.launch(scopeExceptionHandler) {
                    //TODO : image upload animation
//                    Progress.show(requireContext())
                    clearAllLabeling();
                    showRecognizeProgress()
                    args.diets.tempImage?.toCacheFile(requireContext())?.let { file ->
                        val result = viewModel.vision(file)
                        result.data.forEach {
                            val food = viewModel.getFood(it.classId).toFood()
                            viewModel.diets.value?.let { diets ->
                                val foodList = diets.foods?.toMutableList() ?: mutableListOf()
                                foodList.add(food)
                                diets.foods = foodList.toTypedArray()
                            }
                        }

                        viewModel.diets.postValue(viewModel.diets.value)
                    }?: kotlin.run {
                        Dialog.error(this@HistoryDetailFragment, R.string.error_message)
                    }
//                    Progress.dismiss()
                    dismissRecognizeProgress()
                }
            } else {
                //아래쪽이 안보이니 스크롤 시키자.
                bind.scrollView.postDelayed({
                    bind.scrollView?.smoothScrollBy(0, 1000)
                }, 1500)
            }
        } else {
            viewModel.diets.value?.let { diets ->
                bind.viewHistoryDetailTime.text = diets.timeString
                getAdapter()?.apply {
                    reset()
                    notifyDataSetChanged()
                }
                setData()
            } ?: run {
                lifecycleScope.launch(scopeExceptionHandler) {
                    viewModel.getDietsDetail(args.diets.id)
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            //시간 설정 변경
            it.getLiveData<Date>(TimeDialog.ARG_TIME).observe(
                viewLifecycleOwner
            ) { result ->
                viewModel.updateTime(result)
                bind.viewHistoryDetailTime.text = Define.toTimeFormat(result)
            }
            //음식 추가
            it.getLiveData<Food>(ARG_ADD_FOOD).observe(viewLifecycleOwner) { result ->
                viewModel.diets.value?.let { diets ->
                    val foodList = diets.foods?.toMutableList() ?: mutableListOf()
                    foodList.add(result)
                    diets.foods = foodList.toTypedArray()
                    viewModel.diets.postValue(diets)
                }
                it.remove<Food>(ARG_ADD_FOOD)
            }
            it.getLiveData<Pair<Int, Float>>(IntakeFragment.ARG_INTAKE_RESULT)
                .observe(viewLifecycleOwner) { result ->
                    viewModel.diets.value?.let { diets ->
                        diets.foods!![result.first].amount = result.second
                        viewModel.diets.postValue(diets)
                    }
                    it.remove<Pair<Int, Float>>(IntakeFragment.ARG_INTAKE_RESULT)
                }
            //서비스 둘러보기 사용자 저장시에 로그인 화면으로
            it.getLiveData<Boolean>(TwoButtonDialog.ARG_POSITIVE).observe(
                viewLifecycleOwner
            ) { positive ->
                if(positive) {
                    findNavController().navigate(HistoryDetailFragmentDirections.actionSnsSignInFragment())
                }
            }
        }

        //시간 설정 클릭
        bind.viewHistoryDetailTimeLayout.setOnSafeClickListener {
            if (bind.viewHistoryDetailTimeEdit.visibility == View.VISIBLE) { //edit mode
                findNavController().navigate(
                    HistoryDetailFragmentDirections.actionTimeFragment(
                        viewModel.diets.value?.date?.time ?: Date().time
                    )
                )
            }
        }

        //음식 추가
        bind.viewHistoryDetailAdd.setOnSafeClickListener {
            findNavController().navigate(HistoryDetailFragmentDirections.actionFoodAddFragment())
        }
        val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.PopupMenu)
        val popup = PopupMenu(wrapper, bind.viewHistoryDetailTitle.viewTitleMenu)

        viewModel.foodCellType.observe(viewLifecycleOwner) {
            setEditMode(it == FoodCellType.HistoryDetailEdit)
        }
        popup.menuInflater.inflate(R.menu.menu_history_detail, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_detail_edit -> {
                    viewModel.foodCellType.postValue(FoodCellType.HistoryDetailEdit)
                }
                R.id.action_detail_delete -> {
                    delete()
                }
            }
            true
        }

        bind.viewHistoryDetailTitle.viewTitleMenu.visibility = View.VISIBLE
        bind.viewHistoryDetailTitle.viewTitleMenu.setOnClickListener {
            popup.show()
        }

        setBackButton {
            findNavController().popBackStack()
        }

        //수정 취소
        bind.viewHistoryDetailCancel.setOnSafeClickListener {
            if (addMode) {
                findNavController().navigate(HistoryDetailFragmentDirections.actionMainFragment())
            } else {
                viewModel.cancel()
                viewModel.foodCellType.postValue(FoodCellType.HistoryDetail)
                getAdapter()?.reset()
            }
        }
        //수정 저장
        bind.viewHistoryDetailSave.setOnSafeClickListener {
            if(Define.isGuest) {
                TwoButtonDialog.show(this, R.string.notification, R.string.error_login_request, R.string.signin_login)
            } else {
                lifecycleScope.launch {
                    viewModel.save()
                    if (addMode) {
                        findNavController().navigate(HistoryDetailFragmentDirections.actionMainFragment())
                    } else {
                        viewModel.foodCellType.postValue(FoodCellType.HistoryDetail)
                        getAdapter()?.reset()
                    }
                }
            }
        }
    }

    private fun getAdapter(): HistoryDetailFoodAdapter? {
        if (bind.viewHistoryDetailList.adapter == null) {
            viewModel.diets.value?.let {
                bind.viewHistoryDetailList.adapter =
                    HistoryDetailFoodAdapter(viewModel, findNavController())
            } ?: run {
                return null
            }
        }
        return bind.viewHistoryDetailList.adapter as? HistoryDetailFoodAdapter
    }

    private fun setTitle() {
        bind.viewHistoryDetailTitle.viewTitleMessage.text =
            if (Define.toFullDateFormat(args.diets.date) == Define.toFullDateFormat(Date())) {
                "오늘 " + getString(args.diets.type().resId)
            } else {
                Define.toFullDateFormat(args.diets.date) + " " + getString(args.diets.type().resId)
            }
        getString(args.diets.type().resId)
    }

    private fun setImage(visionResponse: VisionResponse) {
        args.diets.tempImage?.let { uri ->
            GlideApp.with(requireContext()).load(uri).listener(imageLoadListener).into(bind.viewHistoryDetailImage)
        } ?: kotlin.run {
            val url = visionResponse.getImageUrl()
            Timber.d("@@ url:$url")
            GlideApp.with(requireContext()).load(url).listener(imageLoadListener).into(bind.viewHistoryDetailImage)
        }
    }
    private fun setData() {
        viewModel.foodCellType.value?.let { cellType ->
            getAdapter()?.setType(cellType)
        }
        getAdapter()?.getData()?.let { foodArray ->
            bind.viewHistoryDetailTotalCal.text =
                foodArray.sumOf { it.calorie?.toInt() ?: 0 }.toString()
            bind.viewHistoryDetailTotalCount.text = foodArray.size.toString()
        }
    }

    private val imageLoadListener = object: RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {



            resource?.let { r ->
                //TODO : draw TextView
                target?.getSize { width, height -> Timber.d("drawableRect : $width, $height, ${resource.intrinsicWidth}, ${resource.intrinsicHeight}")}
//                Timber.d("drawableRect : ${drawableRect.left}, ${drawableRect.top}, ${drawableRect.right}, ${drawableRect.bottom}")
                val v = bind.viewHistoryDetailImage

                val viewRect = Rect(v.x.toInt(), v.y.toInt(), v.x.toInt()+ v.width, v.y.toInt()+v.height)
                Timber.d("result "+ r)
                Timber.d("viewRect : ${viewRect.left}, ${viewRect.top}, ${viewRect.right}, ${viewRect.bottom}")
//                        m.setRectToRect(r.bounds.toRectF(), viewRect.toRectF(), Matrix.ScaleToFit.START)
//                        resource
                bind.viewHistoryDetailImage.setImageDrawable(resource)
                viewModel.visionResponse.value?.let {
                    drawFoodRect(it, resource.intrinsicWidth, resource.intrinsicHeight)
                }
            }

            return true
        }
    }
    private fun drawFoodRect(visionResponse: VisionResponse, width: Int, height: Int) {
//        visionResponse.
        Timber.d("visionResponse , ${bind.viewHistoryDetailImage.width}, ${bind.viewHistoryDetailImage.height}"+ visionResponse)
        visonsList = visionResponse.data;

        for (item in visonsList) {

            val ratioW = width.toFloat() / item.frameWidth.toFloat()
            val ratioH = height.toFloat() / item.frameHeight.toFloat()

            Timber.d("visionResponse ratio  ${ratioW} + ${ratioH}")
            var x : Float = item.left.toFloat() * ratioW
            var y : Float = item.top.toFloat() * ratioH
            val w = item.width * ratioW
            val h = item.height * ratioH
            if (x < 0) x = 0.0f
            if (y < 0) y = 0.0f

            Timber.d("visionResponse ratio  ${x} + ${y} + ${w} + ${h}")
            addLabeling(x, y, w.toInt(), h.toInt(), item.classId, item.className)

        }
//                showRecognizeProgress()

//                clearAllLabeling();
    }


    private fun addLabeling (x: Float, y: Float, w: Int, h: Int, tag: String, name: String ) {
        val labeling = TextView(context)
        labeling.text = name
        labeling.tag = tag
        labeling.gravity= Gravity.CENTER
        labeling.ellipsize = TextUtils.TruncateAt.END
        labeling.setLines(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            labeling.setTextAppearance(R.style.LabelingText)
        } else {
            labeling.setTextAppearance(this.requireContext(),R.style.LabelingText)
        }

        labeling.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.white))
        labeling.layoutParams = FrameLayout.LayoutParams(w,h)
        labeling.x = x
        labeling.y = y
        labeling.gravity= Gravity.CENTER
        labeling.setBackgroundResource(R.drawable.bg_rect_black_outline_white)
        bind.viewHistoryDetailImageLayout.addView(labeling)
    }

    private fun clearAllLabeling() {
        var startIndex = 1
        val count =  bind.viewHistoryDetailImageLayout.childCount

        if (count == 1) return;
        for( i in 0..count - 1) {
           val v =  bind.viewHistoryDetailImageLayout.getChildAt(i)
           if ( v is TextView ) {
               startIndex = i
               break
           }
        }
        bind.viewHistoryDetailImageLayout.removeViews(startIndex, count - startIndex )
    }

    private fun showRecognizeProgress() {
        bind.viewHistoryDetailProgressLayout.visibility = View.VISIBLE
        val drawable = bind.aniProgressView.drawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (drawable is AnimatedVectorDrawable) {
                val animatedVectorDrawable = drawable;
                animatedVectorDrawable.start();
            }
        } else {
            if (drawable is AnimatedVectorDrawableCompat) {
                val animatedVectorDrawableCompat = drawable;
                animatedVectorDrawableCompat.start();
            }
        }
    }

    private fun dismissRecognizeProgress() {
        bind.viewHistoryDetailProgressLayout.visibility = View.GONE
    }


    private fun setEditMode(value: Boolean) {
        if (value) {
            bind.viewHistoryDetailTitle.viewTitleMenu.visibility = View.GONE
            bind.viewHistoryDetailBottomLayoutShadow.visibility = View.VISIBLE
            bind.viewHistoryDetailBottomLayout.visibility = View.VISIBLE
            bind.viewHistoryDetailAdd.visibility = View.VISIBLE
            bind.viewHistoryDetailTimeEdit.visibility = View.VISIBLE

            getAdapter()?.setType(FoodCellType.HistoryDetailEdit)
        } else {
            bind.viewHistoryDetailTitle.viewTitleMenu.visibility = View.VISIBLE
            bind.viewHistoryDetailBottomLayoutShadow.visibility = View.GONE
            bind.viewHistoryDetailBottomLayout.visibility = View.GONE
            bind.viewHistoryDetailAdd.visibility = View.GONE
            bind.viewHistoryDetailTimeEdit.visibility = View.GONE

            getAdapter()?.setType(FoodCellType.HistoryDetail)
        }
    }

    private fun delete() {
        lifecycleScope.launch {
            viewModel.delete()
            findNavController().popBackStack()
        }
    }
}