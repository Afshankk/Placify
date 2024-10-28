package com.example.myplacements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlacementMaterialAdapter(private var contributions: List<Contribution>) :
    RecyclerView.Adapter<PlacementMaterialAdapter.ContributionViewHolder>() {

    inner class ContributionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyNameTextView: TextView = itemView.findViewById(R.id.companyNameTextView)
        val positionTextView: TextView = itemView.findViewById(R.id.positionTextView)
        val dropdownButton: Button = itemView.findViewById(R.id.dropdownButton)
        val detailsLayout: LinearLayout = itemView.findViewById(R.id.detailsLayout)
        val interviewProcessTextView: TextView = itemView.findViewById(R.id.interviewProcessTextView)
        val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contribution, parent, false)
        return ContributionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContributionViewHolder, position: Int) {
        val contribution = contributions[position]

        holder.companyNameTextView.text = contribution.companyName
        holder.positionTextView.text = contribution.position

        // Set detailed info
        holder.interviewProcessTextView.text = contribution.interviewProcess
        holder.linkTextView.text = contribution.link.takeIf { it.isNotEmpty() } ?: "No link provided"
        holder.nameTextView.text = contribution.name

        // Handle dropdown toggle
        holder.dropdownButton.setOnClickListener {
            if (holder.detailsLayout.visibility == View.GONE) {
                holder.detailsLayout.visibility = View.VISIBLE
                holder.dropdownButton.text = "Hide Details"
            } else {
                holder.detailsLayout.visibility = View.GONE
                holder.dropdownButton.text = "View Details"
            }
        }
    }

    override fun getItemCount() = contributions.size

    // Method to update the list for search filtering
    fun updateList(filteredContributions: List<Contribution>) {
        contributions = filteredContributions
        notifyDataSetChanged()
    }
}
