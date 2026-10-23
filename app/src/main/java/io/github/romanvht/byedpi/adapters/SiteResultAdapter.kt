package io.github.romanvht.byedpi.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.romanvht.byedpi.R
import io.github.romanvht.byedpi.data.DomainList
import io.github.romanvht.byedpi.data.SiteGroup
import io.github.romanvht.byedpi.data.SiteResult
import io.github.romanvht.byedpi.data.buildSiteGroups

/**
 * Список сайтов в раскрытой карточке стратегии. При пустом снимке списков —
 * плоский список (обратная совместимость со старыми результатами), иначе
 * заголовки групп «Имя — X/Y (Z%)» и сайты каждой группы под своим заголовком.
 */
class SiteResultAdapter(private val noListName: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private sealed class Element {
        class Header(val group: SiteGroup) : Element()
        class Site(val result: SiteResult) : Element()
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SITE = 1
    }

    private val sites = mutableListOf<SiteResult>()
    private var elements: List<Element> = emptyList()

    class SiteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val siteTextView: TextView = view.findViewById(R.id.siteTextView)
        val resultTextView: TextView = view.findViewById(R.id.siteResultTextView)
    }

    class GroupHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupTextView: TextView = view.findViewById(R.id.groupTextView)
        val groupStatsTextView: TextView = view.findViewById(R.id.groupStatsTextView)
    }

    override fun getItemViewType(position: Int): Int =
        if (elements.isEmpty()) TYPE_SITE
        else if (elements[position] is Element.Header) TYPE_HEADER
        else TYPE_SITE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            GroupHeaderViewHolder(inflater.inflate(R.layout.item_site_group_header, parent, false))
        } else {
            SiteViewHolder(inflater.inflate(R.layout.item_site_result, parent, false))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupHeaderViewHolder) {
            val group = (elements[position] as Element.Header).group
            holder.groupTextView.text = group.name
            holder.groupStatsTextView.text =
                "${group.successCount}/${group.totalCount} (${group.successPercentage}%)"
        } else if (holder is SiteViewHolder) {
            val site = if (elements.isEmpty()) sites[position]
            else (elements[position] as Element.Site).result
            holder.siteTextView.text = site.site
            holder.resultTextView.text = "${site.successCount}/${site.totalCount}"
        }
    }

    override fun getItemCount(): Int =
        if (elements.isEmpty()) sites.size else elements.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateSites(newSites: List<SiteResult>, groups: List<DomainList>) {
        sites.clear()
        sites.addAll(newSites)
        elements = if (groups.isEmpty()) emptyList()
        else buildSiteGroups(newSites, groups, noListName).flatMap { group ->
            listOf<Element>(Element.Header(group)) + group.siteResults.map { Element.Site(it) }
        }
        notifyDataSetChanged()
    }
}
