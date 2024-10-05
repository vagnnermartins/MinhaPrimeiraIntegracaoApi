package com.example.minhaprimeiraapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minhaprimeiraapi.databinding.ActivityMainBinding
import com.example.minhaprimeiraapi.service.Result
import com.example.minhaprimeiraapi.service.RetrofitClient
import com.example.minhaprimeiraapi.service.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        fetchItems()
    }

    private fun fetchItems() {
        // Alterando execução para IO thread
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getItems() }

            // Alterando execução para Main thread
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {}
                    is Result.Success -> {}
                }
            }
        }
    }

    private fun setupView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
