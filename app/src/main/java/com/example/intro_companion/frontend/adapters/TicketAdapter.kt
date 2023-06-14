package com.example.intro_companion.frontend.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.intro_companion.backend.entities.Ticket
import com.example.intro_companion.databinding.ListItemTicketBinding
import java.util.UUID

class TicketAdapter(
    private val active: List<Ticket>,
    private val onTicketClicked: (id: UUID) -> Unit
) : RecyclerView.Adapter<TicketAdapter.ActiveTicketHolder>() {

    /**
     * Represents a view holder for a single item in a RecyclerView list of tickets.
     * @param binding The binding object for the list_item_ticket.xml layout file.
     */
    inner class ActiveTicketHolder(private val binding: ListItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ticket: Ticket, onTicketClicked: (id: UUID) -> Unit) {
            binding.ticketDateLabel.text = ticket.submittedDate.toString()
            binding.descriptionLabel.text = ticket.classification

            binding.root.setOnClickListener {
                onTicketClicked(ticket.id)
            }
        }
    }

    /**
     * Creates a new [ActiveTicketHolder] instance and inflates the layout for a single item view
     * in the RecyclerView.
     *
     * @param parent The parent ViewGroup into which the new view will be added after it is bound to an adapter position.
     * @param viewType The view type of the new view.
     * @return A new instance of [ActiveTicketHolder] that holds a reference to the inflated [ListItemTicketBinding] object for the single item view.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActiveTicketHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTicketBinding.inflate(inflater, parent, false)

        return ActiveTicketHolder(binding)
    }

    /**
     * Binds the data at the specified position to the view holder.
     *
     * @param holder The view holder to bind the data to.
     * @param position The position of the item in the data set.
     */
    override fun onBindViewHolder(holder: ActiveTicketHolder, position: Int) {
        val ticket = active[position]
        holder.bind(ticket, onTicketClicked)
    }

    /**
     * Returns the total number of items in the data set held by the [Adapter].
     * @return The total number of items in the data set.
     */
    override fun getItemCount() = active.size
}