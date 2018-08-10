package com.rosebay.odds.ui.createOdds


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.R
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.util.SharedPreferencesClient
import easymvp.annotation.FragmentView
import easymvp.annotation.Presenter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@FragmentView(presenter = CreateOddsPresenterImpl::class)
open class CreateOddsFragment : Fragment(), CreateOddsView, DatePickerDialog.OnDateSetListener {

    @Presenter
    var createOddsPresenter: CreateOddsPresenterImpl? = null

    @Inject
    lateinit var sharedPreferencesClient: SharedPreferencesClient

    @BindView(R.id.createOddsButton)
    lateinit var mCreateOddsButton: Button
    @BindView(R.id.noCheckBox)
    lateinit var noCheckBox: CheckBox
    @BindView(R.id.yesCheckBox)
    lateinit var yesCheckBox: CheckBox
    @BindView(R.id.descriptionEditText)
    lateinit var mDescriptionEditText: EditText
    @BindView(R.id.imageViewPager)
    lateinit var mImageViewPager: ViewPager
    @BindView(R.id.forOrAgainstLinearLayout)
    lateinit var mForOrAgainstLinearLayout: LinearLayout
    @BindView(R.id.instructions_label)
    lateinit var mInstructionsLabelTextView: TextView
    @BindView(R.id.imageSearchProgressBar)
    lateinit var mImageSearchProgressBar: ProgressBar
    @BindView(R.id.imageSearchTermsEditText)
    lateinit var mImageSearchTermsEditText: EditText
    @BindView(R.id.imageSearchTermsLayout)
    lateinit var mImageSearchTermsLayout: ConstraintLayout
    @BindView(R.id.createOddsButtonLayout)
    lateinit var mCreateOddsButtonLayout: LinearLayout
    @BindView(R.id.searchButton)
    lateinit var mSearchButton: Button
    @BindView(R.id.looksGoodButton)
    lateinit var mLooksGoodButton: Button
    @BindView(R.id.makeChangesButton)
    lateinit var mMakeChangesButton: Button
    @BindView(R.id.dateLayout)
    lateinit var mDateLayout: LinearLayout
    @BindView(R.id.noDateButton)
    lateinit var mNoDateButton: Button
    @BindView(R.id.pickADateButton)
    lateinit var mPickADateButton: Button
    @BindView(R.id.dueDateTextView)
    lateinit var mDueDateTextView: TextView

