package abdulrahman.ali19.ui

import abdulrahman.ali19.R
import abdulrahman.ali19.databinding.ActivityMainBinding
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.fragmentsContainer)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.splashFragment,
                R.id.homeFragment,
                R.id.reportFragment,
                R.id.notificationFragment,
                R.id.accountFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val isTopScreen = appBarConfiguration.topLevelDestinations.contains(destination.id)

        if (isTopScreen)
            if (destination.id == R.id.splashFragment) hideAll() else showBottomNavAndTopbars()
        else
            binding.bottomNavBar.visibility = GONE
    }

    private fun hideAll() {
        binding.appbar.visibility = GONE
        binding.bottomNavBar.visibility = GONE
    }

    private fun showBottomNavAndTopbars() {
        binding.appbar.visibility = VISIBLE
        binding.bottomNavBar.visibility = VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}