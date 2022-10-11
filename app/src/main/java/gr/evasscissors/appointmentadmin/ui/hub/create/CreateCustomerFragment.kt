package gr.evasscissors.appointmentadmin.ui.hub.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import gr.evasscissors.appointmentadmin.R
import gr.evasscissors.appointmentadmin.data.Gender
import gr.evasscissors.appointmentadmin.data.NEW_USER_NAV_KEY
import gr.evasscissors.appointmentadmin.databinding.FragmentCreateCustomerBinding

class CreateCustomerFragment : Fragment() {

    private val viewModel: CreateCustomerViewModel by viewModels()
    private lateinit var binding: FragmentCreateCustomerBinding

    private lateinit var backPressedCallback: OnBackPressedCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_customer, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CreateCustomerFragment.viewModel
            customerProfileEditFragment = this@CreateCustomerFragment
        }

        backPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (viewModel.isModified()) {
                    showWarningMessage()
                } else {
                    closeFragment()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

        binding.creationGenderGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.creation_gender_female -> viewModel.setGender(Gender.FEMALE)
                    R.id.creation_gender_male -> viewModel.setGender(Gender.MALE)
                    R.id.creation_gender_any -> viewModel.setGender(Gender.ANY)
                }
            }
        }

        observeStoredState()
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.isEnabled = false
    }

    fun saveCustomer() {
        if (isValid() && hasGender()) {
            binding.createCustomerProgress.show()

            viewModel.createUser()
        }
    }

    fun closeFragment() {
        findNavController().navigateUp()
    }

    fun pressBack() {
        requireActivity().onBackPressed()
    }

    private fun isValid(): Boolean {
        return if (viewModel.firstname.value.isNullOrBlank() && viewModel.lastname.value.isNullOrBlank()) {
            binding.apply {
                firstname.error = getString(R.string.one_field_requited_err_msg)
                lastname.error = getString(R.string.one_field_requited_err_msg)
            }
            false
        } else {
            true
        }
    }

    private fun hasGender(): Boolean {
        binding.apply {
            return if (creationGenderGroup.checkedButtonId == -1) {
                creationGenderGroup.background =
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.error_stroke,
                        requireActivity().theme
                    )
                genderErrMsg.visibility = View.VISIBLE
                false
            } else {
                creationGenderGroup.background?.alpha = 0
                genderErrMsg.visibility = View.GONE
                true
            }
        }
    }

    private fun observeStoredState() {
        viewModel.userStored.observe(viewLifecycleOwner) { stored ->
            binding.createCustomerProgress.hide()

            val text: String
            val color: Int

            if (stored) {
                text = getString(R.string.user_creation_success)
                color = R.color.md_theme_dark_secondary
                informForNewUser()
            } else {
                text = getString(R.string.user_creation_err)
                color = R.color.md_theme_dark_errorContainer
            }

            Snackbar.make(binding.saveInfoBtn, text, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setBackgroundTint(resources.getColor(color, requireActivity().theme))
                .setAction("Ok") {
                    if (stored) closeFragment()
                }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (stored) closeFragment()
                    }
                })
                .show()
        }
    }

    private fun informForNewUser() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            NEW_USER_NAV_KEY,
            viewModel.getUser()
        )
    }

    private fun showWarningMessage() {
        activity?.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.apply {
                setTitle(getString(R.string.unsaved_changes_warning_title))
                setMessage(getString(R.string.unsaved_changes_warning_msg))
                setPositiveButton(getString(R.string.dialog_yes_btn)) { dialog, id ->
                    closeFragment()
                }
                setNegativeButton(getString(R.string.dialog_no_btn)) { dialog, id ->

                }
            }
                .create()
        }?.show()
    }

}