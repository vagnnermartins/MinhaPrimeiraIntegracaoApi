package com.example.minhaprimeiraapi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minhaprimeiraapi.databinding.ActivityNewItemBinding
import com.example.minhaprimeiraapi.model.ItemValue
import com.example.minhaprimeiraapi.service.Result
import com.example.minhaprimeiraapi.service.RetrofitClient
import com.example.minhaprimeiraapi.service.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.util.Date

class NewItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.saveCta.setOnClickListener {
            save()
        }
    }

    private fun save() {
        if (!validateForm()) return
        CoroutineScope(Dispatchers.IO).launch {
            val id = SecureRandom().nextInt().toString()
            val itemValue = ItemValue(
                id,
                binding.name.text.toString(),
                binding.surname.text.toString(),
                binding.address.text.toString(),
                binding.imageUrl.text.toString(),
                binding.age.text.toString().toInt(),
                location = null,
                Date()
            )
            val result = safeApiCall { RetrofitClient.apiService.addItem(itemValue) }
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@NewItemActivity,
                            R.string.error_create,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Success -> {
                        Toast.makeText(
                            this@NewItemActivity,
                            getString(R.string.success_create, result.data.id),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.name.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Name"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.surname.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Surname"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.age.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Age"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.imageUrl.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Image Url"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.address.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Address"), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {

        fun newIntent(context: Context) =
            Intent(context, NewItemActivity::class.java)
    }
}