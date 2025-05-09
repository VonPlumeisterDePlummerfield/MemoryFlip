// ScoreAdapter.kt
package com.example.memflip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScoresAdapter(private val scoresList: List<PlayerScore>) : RecyclerView.Adapter<ScoresAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.player_name)
        val playerPoints: TextView = itemView.findViewById(R.id.player_points)
        val playerMoves: TextView = itemView.findViewById(R.id.player_moves)
        val playerTime: TextView = itemView.findViewById(R.id.player_time)
        // Add other views as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val score = scoresList[position]
        holder.playerName.text = "Name: ${score.name}"
        holder.playerPoints.text = "Points: ${score.points}"
        holder.playerMoves.text = "Moves taken: ${score.moves}"
        holder.playerTime.text = "Total time: ${score.time}"
    }

    override fun getItemCount(): Int {
        return scoresList.size
    }
}

