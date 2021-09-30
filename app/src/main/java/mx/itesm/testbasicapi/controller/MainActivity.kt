package mx.itesm.testbasicapi.controller

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import mx.itesm.testbasicapi.R

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var welcomeFragment: WelcomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureViews()
        assignClickListener()
        showWelcomeFragment()
    }

    private fun showWelcomeFragment() {
        welcomeFragment = WelcomeFragment.newInstance("Michael Scott")
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_content, welcomeFragment)
            .commit()
    }

    private fun configureViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun assignClickListener() {
        navigationView.setNavigationItemSelectedListener { item ->
            var fragmentToShow: Fragment = welcomeFragment
            when (item.itemId) {
                R.id.mnu_products -> fragmentToShow = ProductsFragment()
                R.id.mnu_shop -> fragmentToShow = ShopFragment()
                else -> {
                    Snackbar.make(
                        drawerLayout,
                        "Add more fragments and edit MainActivity to show them",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_content, fragmentToShow)
                .commit()
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }


    override fun onBackPressed() {
        // If drawer is open, close it
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
        // Exit the app
            super.onBackPressed()
    }
}