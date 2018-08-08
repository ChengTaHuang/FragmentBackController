package com.fragmentbackcontroller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import com.fragmentbackcontroller.fragment.child.ChildFragment
import com.fragmentbackcontroller.fragment.container.ContainerFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    enum class TAG constructor(var string: String){
        FIRST("FIRST") , SECOND("SECOND") , THIRD("THIRD") , FOURTH("FOURTH") , FIFTH("FIFTH")
    }

    private var firstFragment = ContainerFragment.newInstance(MainActivity.TAG.FIRST)
    private val secondFragment = ContainerFragment.newInstance(MainActivity.TAG.SECOND)
    private val thirdFragment = ContainerFragment.newInstance(MainActivity.TAG.THIRD)
    private val fourthFragment = ContainerFragment.newInstance(MainActivity.TAG.FOURTH)
    private val fifthFragment = ContainerFragment.newInstance(MainActivity.TAG.FIFTH)

    private val fragmentTagStack = Stack<TAG>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        fragmentTagStack.push(MainActivity.TAG.FIRST)
        supportFragmentManager.beginTransaction()
                .add(R.id.frame_container, firstFragment, MainActivity.TAG.FIRST.string)
                .commit()

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_first -> {
                    changeFragment(firstFragment , MainActivity.TAG.FIRST)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_second -> {
                    changeFragment(secondFragment , MainActivity.TAG.SECOND)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_third -> {
                    changeFragment(thirdFragment , MainActivity.TAG.THIRD)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_fourth ->{
                    changeFragment(fourthFragment, MainActivity.TAG.FOURTH)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_fifth ->{
                    changeFragment(fifthFragment, MainActivity.TAG.FIFTH)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
        //Reference https://gist.github.com/kooroshh/d8033f7ff4609b97372c71686471e18c
        BottomNavigationViewHelper.disableShiftMode(navigation)

        fab.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mBroadcastReceiver, IntentFilter(ChildFragment.ACTION))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mBroadcastReceiver)
    }

    private fun changeFragment(fragment: ContainerFragment , tag : MainActivity.TAG) {
        val lastFragmentTag = fragmentTagStack.peek()
        if(lastFragmentTag == tag) return

        fragmentTagStack.remove(tag)
        fragmentTagStack.push(tag)

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val lastFragment = supportFragmentManager.findFragmentByTag(lastFragmentTag.string) as ContainerFragment
        fragmentTransaction.hide(lastFragment)

        //if manager already added fragment, just show it
        if(fragment.isAdded) {
            fragmentTransaction
                    .show(fragment)
        }else{
            fragmentTransaction
                    .add(R.id.frame_container, fragment, tag.string)
        }

        fragmentTransaction.commit()
    }

    //using broadcast change child fragment
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras != null && intent.hasExtra(ChildFragment.PARAMS)) {
                val fragmentTag = intent.getStringExtra(ChildFragment.PARAMS)
                when(navigation.selectedItemId){
                    R.id.navigation_first -> {
                        firstFragment.showFragment(ChildFragment.newInstance(fragmentTag))
                    }
                    R.id.navigation_second -> {
                        secondFragment.showFragment(ChildFragment.newInstance(fragmentTag))
                    }
                    R.id.navigation_third -> {
                        thirdFragment.showFragment(ChildFragment.newInstance(fragmentTag))
                    }
                    R.id.navigation_fourth ->{
                        fourthFragment.showFragment(ChildFragment.newInstance(fragmentTag))
                    }
                    R.id.navigation_fifth ->{
                        fifthFragment.showFragment(ChildFragment.newInstance(fragmentTag))
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onBackPressed() {

        if(fragmentTagStack.empty()){
            super.onBackPressed()
        }else {
            //get current showing fragment
            val currentFragmentTag = fragmentTagStack.peek()
            val showingFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag.string) as ContainerFragment?
            if (showingFragment != null) {
                //if container still have child , just pop child fragment , otherwise remove container and show last container
                if (showingFragment.getChildFragmentCount() > 0) {
                    showingFragment.popFragment()
                } else {
                    fragmentTagStack.pop()
                    if (fragmentTagStack.empty()) {
                        super.onBackPressed()
                    }else {
                        val lastFragmentTag = fragmentTagStack.peek()
                        val lastFragment = supportFragmentManager.findFragmentByTag(lastFragmentTag.string) as ContainerFragment?
                        supportFragmentManager.beginTransaction().remove(showingFragment).show(lastFragment).commitNow()

                        navigation.selectedItemId = when(lastFragmentTag){
                            MainActivity.TAG.FIRST -> R.id.navigation_first
                            MainActivity.TAG.SECOND -> R.id.navigation_second
                            MainActivity.TAG.THIRD -> R.id.navigation_third
                            MainActivity.TAG.FOURTH -> R.id.navigation_fourth
                            MainActivity.TAG.FIFTH -> R.id.navigation_fifth
                            else -> throw Exception("navigation do not support this tag")
                        }

                    }
                }
            } else {
                super.onBackPressed()
            }
        }
    }
}
