package example.testproject.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import example.testproject.fragments.MainFragment
import example.testproject.fragments.TableFragment

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val namesOfTabs = arrayListOf("Главная", "Таблица")

    override fun getPageTitle(position: Int): CharSequence? {
        return namesOfTabs[position]
    }

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> MainFragment()
            1 -> TableFragment()
            else -> MainFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}