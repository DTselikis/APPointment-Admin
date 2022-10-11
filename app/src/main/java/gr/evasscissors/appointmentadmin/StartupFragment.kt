package gr.evasscissors.appointmentadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import gr.evasscissors.appointmentadmin.databinding.FragmentStartupBinding

class StartupFragment : Fragment() {

    private lateinit var binding: FragmentStartupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_startup, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startupLogo.animate().scaleYBy(10f).scaleXBy(10f).alpha(0f).apply {
            duration = 600
            withEndAction {
                findNavController().navigate(R.id.action_startupFragment_to_loginFragment)
            }
        }
    }
}