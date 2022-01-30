package com.example.cryptoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.cryptoapp.adapters.CoinInfoAdapter
import com.example.cryptoapp.databinding.ActivityCoinDetailBinding
import com.example.cryptoapp.databinding.ActivityCoinPriceListBinding
import com.example.cryptoapp.viewModel.CoinViewModel
import java.text.DateFormatSymbols

class CoinDetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_FROM_SYMBOL = "fSyn"

        fun newIntent(context: Context, fromSymbol: String): Intent {
            val intent = Intent(context, CoinDetailActivity::class.java)
            intent.putExtra(EXTRA_FROM_SYMBOL, fromSymbol)
            return intent
        }
    }

    private lateinit var viewModel: CoinViewModel
    private lateinit var activityCoinDetailBinding: ActivityCoinDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)

        activityCoinDetailBinding = ActivityCoinDetailBinding.inflate(layoutInflater)
        val view = activityCoinDetailBinding.root
        setContentView(view)

        if (!intent.hasExtra(EXTRA_FROM_SYMBOL)) {
            finish()
            return
        }
        val fromSymbols: String = intent.getStringExtra(EXTRA_FROM_SYMBOL).toString()

        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        viewModel.getDetailInfo(fromSymbols).observe(this) {
            with(activityCoinDetailBinding) {
                tvCoinType.text =
                    String.format(
                        CoinInfoAdapter.getSymbolsTemplate(this@CoinDetailActivity),
                        it.fromSymbol,
                        it.toSymbol
                    )
                tvCoinPrice.text = it.price.toString()
                tvMinValuePerDay.text = it.lowDay.toString()
                tvMaxValuePerDay.text = it.highDay.toString()
                tvLastDeal.text = it.lastMarket
                tvLastUpdate.text = it.getFormattedTime()
                Glide.with(this@CoinDetailActivity).load(it.getFullImageUrl()).into(ivCoinLogo)
            }
        }
    }
}
