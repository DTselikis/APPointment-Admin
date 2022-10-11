package gr.evasscissors.appointmentadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import gr.evasscissors.appointmentadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}