    lateinit var mImagePagerAdapter: ImagePagerAdapter
    var mDueDate = ""
    lateinit var calendar: Calendar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_create_odds,
                container, false)
        ButterKnife.bind(this, root)
        showInitialLayout()
        calendar = Calendar.getInstance()
        return root
    }

    override fun onAttach(context: Context) {
        OddsApplication.appComponent.inject(this)
        super.onAttach(context)
    }

    fun showInitialLayout() {
        mInstructionsLabelTextView.visibility = View.VISIBLE
        mDescriptionEditText.visibility = View.VISIBLE
        mImageViewPager.visibility = View.GONE
        hideOnInitialState()
    }

    override fun onSearch() {
        mImageSearchProgressBar.visibility = View.VISIBLE
        mDescriptionEditText.clearFocus()
        mImageSearchTermsEditText.clearFocus()
        mImageViewPager.visibility = View.GONE
        mInstructionsLabelTextView.visibility = View.INVISIBLE
    }

    fun startAgain() {
        mDescriptionEditText.visibility = View.VISIBLE
        mImageSearchTermsLayout.visibility = View.VISIBLE
        yesCheckBox.isChecked = false
        noCheckBox.isChecked = false
        hideOnInitialState()
    }

    fun hideOnInitialState() {
        mForOrAgainstLinearLayout.visibility = View.GONE
        mLooksGoodButton.visibility = View.GONE
        mImageSearchProgressBar.visibility = View.GONE
        mCreateOddsButtonLayout.visibility = View.GONE
        mDateLayout.visibility = View.GONE
        mDueDateTextView.visibility = View.GONE
    }

    override fun closeKeyboard() {
        val inputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val binder = view!!.windowToken
        inputMethodManager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun clearTextFields() {
        mDescriptionEditText.text = null
        mImageSearchTermsEditText.text = null
        yesCheckBox.isChecked = false
        noCheckBox.isChecked = false
    }

    override fun loadImages(imageItemList: List<String>) {
        mInstructionsLabelTextView.visibility = View.GONE
        mImageViewPager.visibility = View.VISIBLE
        mImageSearchProgressBar.visibility = View.GONE
        mLooksGoodButton.visibility = View.VISIBLE
        mImagePagerAdapter = ImagePagerAdapter(this.context!!, imageItemList)
        mImageViewPager.adapter = mImagePagerAdapter
        mImagePagerAdapter.notifyDataSetChanged()
    }

    fun onCheckedCheckbox() {
        if (yesCheckBox.isChecked || noCheckBox.isChecked) {
            mDateLayout.visibility = View.VISIBLE
        } else {
            mDateLayout.visibility = View.GONE
        }
    }

    @OnClick(R.id.searchButton)
    fun searchForImages() {
        createOddsPresenter!!.getImages(mDescriptionEditText.text.toString(), mImageSearchTermsEditText.text.toString())
    }

    @OnClick(R.id.looksGoodButton)
    fun showForOrAgainstLayout() {
        mDescriptionEditText.clearFocus()
        mImageSearchTermsLayout.visibility = View.GONE
        mForOrAgainstLinearLayout.visibility = View.VISIBLE
        closeKeyboard()
    }

    @OnClick(R.id.makeChangesButton)
    fun makeChanges() {
        startAgain()
    }

    @OnClick(R.id.noCheckBox)
    fun voteNo() {
        yesCheckBox.isChecked = false
        onCheckedCheckbox()
    }

    @OnClick(R.id.yesCheckBox)
    fun voteYes() {
        noCheckBox.isChecked = false
        onCheckedCheckbox()
    }

    @OnClick(R.id.pickADateButton)
    fun pickADate() {
        showDatePicker()
    }

    @OnClick(R.id.noDateButton)
    fun noDate() {
        showSubmitLayout()
    }

    fun showSubmitLayout() {
        mForOrAgainstLinearLayout.visibility = View.GONE
        mDateLayout.visibility = View.GONE
        mCreateOddsButtonLayout.visibility = View.VISIBLE
    }

    @OnClick(R.id.createOddsButton)
    fun submitNewOdd() {
        createOddsPresenter!!.createOdds(createNewOdd())
    }

    private fun createNewOdd(): SingleOdd {
        val singleOdd = SingleOdd()
        singleOdd.description = mDescriptionEditText.text.toString()
        singleOdd.username = sharedPreferencesClient.getUsername(getString(R.string.username))
        singleOdd.oddsFor = if (yesCheckBox.isChecked) 1 else 0
        singleOdd.oddsAgainst = if (noCheckBox.isChecked) 1 else 0
        singleOdd.percentage = if (singleOdd.oddsFor == 1) 100 else 0
        singleOdd.dateSubmitted = setCurrentDate()
        singleOdd.dueDate = mDueDate
        singleOdd.imageUrl = mImagePagerAdapter.getCurrentUrl(mImageViewPager.currentItem)
        return singleOdd
    }

    fun setCurrentDate(): String {
        val today = Date()
        val dateFormat = SimpleDateFormat.getDateInstance()
        return dateFormat.format(today)
    }

    override fun onError() {
        mImageSearchProgressBar.visibility = View.GONE
        startAgain()
        Snackbar.make(mCreateOddsButton, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSave(description: String) {
        Snackbar.make(mCreateOddsButton, String.format(getString(R.string.odds_have_been_saved_msg), description), Snackbar.LENGTH_LONG).show()
        fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
    }

    private fun getDueDate(month: Int, dayOfMonth: Int, year: Int) {
        calendar.set(year, month, dayOfMonth)
        val date = calendar.time
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        mDueDateTextView.visibility = View.VISIBLE
        mDueDate = dateFormat.format(date)
        mDueDateTextView.text = String.format(getString(R.string.due_date_answer), mDueDate)
        showSubmitLayout()
    }

    fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> this.onDateSet(view, year, month, dayOfMonth) }, year, month, dayOfMonth)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        getDueDate(month, dayOfMonth, year)
    }

    override fun onPause() {
        super.onPause()
        createOddsPresenter?.onViewDetached()
    }

    override fun onResume() {
        super.onResume()
        createOddsPresenter?.onViewAttached(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = OddsApplication.getRefWatcher(this.activity!!.applicationContext.applicationContext)
        refWatcher.watch(this)
    }

    companion object {

        fun newInstance(): CreateOddsFragment {
            return CreateOddsFragment()
        }
    }

}
