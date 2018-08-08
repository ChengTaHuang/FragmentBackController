package com.fragmentbackcontroller.fragment.child

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fragmentbackcontroller.R
import kotlinx.android.synthetic.main.fragment_child.*

/**
 * Created by zeno on 2018/8/1.
 */
class ChildFragment : Fragment() {

    companion object {
        val ACTION = "action"
        val PARAMS = "params"

        private val EXTRA_TAG = "tag"

        fun newInstance(tag : String) : ChildFragment {
            val fragment = ChildFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_TAG, tag)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_child, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(arguments!!.getString(EXTRA_TAG))
    }

    private fun initViews(tag : String){
        with(textTag){
            text = tag
            setOnClickListener {
                val intent = Intent()
                intent.action = ACTION
                intent.putExtra(PARAMS ,(tag.toCharArray()[0] + 1).toString())
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }
        }
    }
}