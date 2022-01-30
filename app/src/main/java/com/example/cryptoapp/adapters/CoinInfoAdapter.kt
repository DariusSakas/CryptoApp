package com.example.cryptoapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptoapp.R
import com.example.cryptoapp.databinding.ItemCoinInfoBinding
import com.example.cryptoapp.pojo.CoinPriceInfo

class CoinInfoAdapter(private val context: Context) : RecyclerView.Adapter<CoinInfoAdapter.CoinInfoViewHolder>() {

    private lateinit var itemCoinInfoBinding: ItemCoinInfoBinding

    companion object {
        fun getLastUpdateTemplate(context: Context): String {
            return context.resources.getString(R.string.last_update)
        }

        fun getSymbolsTemplate(context: Context): String {
            return context.resources.getString(R.string.symbols_template)
        }
    }

    var coinInfoList: List<CoinPriceInfo> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onCoinClickListener: OnCoinClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coin_info, parent, false)
        itemCoinInfoBinding = ItemCoinInfoBinding.bind(view)
        return CoinInfoViewHolder(itemCoinInfoBinding)
    }

    override fun onBindViewHolder(holder: CoinInfoViewHolder, position: Int) {
        val coin = coinInfoList[position]
        with(holder) {
            with(coin) {
                tvSymbols.text = String.format(getSymbolsTemplate(this@CoinInfoAdapter.context), fromSymbol, toSymbol)
                tvCoinPrice.text = price.toString()
                tvLastUpdate.text =
                    String.format(getLastUpdateTemplate(this@CoinInfoAdapter.context), getFormattedTime())
                Glide.with(itemView.context).load(coin.getFullImageUrl()).into(holder.ivLogoCoin)
                itemView.setOnClickListener {
                    onCoinClickListener?.onCoinClick(this)
                }
            }
        }
    }

    override fun getItemCount() = coinInfoList.size

    inner class CoinInfoViewHolder(binding: ItemCoinInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val ivLogoCoin = itemCoinInfoBinding.ivCoinIcon
        val tvSymbols = itemCoinInfoBinding.tvSymbols
        val tvCoinPrice = itemCoinInfoBinding.tvCoinPrice
        val tvLastUpdate = itemCoinInfoBinding.tvLastUpdate
    }
}
