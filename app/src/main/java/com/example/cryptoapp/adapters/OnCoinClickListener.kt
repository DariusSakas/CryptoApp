package com.example.cryptoapp.adapters

import com.example.cryptoapp.pojo.CoinPriceInfo

interface OnCoinClickListener {
    fun onCoinClick(coinPriceInfo: CoinPriceInfo)
}
