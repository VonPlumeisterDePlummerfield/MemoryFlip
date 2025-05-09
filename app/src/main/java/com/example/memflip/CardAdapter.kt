package com.example.memflip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val cards: List<Card>,
    private val onCardClick: (Card) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.card_image)

        fun bind(card: Card) {
            if (card.isMatched) {
                itemView.visibility = View.INVISIBLE // Hide matched cards
            } else {
                itemView.visibility = View.VISIBLE // Show unmatched cards
                imageView.setImageResource(
                    if (card.isFaceUp) card.imageResId else R.drawable.card_back
                )
            }

            itemView.setOnClickListener {
                onCardClick(card)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size
}
