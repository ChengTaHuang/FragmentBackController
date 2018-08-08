package com.fragmentbackcontroller.fragment.container

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fragmentbackcontroller.MainActivity
import com.fragmentbackcontroller.R
import com.fragmentbackcontroller.fragment.child.ChildFragment
import kotlinx.android.synthetic.main.fragment_parent.*

/**
 * Created by zeno on 2018/8/1.
 */
class ContainerFragment : Fragment() {
    var myTAG : MainActivity.TAG? = null

    companion object {
        private val EXTRA_TAG = "tag"

        fun newInstance(tag : MainActivity.TAG) : ContainerFragment {
            val fragment = ContainerFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_TAG, tag)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_parent, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(arguments!!.getSerializable(EXTRA_TAG) as MainActivity.TAG)
    }

    private fun initViews(tag : MainActivity.TAG){
        textTag.text = tag.string
        myTAG = tag
        initChildFragment(ChildFragment.newInstance("A"))
    }

    fun showFragment(fragment: Fragment){
        childFragmentManager.beginTransaction()
                .replace(R.id.child_container , fragment)
                .addToBackStack(null)
                .commit()
    }

    private fun initChildFragment(fragment: Fragment){
        childFragmentManager.beginTransaction()
                .replace(R.id.child_container , fragment)
                .commit()
    }

    fun popFragment(){
        childFragmentManager.popBackStackImmediate()
    }

    fun getChildFragmentCount() : Int{
        return childFragmentManager.backStackEntryCount
    }
}