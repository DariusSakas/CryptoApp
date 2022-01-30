package com.example.cryptoapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.cryptoapp.api.ApiFactory
import com.example.cryptoapp.database.AppDatabase
import com.example.cryptoapp.pojo.CoinPriceInfo
import com.example.cryptoapp.pojo.CoinPriceInfoRawData
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CoinViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    init {
        loadData()
    }

    val priceList = db.coinPriceInfoDao().getPriceList()

    private fun loadData() {
        val disposable = ApiFactory.apiService.getTopCoinsInfo(limit = 10)
            .map { it.data?.map { it.coinInfo?.name }?.joinToString(",").toString() }
            .flatMap { ApiFactory.apiService.getFullPriceList(fromSymbols = it) }
            .map { getPriceListFromRawData(it) }
            .delaySubscription(5, TimeUnit.SECONDS)
            .repeat() //will repeat info update till there is connection
            .retry() //will try to restart update while no connection
            .subscribeOn(Schedulers.io())

            .subscribe({
                Log.d("SUCCESSFULLY_PARSED", "Successfully loaded: $it")
                db.coinPriceInfoDao().insertPriceList(it)
            }, {
                Log.d("ERROR_LOADING", it.message.toString())
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getDetailInfo(fSym: String): LiveData<CoinPriceInfo> {
        return db.coinPriceInfoDao().getPriceInfoAboutCoin(fSym)
    }

    private fun getPriceListFromRawData(coinPriceInfoRawData: CoinPriceInfoRawData)
            : List<CoinPriceInfo> {
        val result = ArrayList<CoinPriceInfo>()
        val jsonObject = coinPriceInfoRawData.CoinPriceInfoJSONObject ?: return result
        val coinKeySet = jsonObject.keySet()

        for (coin in coinKeySet) {
            val currencyJson = jsonObject.getAsJsonObject(coin)
            val currencyKeySet = currencyJson.keySet()
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinPriceInfo::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }
}
