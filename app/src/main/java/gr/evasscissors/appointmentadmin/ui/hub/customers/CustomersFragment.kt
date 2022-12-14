package gr.evasscissors.appointmentadmin.ui.hub.customers


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import gr.evasscissors.appointmentadmin.R
import gr.evasscissors.appointmentadmin.data.MERGE_NAV_KEY
import gr.evasscissors.appointmentadmin.data.NEW_USER_NAV_KEY
import gr.evasscissors.appointmentadmin.data.USER_NAV_KEY
import gr.evasscissors.appointmentadmin.data.User
import gr.evasscissors.appointmentadmin.databinding.FragmentCustomersBinding
import kotlinx.coroutines.flow.collectLatest

class CustomersFragment : Fragment() {

    private lateinit var binding: FragmentCustomersBinding
    private val viewModel: CustomersViewModel by viewModels()

    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchUsersFromDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customers, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val autocompleteAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line
        )


        userAdapter = UserAdapter(this@CustomersFragment)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CustomersFragment.viewModel
            fragmentCustomers = this@CustomersFragment
            usersRv.adapter = userAdapter
            searchText.apply {
                setAdapter(autocompleteAdapter)
                doOnTextChanged { text, _, _, _ ->
                    this@CustomersFragment.viewModel.filterUsersByName(text!!)

                    autocompleteAdapter.clear()
                    val namesList =
                        this@CustomersFragment.viewModel.usersForDisplay.value
                            ?.map { "${it.firstname ?: ""} ${it.lastname ?: ""} (${it.nickname})" }
                    namesList?.forEach { name ->
                        autocompleteAdapter.add(name)
                    }
                }
                setOnItemClickListener { _, _, position, _ ->
                    val name = autocompleteAdapter.getItem(position)
                    val index = name?.indexOf('(')?.minus(1)
                    setText(name?.subSequence(0, index!!))
                }
            }
        }

        observeForUserDeletion()
        observeForUserModifications()
        observeForNewUser()
        observeForMergeResult()
    }

    fun navigate(user: User) {
        if (viewModel.mergeMode) {
            navigateToMergeConflicts(user)
        } else {
            navigateToCustomerProfile(user)
        }

    }

    private fun observeForUserModifications() {
        findNavController().currentBackStackEntry?.savedStateHandle?.let { savedStateHandle ->
            savedStateHandle.getLiveData<User>(USER_NAV_KEY)
                .observe(viewLifecycleOwner) { updatedUser ->
                    viewModel.updateUser(updatedUser)
                    savedStateHandle.remove<User>(USER_NAV_KEY)
                }
        }
    }

    private fun observeForNewUser() {
        findNavController().currentBackStackEntry?.savedStateHandle?.let { savedStateHandle ->
            savedStateHandle.getLiveData<User>(
                NEW_USER_NAV_KEY
            ).observe(viewLifecycleOwner) { newUser ->
                viewModel.insertUser(newUser)
                savedStateHandle.remove<User>(NEW_USER_NAV_KEY)
            }
        }
    }

    private fun observeForMergeResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.let { savedStateHandle ->
            savedStateHandle.getLiveData<User?>(
                MERGE_NAV_KEY
            ).observe(viewLifecycleOwner) { mergedUser ->
                deactivateMergeMode()

                mergedUser?.let {
                    viewModel.updateUser(mergedUser)
                    viewModel.deleteUser(viewModel.getUserToBeMerged())
                }

                savedStateHandle.remove<User?>(MERGE_NAV_KEY)
            }
        }
    }

    private fun observeForUserDeletion() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userDeleted.collectLatest { deleted ->
                val text =
                    if (deleted) getString(R.string.delete_success) else getString(R.string.delete_fail)

                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun navigateToCreateNewCustomer() {
        findNavController().navigate(R.id.action_customersFragment_to_createCustomerFragment)
    }

    private fun navigateToMergeConflicts(user: User) {
        viewModel.setUserToBeMergedWith(user)

        val action = CustomersFragmentDirections.actionCustomersFragmentToMergeConflictsFragment(
            viewModel.getUserToBeMerged(),
            user
        )
        findNavController().navigate(action)
    }

    private fun navigateToCustomerProfile(user: User) {
        val action =
            CustomersFragmentDirections.actionCustomersFragmentToCustomerProfileFragment(user)
        findNavController().navigate(action)
    }

    fun activateMergeMode(user: User) {
        binding.apply {
            filterBtn.visibility = View.GONE
            mergeHint.visibility = View.VISIBLE
            backBtn.visibility = View.VISIBLE
        }
        viewModel.setUserToBeMerged(user)
        viewModel.mergeMode = true

        showRegisteredCustomers()
    }

    fun deleteUser(user: User) {
        viewModel.deleteUser(user)
    }

    fun deactivateMergeMode() {
        binding.apply {
            filterBtn.visibility = View.VISIBLE
            mergeHint.visibility = View.GONE
            backBtn.visibility = View.GONE
        }
        viewModel.mergeMode = false

        showAllCustomers()
    }

    fun toggleFilters() {
        if (binding.filters.visibility == View.GONE) {
            if (binding.searchText.visibility == View.VISIBLE) toggleSearch()

            binding.apply {
                filterBtn.animate().rotation(-90f).start()
                filters.visibility = View.VISIBLE
                filters.animate().alpha(1f).start()
            }
        } else {
            binding.apply {
                filterBtn.animate().rotation(0f).start()
                val hideAnimation =
                    AnimationUtils.loadAnimation(filters.context, R.anim.filter_btn_hide_anim)
                hideAnimation.apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {}

                        override fun onAnimationEnd(p0: Animation?) {
                            filters.apply {
                                visibility = View.GONE
                                alpha = 0f
                            }
                        }

                        override fun onAnimationRepeat(p0: Animation?) {}
                    })
                }
                filters.startAnimation(hideAnimation)
            }
        }
    }

    fun showRegisteredCustomers() {
        binding.apply {
            showAllFilter.setCardBackgroundColor(
                resources.getColor(
                    android.R.color.transparent,
                    requireActivity().theme
                )
            )
            showUnregisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    android.R.color.transparent,
                    requireActivity().theme
                )
            )
            showRegisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.md_theme_dark_primaryContainer,
                    requireActivity().theme
                )
            )
        }
        viewModel.filterUsers(registered = true)
    }

    fun showUnregisteredCustomers() {
        binding.apply {
            showAllFilter.setCardBackgroundColor(
                resources.getColor(
                    android.R.color.transparent,
                    requireActivity().theme
                )
            )
            showUnregisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.md_theme_dark_primaryContainer,
                    requireActivity().theme
                )
            )
            showRegisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    android.R.color.transparent,
                    requireActivity().theme
                )
            )
        }
        viewModel.filterUsers(registered = false)
    }

    fun showAllCustomers() {
        binding.apply {
            showAllFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.md_theme_dark_primaryContainer,
                    requireActivity().theme
                )
            )
            showRegisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    android.R.color.transparent,
                    requireActivity().theme
                )
            )
            showUnregisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    android.R.color.transparent,
                    requireActivity().theme
                )
            )
        }
        viewModel.resetFilter()
    }

    fun toggleSearch() {
        binding.apply {
            if (filters.visibility == View.VISIBLE) toggleFilters()
            mergeHint.visibility = View.GONE

            searchText.apply {
                if (visibility == View.GONE) {
                    visibility = View.VISIBLE
                    animate().alpha(1f).duration = 300
                } else {
                    animate().alpha(0f).apply {
                        duration = 300
                        withEndAction {
                            visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}